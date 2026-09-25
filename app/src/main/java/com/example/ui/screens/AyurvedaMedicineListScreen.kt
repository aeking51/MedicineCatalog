package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.AyurvedaMedicine
import com.example.data.model.DoshaType
import com.example.data.model.FormulationCategory
import com.example.ui.AyurvedaMedicineViewModel
import com.example.ui.MedicineSearchScope
import com.example.ui.MedicineUiState
import com.example.ui.theme.NaturalBackground
import com.example.ui.theme.NaturalCardBorder
import com.example.ui.theme.NaturalCardSurface
import com.example.ui.theme.NaturalEarthGold
import com.example.ui.theme.NaturalHydrationBlue
import com.example.ui.theme.NaturalKaphaGold
import com.example.ui.theme.NaturalMossDark
import com.example.ui.theme.NaturalMossPrimary
import com.example.ui.theme.NaturalOliveMuted
import com.example.ui.theme.NaturalParchmentBorder
import com.example.ui.theme.NaturalParchmentContainer
import com.example.ui.theme.NaturalPittaGreen
import com.example.ui.theme.NaturalSageBorder
import com.example.ui.theme.NaturalSageContainer
import com.example.ui.theme.NaturalTerracotta
import com.example.ui.theme.NaturalTextHeading
import com.example.ui.theme.NaturalTextPrimary
import com.example.ui.theme.NaturalVataViolet

/**
 * Jetpack Compose screen that displays a list of Ayurveda medicines using
 * the AyurvedaMedicineViewModel and Firestore data, featuring a custom card view
 * highlighting the medicine name, botanical details, therapeutic benefits,
 * ingredients, dosage instructions, and dispensary stock.
 */
