package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AyurvedaIngredient
import com.example.data.model.AyurvedaMedicine
import com.example.data.model.DosageInfo
import com.example.data.model.DoshaType
import com.example.data.model.DravyagunaProfile
import com.example.data.model.FormulationCategory
import com.example.data.repository.AyurvedaMedicineRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Search scope criteria to filter medicines by name, ingredients, or across all fields.
 */
enum class MedicineSearchScope(val displayName: String, val shortLabel: String, val hintText: String) {
    ALL("All Fields", "All", "Search by herb, name, or therapeutic benefit..."),
    NAME("By Name", "Name", "Search specifically by medicine name or Sanskrit title..."),
    INGREDIENT("By Ingredient", "Ingredients", "Search specifically by herb/ingredient (e.g. Ashwagandha, Amla)...")
}

/**
 * UI State for Ayurveda Medicine operations (fetching, saving, filtering).
 */
data class MedicineUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val medicines: List<AyurvedaMedicine> = emptyList(),
    val filteredMedicines: List<AyurvedaMedicine> = emptyList(),
    val selectedMedicine: AyurvedaMedicine? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val searchQuery: String = "",
    val searchScope: MedicineSearchScope = MedicineSearchScope.ALL,
    val selectedCategory: FormulationCategory? = null,
    val selectedDosha: DoshaType? = null
)

/**
 * ViewModel adhering to Room and Clean Data Architecture guidelines.
 * Handles fetching, caching, saving, and querying Ayurveda medicine data with Firestore and Room.
 */
