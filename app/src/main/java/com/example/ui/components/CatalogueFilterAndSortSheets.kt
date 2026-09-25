package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.RestartAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DoshaType
import com.example.data.model.FormulationCategory
import com.example.ui.screens.CatalogueSortOrder
import com.example.ui.theme.NaturalBackground
import com.example.ui.theme.NaturalCardBorder
import com.example.ui.theme.NaturalCardSurface
import com.example.ui.theme.NaturalEarthGold
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

/**
 * Bottom Sheet presenting all Sorting options for the classical catalogue.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogueSortBottomSheet(
    currentSortOrder: CatalogueSortOrder,
    onSortOrderSelected: (CatalogueSortOrder) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = NaturalCardSurface,
        scrimColor = Color.Black.copy(alpha = 0.45f),
        dragHandle = {
            Surface(
                modifier = Modifier.padding(top = 12.dp, bottom = 6.dp),
                color = NaturalCardBorder,
                shape = CircleShape
            ) {
                Box(modifier = Modifier.size(width = 38.dp, height = 4.dp))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 8.dp)
        ) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ORDER & ARRANGE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = NaturalEarthGold
                    )
                    Text(
                        text = "Sort Formulations",
                        fontFamily = FontFamily.Serif,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalTextHeading
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(NaturalSageContainer.copy(alpha = 0.6f))
                        .testTag("sort_sheet_close_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = NaturalMossDark,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sort Options List
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CatalogueSortOrder.values().forEach { order ->
                    val isSelected = currentSortOrder == order
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                onSortOrderSelected(order)
                                onDismiss()
                            }
                            .testTag("sort_option_${order.name}"),
                        color = if (isSelected) NaturalSageContainer else NaturalBackground,
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) NaturalMossPrimary else NaturalCardBorder
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = order.displayName,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) NaturalMossDark else NaturalTextPrimary
                                )
                                if (order.subtitle.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = order.subtitle,
                                        fontSize = 11.sp,
                                        color = NaturalOliveMuted
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) NaturalMossPrimary else Color.Transparent
                                    )
                                    .border(
                                        width = 1.5.dp,
                                        color = if (isSelected) NaturalMossPrimary else NaturalOliveMuted.copy(alpha = 0.4f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * Bottom Sheet presenting rich filtering options for category, dosha, stock, and botanical herbs.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CatalogueFilterBottomSheet(
    selectedCategory: FormulationCategory,
    onCategorySelected: (FormulationCategory) -> Unit,
    selectedDosha: DoshaType?,
    onDoshaSelected: (DoshaType?) -> Unit,
    selectedHerbFilter: String?,
    onHerbFilterSelected: (String?) -> Unit,
    totalResultsCount: Int,
    categoryCounts: Map<FormulationCategory, Int>,
    onResetAllFilters: () -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = NaturalCardSurface,
        scrimColor = Color.Black.copy(alpha = 0.45f),
        dragHandle = {
            Surface(
                modifier = Modifier.padding(top = 12.dp, bottom = 6.dp),
                color = NaturalCardBorder,
                shape = CircleShape
            ) {
                Box(modifier = Modifier.size(width = 38.dp, height = 4.dp))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
        ) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "THERAPEUTIC CRITERIA",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = NaturalEarthGold
                    )
                    Text(
                        text = "Filter Formulations",
                        fontFamily = FontFamily.Serif,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalTextHeading
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Reset All",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalTerracotta,
                        modifier = Modifier
                            .clickable { onResetAllFilters() }
                            .padding(8.dp)
                            .testTag("filter_sheet_reset_btn")
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(NaturalSageContainer.copy(alpha = 0.6f))
                            .testTag("filter_sheet_close_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = NaturalMossDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(top = 8.dp),
                thickness = 1.dp,
                color = NaturalCardBorder
            )

            // Scrollable Filter Sections
            Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 14.dp)
            ) {
                // Section 1: Classical Formulation Category
                Text(
                    text = "CLASSICAL FORMULATION CLASS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = NaturalOliveMuted
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FormulationCategory.primaryCategories.forEach { cat ->
                        val isSelected = selectedCategory == cat
                        val count = categoryCounts[cat] ?: 0
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onCategorySelected(cat) }
                                .testTag("filter_sheet_cat_${cat.name}"),
                            color = if (isSelected) NaturalMossPrimary else NaturalBackground,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) NaturalMossPrimary else NaturalCardBorder
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = cat.displayName,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else NaturalTextPrimary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(
                                            if (isSelected) Color.White.copy(alpha = 0.25f) else NaturalSageContainer
                                        )
                                        .padding(horizontal = 6.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = count.toString(),
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else NaturalMossDark
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 2: Dosha Affinity
                Text(
                    text = "DOSHA AFFINITY & TARGET",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    color = NaturalOliveMuted
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // All Doshas option
                    val isAllSelected = selectedDosha == null
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onDoshaSelected(null) }
                            .testTag("filter_sheet_dosha_all"),
                        color = if (isAllSelected) NaturalSageContainer else NaturalBackground,
                        border = BorderStroke(
                            1.dp,
                            if (isAllSelected) NaturalMossPrimary else NaturalCardBorder
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "All Doshas",
                            fontSize = 11.5.sp,
                            fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isAllSelected) NaturalMossDark else NaturalTextPrimary,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                        )
                    }

                    DoshaType.values().forEach { dosha ->
                        val isSelected = selectedDosha == dosha
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onDoshaSelected(dosha) }
                                .testTag("filter_sheet_dosha_${dosha.name}"),
                            color = if (isSelected) NaturalSageContainer else NaturalBackground,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) NaturalMossPrimary else NaturalCardBorder
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "${dosha.symbol} ${dosha.displayName}",
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) NaturalMossDark else NaturalTextPrimary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Section 3: Botanical Herb Spotlight
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "HERBAL COMPONENT SPOTLIGHT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        color = NaturalOliveMuted
                    )
                    if (selectedHerbFilter != null) {
                        Text(
                            text = "Clear Herb",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaturalTerracotta,
                            modifier = Modifier.clickable { onHerbFilterSelected(null) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                val herbs = listOf(
                    "Ashwagandha", "Triphala", "Guggulu", "Guduchi",
                    "Dashamula", "Brahmi", "Shatavari", "Neem", "Haridra", "Tulsi", "Amalaki"
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    herbs.forEach { herb ->
                        val isSelected = selectedHerbFilter == herb
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable {
                                    onHerbFilterSelected(if (isSelected) null else herb)
                                }
                                .testTag("filter_sheet_herb_$herb"),
                            color = if (isSelected) NaturalEarthGold.copy(alpha = 0.2f) else NaturalBackground,
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) NaturalEarthGold else NaturalCardBorder
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "🌱 $herb",
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) NaturalEarthGold else NaturalTextPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            HorizontalDivider(thickness = 1.dp, color = NaturalCardBorder)

            // Bottom Action Bar: Match summary & Apply button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "MATCHING RESULTS",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalOliveMuted,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "$totalResultsCount formulations",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalTextHeading
                    )
                }

                Button(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NaturalMossPrimary),
                    modifier = Modifier
                        .height(44.dp)
                        .testTag("filter_sheet_apply_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Apply Filters",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}