@Composable
fun AyurvedaMedicineListScreen(
    viewModel: AyurvedaMedicineViewModel = viewModel(),
    onSelectMedicine: ((AyurvedaMedicine) -> Unit)? = null,
    onNavigateBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AyurvedaMedicineListContent(
        uiState = uiState,
        onSearchQueryChange = { viewModel.setSearchQuery(it) },
        onSearchScopeChange = { viewModel.setSearchScope(it) },
        onQuickHerbSelect = { herb ->
            viewModel.filterByIngredient(herb)
        },
        onCategorySelect = { viewModel.setCategoryFilter(it) },
        onDoshaSelect = { viewModel.setDoshaFilter(it) },
        onRefresh = { viewModel.fetchMedicines(forceRemote = true) },
        onSelectMedicine = { med ->
            if (onSelectMedicine != null) {
                onSelectMedicine(med)
            } else {
                viewModel.selectMedicine(med)
            }
        },
        onDismissDetail = { viewModel.selectMedicine(null) },
        onClearMessages = { viewModel.clearMessages() },
        onClearSearch = { viewModel.clearSearch() },
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AyurvedaMedicineListContent(
    uiState: MedicineUiState,
    onSearchQueryChange: (String) -> Unit,
    onSearchScopeChange: (MedicineSearchScope) -> Unit,
    onQuickHerbSelect: (String) -> Unit,
    onCategorySelect: (FormulationCategory?) -> Unit,
    onDoshaSelect: (DoshaType?) -> Unit,
    onRefresh: () -> Unit,
    onSelectMedicine: (AyurvedaMedicine) -> Unit,
    onDismissDetail: () -> Unit,
    onClearMessages: () -> Unit,
    onClearSearch: () -> Unit,
    onNavigateBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NaturalBackground)
            .testTag("ayurveda_medicine_list_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header Bar with Cloud Firestore Badge
            MedicineListHeader(
                medicineCount = uiState.filteredMedicines.size,
                totalCount = uiState.medicines.size,
                isLoading = uiState.isLoading,
                onRefresh = onRefresh,
                onNavigateBack = onNavigateBack
            )

            // Search Bar with dynamic placeholder reflecting searchScope
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 4.dp)
                    .testTag("medicine_search_input"),
                placeholder = {
                    Text(
                        text = when (uiState.searchScope) {
                            MedicineSearchScope.NAME -> "Filter by formulation or Sanskrit name..."
                            MedicineSearchScope.INGREDIENT -> "Filter by herb / ingredient (e.g. Ashwagandha)..."
                            MedicineSearchScope.ALL -> "Search by formulation name or ingredients..."
                        },
                        fontSize = 12.5.sp,
                        color = NaturalOliveMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = NaturalMossPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotBlank()) {
                        IconButton(
                            onClick = { onSearchQueryChange("") },
                            modifier = Modifier.testTag("clear_search_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = NaturalOliveMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = NaturalCardSurface,
                    unfocusedContainerColor = NaturalCardSurface,
                    focusedBorderColor = NaturalMossPrimary,
                    unfocusedBorderColor = NaturalCardBorder
                )
            )

            // Search Scope Selector (All Fields / By Name / By Ingredient)
            SearchScopeRow(
                selectedScope = uiState.searchScope,
                onScopeSelect = onSearchScopeChange
            )

            // Popular Herbal Ingredients Quick-Chips
            PopularHerbsRow(
                currentQuery = uiState.searchQuery,
                onSelectHerb = onQuickHerbSelect
            )

            // Active Filter Summary / Results Counter when filtered
            if (uiState.searchQuery.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Filtered by ${uiState.searchScope.displayName}: \"${uiState.searchQuery}\" (${uiState.filteredMedicines.size} found)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NaturalMossDark
                    )
                    Text(
                        text = "Clear search",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = NaturalTerracotta,
                        modifier = Modifier
                            .clickable { onClearSearch() }
                            .padding(4.dp)
                            .testTag("text_clear_search")
                    )
                }
            }

            // Dosha Filter Chips
            DoshaFilterRow(
                selectedDosha = uiState.selectedDosha,
                onDoshaSelect = onDoshaSelect
            )

            // Category Filter Chips
            CategoryFilterRow(
                selectedCategory = uiState.selectedCategory,
                onCategorySelect = onCategorySelect
            )

            // Dynamic progress bar when syncing with Firestore
            AnimatedVisibility(visible = uiState.isLoading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp),
                    color = NaturalMossPrimary,
                    trackColor = NaturalSageContainer
                )
            }

            // Notification banners
            uiState.errorMessage?.let { errorMsg ->
                BannerMessage(
                    message = errorMsg,
                    isError = true,
                    onDismiss = onClearMessages
                )
            }

            uiState.successMessage?.let { successMsg ->
                BannerMessage(
                    message = successMsg,
                    isError = false,
                    onDismiss = onClearMessages
                )
            }

            // Main Medicine Cards List
            if (uiState.filteredMedicines.isEmpty() && !uiState.isLoading) {
                EmptyMedicineState(
                    searchQuery = uiState.searchQuery,
                    searchScope = uiState.searchScope,
                    onReset = {
                        onClearSearch()
                        onCategorySelect(null)
                        onDoshaSelect(null)
                    },
                    onSwitchToAll = {
                        onSearchScopeChange(MedicineSearchScope.ALL)
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("medicine_lazy_column"),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(
                        items = uiState.filteredMedicines,
                        key = { it.id }
                    ) { medicine ->
                        AyurvedaMedicineCard(
                            medicine = medicine,
                            searchQuery = uiState.searchQuery,
                            searchScope = uiState.searchScope,
                            onClick = { onSelectMedicine(medicine) }
                        )
                    }
                }
            }
        }

        // Selected Medicine Detail Screen (Full Window)
        uiState.selectedMedicine?.let { medicine ->
            AyurvedaMedicineDetailScreen(
                medicine = medicine,
                onNavigateBack = onDismissDetail
            )
        }
    }
}

/**
 * Clean Top Bar Header with Title, Firestore Sync status, and Refresh Button
 */