class AyurvedaMedicineViewModel(
    application: Application,
    private val repository: AyurvedaMedicineRepository = AyurvedaMedicineRepository.getInstance(application)
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(MedicineUiState())
    val uiState: StateFlow<MedicineUiState> = _uiState.asStateFlow()

    init {
        observeLocalDatabase()
        repository.startRealtimeFirestoreSync()
        fetchMedicines(forceRemote = false)
    }

    /**
     * Observes the Room local database reactive stream (Flow<List<AyurvedaMedicine>>).
     * Automatically keeps UI state synchronized whenever Room updates from Firestore.
     */
    private fun observeLocalDatabase() {
        viewModelScope.launch {
            repository.allMedicines.collectLatest { list ->
                _uiState.update { current ->
                    current.copy(
                        medicines = list,
                        filteredMedicines = applyFilters(
                            medicines = list,
                            query = current.searchQuery,
                            searchScope = current.searchScope,
                            category = current.selectedCategory,
                            dosha = current.selectedDosha
                        )
                    )
                }
            }
        }
    }

    /**
     * Fetches medicine data from Cloud Firestore and syncs into Room database.
     */
    fun fetchMedicines(forceRemote: Boolean = true) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.fetchAndSyncFromFirestore()
            result.fold(
                onSuccess = { fetchedList ->
                    _uiState.update { current ->
                        current.copy(
                            isLoading = false,
                            medicines = fetchedList,
                            filteredMedicines = applyFilters(
                                medicines = fetchedList,
                                query = current.searchQuery,
                                searchScope = current.searchScope,
                                category = current.selectedCategory,
                                dosha = current.selectedDosha
                            ),
                            successMessage = if (forceRemote) "Synchronized with Firestore" else null
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update { current ->
                        current.copy(
                            isLoading = false,
                            errorMessage = "Firestore fetch failed: ${error.localizedMessage ?: "Unknown error"}"
                        )
                    }
                }
            )
        }
    }

    /**
     * Saves an Ayurveda medicine to Cloud Firestore and updates local Room database.
     */
    fun saveMedicine(
        medicine: AyurvedaMedicine,
        onComplete: ((Boolean) -> Unit)? = null
    ) {
        if (medicine.name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Medicine name is required.") }
            onComplete?.invoke(false)
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val result = repository.saveMedicine(medicine)
            result.fold(
                onSuccess = {
                    _uiState.update { current ->
                        current.copy(
                            isSaving = false,
                            successMessage = "Saved ${medicine.name} to Firestore and local registry."
                        )
                    }
                    onComplete?.invoke(true)
                },
                onFailure = { error ->
                    _uiState.update { current ->
                        current.copy(
                            isSaving = false,
                            errorMessage = "Failed to save to Firestore: ${error.localizedMessage ?: "Unknown error"}"
                        )
                    }
                    onComplete?.invoke(false)
                }
            )
        }
    }

    /**
     * Helper to construct and save an Ayurveda medicine directly from user-entered fields.
     */
    fun createAndSaveMedicine(
        name: String,
        ingredients: List<AyurvedaIngredient>,
        dosageInstructions: String,
        benefits: List<String>,
        sanskritName: String = "",
        category: FormulationCategory = FormulationCategory.CHURNA,
        shortDescription: String = "",
        primaryBenefit: String = "",
        targetDoshas: List<DoshaType> = listOf(DoshaType.TRIDOSHIC),
        stockUnits: Int = 50,
        batchNumber: String = "AYUR-${System.currentTimeMillis() % 10000}",
        onComplete: ((Boolean) -> Unit)? = null
    ) {
        val medicine = AyurvedaMedicine(
            id = "med_${System.currentTimeMillis()}",
            name = name.trim(),
            ingredients = ingredients,
            dosageInstructions = dosageInstructions.trim(),
            benefits = benefits,
            sanskritName = sanskritName.trim(),
            category = category,
            shortDescription = shortDescription.ifBlank { primaryBenefit },
            primaryBenefit = primaryBenefit.ifBlank { benefits.firstOrNull() ?: "" },
            targetDoshas = targetDoshas,
            stockUnits = stockUnits,
            batchNumber = batchNumber,
            isLowStock = stockUnits < 15
        )
        saveMedicine(medicine, onComplete)
    }

    /**
     * Deletes a medicine from Cloud Firestore and local Room database.
     */
    fun deleteMedicine(medicineId: String, onComplete: ((Boolean) -> Unit)? = null) {
        viewModelScope.launch {
            val result = repository.deleteMedicine(medicineId)
            result.fold(
                onSuccess = {
                    _uiState.update { current ->
                        current.copy(
                            selectedMedicine = if (current.selectedMedicine?.id == medicineId) null else current.selectedMedicine,
                            successMessage = "Medicine deleted from Firestore."
                        )
                    }
                    onComplete?.invoke(true)
                },
                onFailure = { error ->
                    _uiState.update { current ->
                        current.copy(
                            errorMessage = "Delete failed: ${error.localizedMessage ?: "Unknown error"}"
                        )
                    }
                    onComplete?.invoke(false)
                }
            )
        }
    }

    /**
     * Updates medicine inventory stock in Firestore and Room.
     */
    fun updateStock(medicineId: String, newStockUnits: Int) {
        viewModelScope.launch {
            repository.updateStock(medicineId, newStockUnits)
        }
    }

    /**
     * Sets the active detail view medicine.
     */
    fun selectMedicine(medicine: AyurvedaMedicine?) {
        _uiState.update { it.copy(selectedMedicine = medicine) }
    }

    /**
     * Search and filtering by name, ingredients, or across all attributes.
     */
    fun setSearchQuery(query: String) {
        _uiState.update { current ->
            current.copy(
                searchQuery = query,
                filteredMedicines = applyFilters(
                    medicines = current.medicines,
                    query = query,
                    searchScope = current.searchScope,
                    category = current.selectedCategory,
                    dosha = current.selectedDosha
                )
            )
        }
    }

    /**
     * Updates the search criteria scope (ALL, NAME, or INGREDIENT).
     */
    fun setSearchScope(scope: MedicineSearchScope) {
        _uiState.update { current ->
            current.copy(
                searchScope = scope,
                filteredMedicines = applyFilters(
                    medicines = current.medicines,
                    query = current.searchQuery,
                    searchScope = scope,
                    category = current.selectedCategory,
                    dosha = current.selectedDosha
                )
            )
        }
    }

    /**
     * Convenience method to directly filter specifically by medicine name.
     */
    fun filterByName(nameQuery: String) {
        _uiState.update { current ->
            current.copy(
                searchQuery = nameQuery,
                searchScope = MedicineSearchScope.NAME,
                filteredMedicines = applyFilters(
                    medicines = current.medicines,
                    query = nameQuery,
                    searchScope = MedicineSearchScope.NAME,
                    category = current.selectedCategory,
                    dosha = current.selectedDosha
                )
            )
        }
    }

    /**
     * Convenience method to directly filter specifically by ingredient / herb name.
     */
    fun filterByIngredient(ingredientQuery: String) {
        _uiState.update { current ->
            current.copy(
                searchQuery = ingredientQuery,
                searchScope = MedicineSearchScope.INGREDIENT,
                filteredMedicines = applyFilters(
                    medicines = current.medicines,
                    query = ingredientQuery,
                    searchScope = MedicineSearchScope.INGREDIENT,
                    category = current.selectedCategory,
                    dosha = current.selectedDosha
                )
            )
        }
    }

    /**
     * Resets the active search query and scope.
     */
    fun clearSearch() {
        _uiState.update { current ->
            current.copy(
                searchQuery = "",
                searchScope = MedicineSearchScope.ALL,
                filteredMedicines = applyFilters(
                    medicines = current.medicines,
                    query = "",
                    searchScope = MedicineSearchScope.ALL,
                    category = current.selectedCategory,
                    dosha = current.selectedDosha
                )
            )
        }
    }

    fun setCategoryFilter(category: FormulationCategory?) {
        _uiState.update { current ->
            current.copy(
                selectedCategory = category,
                filteredMedicines = applyFilters(
                    medicines = current.medicines,
                    query = current.searchQuery,
                    searchScope = current.searchScope,
                    category = category,
                    dosha = current.selectedDosha
                )
            )
        }
    }

    fun setDoshaFilter(dosha: DoshaType?) {
        _uiState.update { current ->
            current.copy(
                selectedDosha = dosha,
                filteredMedicines = applyFilters(
                    medicines = current.medicines,
                    query = current.searchQuery,
                    searchScope = current.searchScope,
                    category = current.selectedCategory,
                    dosha = dosha
                )
            )
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null, successMessage = null) }
    }

    private fun applyFilters(
        medicines: List<AyurvedaMedicine>,
        query: String,
        searchScope: MedicineSearchScope,
        category: FormulationCategory?,
        dosha: DoshaType?
    ): List<AyurvedaMedicine> {
        return filterMedicines(
            medicines = medicines,
            query = query,
            searchScope = searchScope,
            category = category,
            dosha = dosha
        )
    }

    companion object {
        /**
         * Pure filtering logic supporting scope-based search across Name, Ingredients, or All attributes.
         */
        fun filterMedicines(
            medicines: List<AyurvedaMedicine>,
            query: String,
            searchScope: MedicineSearchScope = MedicineSearchScope.ALL,
            category: FormulationCategory? = null,
            dosha: DoshaType? = null
        ): List<AyurvedaMedicine> {
            val trimmed = query.trim()
            return medicines.filter { med ->
                val matchesSearch = if (trimmed.isBlank()) true else {
                    when (searchScope) {
                        MedicineSearchScope.NAME -> {
                            med.name.contains(trimmed, ignoreCase = true) ||
                                med.sanskritName.contains(trimmed, ignoreCase = true)
                        }
                        MedicineSearchScope.INGREDIENT -> {
                            med.ingredients.any { ing ->
                                ing.name.contains(trimmed, ignoreCase = true) ||
                                    ing.botanicalName.contains(trimmed, ignoreCase = true) ||
                                    ing.sanskritName.contains(trimmed, ignoreCase = true) ||
                                    ing.classicalRole.contains(trimmed, ignoreCase = true)
                            } || med.constituents.any { it.contains(trimmed, ignoreCase = true) }
                        }
                        MedicineSearchScope.ALL -> {
                            med.name.contains(trimmed, ignoreCase = true) ||
                                med.sanskritName.contains(trimmed, ignoreCase = true) ||
                                med.ingredients.any { ing ->
                                    ing.name.contains(trimmed, ignoreCase = true) ||
                                        ing.botanicalName.contains(trimmed, ignoreCase = true) ||
                                        ing.sanskritName.contains(trimmed, ignoreCase = true)
                                } ||
                                med.constituents.any { it.contains(trimmed, ignoreCase = true) } ||
                                med.primaryBenefit.contains(trimmed, ignoreCase = true) ||
                                med.effectiveBenefits.any { it.contains(trimmed, ignoreCase = true) }
                        }
                    }
                }

                val matchesCategory = category == null || med.category == category
                val matchesDosha = dosha == null || med.targetDoshas.contains(dosha) || med.targetDoshas.contains(DoshaType.TRIDOSHIC)

                matchesSearch && matchesCategory && matchesDosha
            }
        }
    }
}
