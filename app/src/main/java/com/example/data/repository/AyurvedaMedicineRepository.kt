package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.data.model.AyurvedaMedicine
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Repository adhering to Clean Architecture with Supabase as Central Cloud Database.
 * Zero SQLite dependencies. Uses Supabase PostgreSQL as the single source of truth.
 */
class AyurvedaMedicineRepository(
    private val supabaseRepository: SupabaseRepository = SupabaseRepository,
    private val firestoreRepository: FirestoreRepository = FirestoreRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    companion object {
        private const val TAG = "AyurMedicineRepo"

        @Volatile
        private var INSTANCE: AyurvedaMedicineRepository? = null

        fun getInstance(context: Context? = null): AyurvedaMedicineRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = AyurvedaMedicineRepository()
                INSTANCE = instance
                instance
            }
        }
    }

    private val _medicines = MutableStateFlow<List<AyurvedaMedicine>>(AyurvedaRepository.allMedicines)

    /**
     * Reactive stream of medicines from central Supabase-backed in-memory state.
     * Updates automatically whenever remote Supabase sync writes new formulations.
     */
    val allMedicines: Flow<List<AyurvedaMedicine>> = _medicines.asStateFlow()

    /**
     * Observable medicine by ID.
     */
    fun getMedicineById(id: String): Flow<AyurvedaMedicine?> = _medicines
        .map { list -> list.find { it.id == id } }
        .flowOn(ioDispatcher)

    /**
     * Search medicines across all fields.
     */
    fun searchMedicines(query: String): Flow<List<AyurvedaMedicine>> = _medicines
        .map { list ->
            if (query.isBlank()) list
            else list.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.sanskritName.contains(query, ignoreCase = true) ||
                it.primaryBenefit.contains(query, ignoreCase = true) ||
                it.shortDescription.contains(query, ignoreCase = true) ||
                it.ingredients.any { ing -> ing.name.contains(query, ignoreCase = true) }
            }
        }
        .flowOn(ioDispatcher)

    /**
     * Search medicines by name.
     */
    fun searchMedicinesByName(query: String): Flow<List<AyurvedaMedicine>> = _medicines
        .map { list ->
            if (query.isBlank()) list
            else list.filter {
                it.name.contains(query, ignoreCase = true) ||
                it.sanskritName.contains(query, ignoreCase = true)
            }
        }
        .flowOn(ioDispatcher)

    /**
     * Search medicines by ingredient.
     */
    fun searchMedicinesByIngredient(query: String): Flow<List<AyurvedaMedicine>> = _medicines
        .map { list ->
            if (query.isBlank()) list
            else list.filter {
                it.ingredients.any { ing -> ing.name.contains(query, ignoreCase = true) }
            }
        }
        .flowOn(ioDispatcher)

    /**
     * Fetches medicines from Central Supabase Cloud database and updates state.
     */
    suspend fun fetchAndSyncFromFirestore(): Result<List<AyurvedaMedicine>> = withContext(ioDispatcher) {
        runCatching {
            // 1. Fetch from Supabase Central Cloud Database
            val supabaseMedicines = supabaseRepository.fetchMedicinesSuspend()
            if (supabaseMedicines.isNotEmpty()) {
                _medicines.update { supabaseMedicines }
                Log.d(TAG, "Successfully synced ${supabaseMedicines.size} medicines from Supabase Cloud.")
                return@runCatching supabaseMedicines
            }

            // 2. Secondary Cloud sync fallback
            val remoteMedicines = firestoreRepository.fetchMedicinesSuspend()
            if (remoteMedicines.isNotEmpty()) {
                _medicines.update { remoteMedicines }
                Log.d(TAG, "Successfully synced ${remoteMedicines.size} medicines from Cloud Firestore.")
                remoteMedicines
            } else {
                val current = _medicines.value
                if (current.isEmpty()) {
                    val defaultList = AyurvedaRepository.allMedicines
                    _medicines.update { defaultList }
                    defaultList
                } else {
                    current
                }
            }
        }.onFailure { e ->
            Log.e(TAG, "Error fetching from Supabase Cloud: ${e.message}", e)
        }
    }

    /**
     * Saves a medicine formulation directly to Supabase Cloud and updates reactive state.
     */
    suspend fun saveMedicine(medicine: AyurvedaMedicine): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            // 1. Immediately update reactive state
            _medicines.update { current ->
                val exists = current.any { it.id == medicine.id }
                if (exists) {
                    current.map { if (it.id == medicine.id) medicine else it }
                } else {
                    listOf(medicine) + current
                }
            }

            // 2. Persist to Central Supabase Cloud
            val supabaseSuccess = supabaseRepository.saveMedicineSuspend(medicine)
            if (supabaseSuccess) {
                Log.d(TAG, "Saved formulation ${medicine.name} (${medicine.id}) to Supabase.")
            } else {
                Log.w(TAG, "Supabase sync returned false.")
            }

            // Keep Firestore synchronized
            firestoreRepository.saveMedicineSuspend(medicine)
            Unit
        }.onFailure { e ->
            Log.e(TAG, "Failed to save medicine to Supabase: ${e.message}", e)
        }
    }

    /**
     * Deletes a medicine formulation from Supabase Cloud and updates reactive state.
     */
    suspend fun deleteMedicine(medicineId: String): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            // 1. Remove from reactive state
            _medicines.update { current -> current.filter { it.id != medicineId } }

            // 2. Delete from Supabase Cloud
            supabaseRepository.deleteMedicineSuspend(medicineId)

            // Delete from Firestore
            firestoreRepository.deleteMedicineSuspend(medicineId)
            Unit
        }.onFailure { e ->
            Log.e(TAG, "Failed to delete medicine: ${e.message}", e)
        }
    }

    /**
     * Updates inventory stock units in Supabase Cloud and reactive state.
     */
    suspend fun updateStock(medicineId: String, newStock: Int): Result<Unit> = withContext(ioDispatcher) {
        runCatching {
            val isLowStock = newStock < 15
            _medicines.update { current ->
                current.map {
                    if (it.id == medicineId) it.copy(stockUnits = newStock) else it
                }
            }
            firestoreRepository.updateMedicineStock(medicineId, newStock, isLowStock)
            Unit
        }
    }

    /**
     * Registers a real-time listener that keeps medicines updated in background.
     */
    fun startRealtimeFirestoreSync() {
        firestoreRepository.observeMedicines(
            onSuccess = { remoteList ->
                if (remoteList.isNotEmpty()) {
                    _medicines.update { remoteList }
                }
            },
            onError = { e ->
                Log.e(TAG, "Realtime sync listener notice: ${e.message}")
            }
        )
    }
}