@Composable
private fun MedicineListHeader(
    medicineCount: Int,
    totalCount: Int,
    isLoading: Boolean,
    onRefresh: () -> Unit,
    onNavigateBack: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (onNavigateBack != null) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = NaturalTextHeading
                    )
                }
            }

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MedicalServices,
                        contentDescription = null,
                        tint = NaturalMossPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Ayurveda Medicine Registry",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalTextHeading,
                        fontFamily = FontFamily.Serif
                    )
                }
                Text(
                    text = "Cloud Firestore • $medicineCount formulations available",
                    fontSize = 11.sp,
                    color = NaturalOliveMuted
                )
            }
        }

        // Firestore sync button with pulse badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = NaturalSageContainer,
                border = androidx.compose.foundation.BorderStroke(1.dp, NaturalSageBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(if (isLoading) NaturalEarthGold else Color(0xFF2E7D32))
                    )
                    Text(
                        text = if (isLoading) "Syncing" else "Firestore Live",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NaturalMossDark
                    )
                }
            }

            IconButton(
                onClick = onRefresh,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("refresh_medicines_button")
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = NaturalMossPrimary
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh from Firestore",
                        tint = NaturalMossPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

/**
 * Dedicated Card View for each Ayurveda Medicine.
 * Prominently presents:
 *  1. Medicine Name & Sanskrit/Botanical Subtitle
 *  2. Formulation Category & Dosha Affinity Badges
 *  3. Therapeutic Benefits Section with styled bullet items
 *  4. Ingredients & Botanical overview
 *  5. Dosage Instructions Preview
 *  6. Stock availability
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AyurvedaMedicineCard(
    medicine: AyurvedaMedicine,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    searchQuery: String = "",
    searchScope: MedicineSearchScope = MedicineSearchScope.ALL
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .testTag("medicine_card_${medicine.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = NaturalCardSurface
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 6.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NaturalCardBorder, RoundedCornerShape(18.dp))
                .padding(18.dp)
        ) {
            // Top Row: Category tag, Dosha Pill, and Stock Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Pill
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NaturalParchmentContainer,
                        border = androidx.compose.foundation.BorderStroke(1.dp, NaturalParchmentBorder)
                    ) {
                        Text(
                            text = medicine.category.displayName.uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaturalTerracotta,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                        )
                    }

                    // Dosha Impact Tag
                    DoshaTagPill(doshas = medicine.targetDoshas, impactText = medicine.doshaImpact)
                }
            }

            // Matching Herb / Ingredient or Name badge when searching
            if (searchQuery.isNotBlank()) {
                val trimmedQuery = searchQuery.trim()
                val matchedHerb = medicine.ingredients.firstOrNull { ing ->
                    ing.name.contains(trimmedQuery, ignoreCase = true) ||
                        ing.botanicalName.contains(trimmedQuery, ignoreCase = true) ||
                        ing.sanskritName.contains(trimmedQuery, ignoreCase = true)
                }

                if (matchedHerb != null && (searchScope == MedicineSearchScope.INGREDIENT || searchScope == MedicineSearchScope.ALL)) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NaturalMossPrimary.copy(alpha = 0.10f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NaturalMossPrimary.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .testTag("matched_herb_badge_${medicine.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Eco,
                                contentDescription = null,
                                tint = NaturalMossPrimary,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Contains: ${matchedHerb.name}" + if (matchedHerb.botanicalName.isNotBlank()) " (${matchedHerb.botanicalName})" else "",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NaturalMossDark,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                } else if (searchScope == MedicineSearchScope.NAME &&
                    (medicine.name.contains(trimmedQuery, ignoreCase = true) || medicine.sanskritName.contains(trimmedQuery, ignoreCase = true))) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NaturalEarthGold.copy(alpha = 0.12f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NaturalEarthGold.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                            .testTag("matched_name_badge_${medicine.id}")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = NaturalEarthGold,
                                modifier = Modifier.size(13.dp)
                            )
                            Text(
                                text = "Name match: ${medicine.name}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NaturalTerracotta,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 1. Medicine Name & Sanskrit Subtitle
            Text(
                text = medicine.name,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold,
                color = NaturalTextHeading,
                fontFamily = FontFamily.Serif,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            if (medicine.sanskritName.isNotBlank()) {
                Text(
                    text = medicine.sanskritName,
                    fontSize = 13.sp,
                    fontStyle = FontStyle.Italic,
                    color = NaturalOliveMuted,
                    modifier = Modifier.padding(top = 1.dp)
                )
            }

            // Short Description
            if (medicine.shortDescription.isNotBlank()) {
                Text(
                    text = medicine.shortDescription,
                    fontSize = 12.sp,
                    color = NaturalTextPrimary,
                    lineHeight = 17.sp,
                    modifier = Modifier.padding(top = 6.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. THERAPEUTIC BENEFITS SECTION (Explicitly highlighted as requested)
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = NaturalBackground,
                border = androidx.compose.foundation.BorderStroke(1.dp, NaturalCardBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Spa,
                            contentDescription = null,
                            tint = NaturalMossPrimary,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "KEY THERAPEUTIC BENEFITS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaturalMossPrimary,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    val benefitsList = medicine.effectiveBenefits
                    if (benefitsList.isNotEmpty()) {
                        benefitsList.take(3).forEach { benefit ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "•",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NaturalEarthGold
                                )
                                Text(
                                    text = benefit,
                                    fontSize = 12.5.sp,
                                    color = NaturalTextPrimary,
                                    lineHeight = 16.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    } else if (medicine.primaryBenefit.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = "•", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = NaturalEarthGold)
                            Text(
                                text = medicine.primaryBenefit,
                                fontSize = 12.5.sp,
                                color = NaturalTextPrimary
                            )
                        }
                    } else {
                        Text(
                            text = "Promotes holistic vitality, tissue rejuvenation, and doshic equilibrium.",
                            fontSize = 12.sp,
                            color = NaturalOliveMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Ingredients Chips
            if (medicine.ingredients.isNotEmpty()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = NaturalOliveMuted,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "Ingredients:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NaturalOliveMuted
                    )
                    Text(
                        text = medicine.ingredients.take(3).joinToString(", ") { it.name } +
                                if (medicine.ingredients.size > 3) " +${medicine.ingredients.size - 3} more" else "",
                        fontSize = 11.sp,
                        color = NaturalTextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
            }

            // 4. Dosage Instructions Bar
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = NaturalSageContainer.copy(alpha = 0.5f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = NaturalMossDark,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = medicine.effectiveDosageInstructions,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = NaturalMossDark,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Tap prompt footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tap to view full botanical details →",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NaturalMossPrimary
                )
            }
        }
    }
}

/**
 * Dosha Tag Pill with color accents for Vata, Pitta, Kapha, or Tridoshic
 */
@Composable
private fun DoshaTagPill(
    doshas: List<DoshaType>,
    impactText: String
) {
    val isTridoshic = doshas.contains(DoshaType.TRIDOSHIC) || doshas.size >= 3
    val label = when {
        isTridoshic -> "Tridoshic"
        doshas.contains(DoshaType.VATA) && doshas.contains(DoshaType.PITTA) -> "Vata-Pitta"
        doshas.contains(DoshaType.VATA) && doshas.contains(DoshaType.KAPHA) -> "Vata-Kapha"
        doshas.contains(DoshaType.PITTA) && doshas.contains(DoshaType.KAPHA) -> "Pitta-Kapha"
        doshas.contains(DoshaType.VATA) -> "Vata"
        doshas.contains(DoshaType.PITTA) -> "Pitta"
        doshas.contains(DoshaType.KAPHA) -> "Kapha"
        impactText.isNotBlank() -> impactText
        else -> "Tridoshic"
    }

    val chipColor = when {
        doshas.contains(DoshaType.VATA) -> NaturalVataViolet.copy(alpha = 0.15f)
        doshas.contains(DoshaType.PITTA) -> NaturalPittaGreen.copy(alpha = 0.2f)
        doshas.contains(DoshaType.KAPHA) -> NaturalKaphaGold.copy(alpha = 0.2f)
        else -> NaturalSageContainer
    }

    val textColor = when {
        doshas.contains(DoshaType.VATA) -> Color(0xFF6A4C6D)
        doshas.contains(DoshaType.PITTA) -> Color(0xFF386641)
        doshas.contains(DoshaType.KAPHA) -> Color(0xFF8A5A2B)
        else -> NaturalMossDark
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = chipColor
    ) {
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = textColor,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        )
    }
}

/**
 * Filter row for selecting Dosha types
 */
@Composable
private fun DoshaFilterRow(
    selectedDosha: DoshaType?,
    onDoshaSelect: (DoshaType?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val doshaOptions = listOf<Pair<String, DoshaType?>>(
            "All Doshas" to null,
            "Tridoshic" to DoshaType.TRIDOSHIC,
            "Vata" to DoshaType.VATA,
            "Pitta" to DoshaType.PITTA,
            "Kapha" to DoshaType.KAPHA
        )

        doshaOptions.forEach { (title, dosha) ->
            val isSelected = selectedDosha == dosha
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) NaturalMossPrimary else NaturalCardSurface)
                    .border(1.dp, if (isSelected) NaturalMossPrimary else NaturalCardBorder, RoundedCornerShape(16.dp))
                    .clickable { onDoshaSelect(dosha) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("filter_dosha_${title.lowercase()}")
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else NaturalTextPrimary
                )
            }
        }
    }
}

/**
 * Filter row for selecting classical formulation categories
 */
@Composable
private fun CategoryFilterRow(
    selectedCategory: FormulationCategory?,
    onCategorySelect: (FormulationCategory?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // "All Categories" chip
        val isAllSelected = selectedCategory == null
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(if (isAllSelected) NaturalEarthGold else NaturalCardSurface)
                .border(1.dp, if (isAllSelected) NaturalEarthGold else NaturalCardBorder, RoundedCornerShape(16.dp))
                .clickable { onCategorySelect(null) }
                .padding(horizontal = 12.dp, vertical = 6.dp)
                .testTag("filter_cat_all")
        ) {
            Text(
                text = "All Formulations",
                fontSize = 11.sp,
                fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isAllSelected) Color.White else NaturalTextPrimary
            )
        }

        FormulationCategory.values().filter { it != FormulationCategory.ALL }.forEach { cat ->
            val isSelected = selectedCategory == cat
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) NaturalEarthGold else NaturalCardSurface)
                    .border(1.dp, if (isSelected) NaturalEarthGold else NaturalCardBorder, RoundedCornerShape(16.dp))
                    .clickable { onCategorySelect(cat) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("filter_cat_${cat.name}")
            ) {
                Text(
                    text = cat.displayName,
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else NaturalTextPrimary
                )
            }
        }
    }
}

/**
 * Filter scope selector allowing switching between: All Fields, By Name, and By Ingredients
 */
@Composable
private fun SearchScopeRow(
    selectedScope: MedicineSearchScope,
    onScopeSelect: (MedicineSearchScope) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 2.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Scope:",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = NaturalOliveMuted
        )

        MedicineSearchScope.values().forEach { scope ->
            val isSelected = selectedScope == scope
            val testTag = when (scope) {
                MedicineSearchScope.ALL -> "search_scope_all"
                MedicineSearchScope.NAME -> "search_scope_name"
                MedicineSearchScope.INGREDIENT -> "search_scope_ingredient"
            }
            val iconEmoji = when (scope) {
                MedicineSearchScope.ALL -> "✦"
                MedicineSearchScope.NAME -> "🏷️"
                MedicineSearchScope.INGREDIENT -> "🌿"
            }
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = if (isSelected) NaturalMossPrimary else NaturalCardSurface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) NaturalMossPrimary else NaturalCardBorder
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onScopeSelect(scope) }
                    .testTag(testTag)
            ) {
                Text(
                    text = "$iconEmoji ${scope.displayName}",
                    fontSize = 11.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else NaturalTextPrimary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}

/**
 * Quick popular herb shortcuts to immediately filter formulations containing specific ingredients
 */
@Composable
private fun PopularHerbsRow(
    currentQuery: String,
    onSelectHerb: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 3.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Herbs:",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = NaturalOliveMuted
        )

        val popularHerbs = listOf(
            "Ashwagandha",
            "Amalaki",
            "Haritaki",
            "Brahmi",
            "Guduchi",
            "Guggulu",
            "Shatavari",
            "Arjuna",
            "Tulsi",
            "Triphala"
        )

        popularHerbs.forEach { herb ->
            val isSelected = currentQuery.equals(herb, ignoreCase = true)
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = if (isSelected) NaturalSageContainer else NaturalCardSurface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) NaturalMossPrimary else NaturalSageBorder
                ),
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable {
                        if (isSelected) {
                            onSelectHerb("")
                        } else {
                            onSelectHerb(herb)
                        }
                    }
                    .testTag("quick_herb_${herb.lowercase()}")
            ) {
                Text(
                    text = "🌿 $herb",
                    fontSize = 10.5.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) NaturalMossDark else NaturalTextPrimary,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

/**
 * Empty search or filter results display
 */
@Composable
private fun EmptyMedicineState(
    searchQuery: String,
    searchScope: MedicineSearchScope,
    onReset: () -> Unit,
    onSwitchToAll: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = NaturalSageContainer,
            modifier = Modifier.size(64.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = null,
                    tint = NaturalMossPrimary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No Formulations Found",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = NaturalTextHeading,
            fontFamily = FontFamily.Serif
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = if (searchQuery.isNotBlank())
                "No medicines matched \"$searchQuery\" in ${searchScope.displayName}. Try checking ingredients, name spelling, or searching across all attributes."
            else
                "No medicines found for the selected category and dosha filters.",
            fontSize = 13.sp,
            color = NaturalOliveMuted,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            lineHeight = 18.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (searchQuery.isNotBlank() && searchScope != MedicineSearchScope.ALL) {
                OutlinedButton(
                    onClick = onSwitchToAll,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = NaturalMossPrimary
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, NaturalMossPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("empty_search_all_button")
                ) {
                    Text(text = "Search in All Fields", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            OutlinedButton(
                onClick = onReset,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = NaturalTerracotta
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, NaturalTerracotta),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("empty_reset_filters_button")
            ) {
                Text(text = "Reset All Filters", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

/**
 * Message banner for errors and sync confirmations
 */
@Composable
private fun BannerMessage(
    message: String,
    isError: Boolean,
    onDismiss: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = if (isError) Color(0xFFFDECEA) else NaturalSageContainer,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isError) Color(0xFFF5C6CB) else NaturalSageBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (isError) Icons.Default.Info else Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (isError) Color(0xFFD32F2F) else Color(0xFF2E7D32),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = message,
                    fontSize = 12.sp,
                    color = if (isError) Color(0xFF721C24) else NaturalMossDark
                )
            }
            IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Dismiss",
                    tint = NaturalOliveMuted,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}
