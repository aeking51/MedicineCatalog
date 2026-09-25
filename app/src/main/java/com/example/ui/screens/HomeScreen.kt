package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.ui.DailySpotlightHelper
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AyurvedaMedicine
import com.example.data.model.DoshaType
import com.example.data.model.FormulationCategory
import com.example.data.model.UserRole
import com.example.ui.AyurvedaUiState
import com.example.ui.components.GuestLimitedAccessBanner
import com.example.ui.theme.AyurTheme
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
import com.example.ui.theme.NaturalVataViolet

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    uiState: AyurvedaUiState,
    onSelectMedicine: (AyurvedaMedicine) -> Unit,
    onCategorySelected: (FormulationCategory) -> Unit = {},
    onNavigateToLibrary: () -> Unit = {},
    // Optional legacy callbacks maintained for API compatibility
    onLogVitalityDose: () -> Unit = {},
    onToggleHabit: (String) -> Unit = {},
    onToggleDose: (String) -> Unit = {},
    onAddHydration: (Float) -> Unit = {},
    onNavigateToInsights: () -> Unit = {},
    onPromptSignIn: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var currentDayOfYear by remember {
        mutableIntStateOf(java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR))
    }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val newDay = java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR)
                if (newDay != currentDayOfYear) {
                    currentDayOfYear = newDay
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val featuredMed = remember(uiState.allMedicines, uiState.dailyVitalityMedicine, currentDayOfYear) {
        if (uiState.allMedicines.isEmpty()) {
            uiState.dailyVitalityMedicine
        } else {
            DailySpotlightHelper.getTodaySpotlight(uiState.allMedicines)
        }
    }

    val theme = AyurTheme.colors

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isTablet = maxWidth >= 600.dp

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(if (theme.isGlass) Color.Transparent else NaturalBackground)
                .padding(horizontal = if (isTablet) 28.dp else 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Guest Access Limited Mode Notice
            if (uiState.currentUser.role == UserRole.GUEST) {
                item {
                    GuestLimitedAccessBanner(
                        onSignInClick = onPromptSignIn,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            // 1 & 2: Hero Section (Adaptive side-by-side on tablet, stacked on phone)
            if (isTablet) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(modifier = Modifier.weight(1f)) {
                            HeritageOverviewCard(allMedicinesCount = uiState.allMedicines.size)
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            SpotlightHeroCard(
                                featuredMed = featuredMed,
                                onSelectMedicine = onSelectMedicine
                            )
                        }
                    }
                }
            } else {
                item {
                    HeritageOverviewCard(allMedicinesCount = uiState.allMedicines.size)
                }
                item {
                    SpotlightHeroCard(
                        featuredMed = featuredMed,
                        onSelectMedicine = onSelectMedicine
                    )
                }
            }

            // 3. Classical Categories Showcase (From Handbook Structure)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = (if (theme.isGlass) {
                            Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x55000000))
                                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        } else Modifier).weight(1f, fill = false)
                    ) {
                        Text(
                            text = "EXPLORE BY CLASSICAL CATEGORY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.1.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = if (theme.isGlass) Color.White else NaturalMossDark,
                            style = if (theme.isGlass) {
                                LocalTextStyle.current.copy(
                                    shadow = Shadow(
                                        color = Color(0xFF000000),
                                        offset = Offset(0f, 1f),
                                        blurRadius = 4f
                                    )
                                )
                            } else LocalTextStyle.current
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        onClick = onNavigateToLibrary,
                        shape = RoundedCornerShape(10.dp),
                        color = if (theme.isGlass) Color(0xE6FFFFFF) else NaturalSageContainer,
                        border = BorderStroke(1.dp, if (theme.isGlass) Color.White else NaturalSageBorder),
                        modifier = Modifier.clip(RoundedCornerShape(10.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "Full Catalogue",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = NaturalMossDark,
                                maxLines = 1
                            )
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = NaturalMossDark,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                // Horizontal Carousel of Major Classical Categories
                val majorCategories: List<Pair<FormulationCategory, String>> = listOf(
                    FormulationCategory.ARISHTA to "🍷 Fermented Tonics",
                    FormulationCategory.KWATHA to "🍵 Classical Decoctions",
                    FormulationCategory.TAILA to "🌿 Medicated Tailams",
                    FormulationCategory.GHRITA to "🧈 Medicated Ghee",
                    FormulationCategory.CHURNA to "🌾 Herbal Choornams",
                    FormulationCategory.VATI to "💊 Classical Pills",
                    FormulationCategory.RASAYANA to "🍯 Confections"
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    items(majorCategories) { item ->
                        val cat = item.first
                        val desc = item.second
                        val count = uiState.allMedicines.count { it.category == cat }
                        Surface(
                            modifier = Modifier
                                .width(150.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, NaturalCardBorder, RoundedCornerShape(16.dp))
                                .clickable {
                                    onCategorySelected(cat)
                                    onNavigateToLibrary()
                                },
                            color = NaturalCardSurface
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = cat.displayName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NaturalTextHeading,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = desc,
                                    fontSize = 10.sp,
                                    color = NaturalOliveMuted,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "$count Items",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NaturalMossDark
                                    )
                                    Text(
                                        text = "Explore →",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = NaturalMossPrimary
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 4. Curated Classical Remedies
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = (if (theme.isGlass) {
                            Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x55000000))
                                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        } else Modifier).weight(1f, fill = false)
                    ) {
                        Text(
                            text = "FEATURED PHARMACOPEIA REMEDIES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.1.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = if (theme.isGlass) Color.White else NaturalMossDark,
                            style = if (theme.isGlass) {
                                LocalTextStyle.current.copy(
                                    shadow = Shadow(
                                        color = Color(0xFF000000),
                                        offset = Offset(0f, 1f),
                                        blurRadius = 4f
                                    )
                                )
                            } else LocalTextStyle.current
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = if (theme.isGlass) {
                            Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x55000000))
                                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        } else Modifier
                    ) {
                        Text(
                            text = "${uiState.allMedicines.size} Formulations",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            color = if (theme.isGlass) Color.White else NaturalMossDark,
                            style = if (theme.isGlass) {
                                LocalTextStyle.current.copy(
                                    shadow = Shadow(
                                        color = Color(0xFF000000),
                                        offset = Offset(0f, 1f),
                                        blurRadius = 4f
                                    )
                                )
                            } else LocalTextStyle.current
                        )
                    }
                }

                // Grid/List of Curated Formulations (2 columns on tablet, 1 column on phone)
                if (isTablet) {
                    uiState.allMedicines.take(6).chunked(2).forEach { rowMeds ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            rowMeds.forEach { med ->
                                Box(modifier = Modifier.weight(1f)) {
                                    CuratedRemedyCard(med = med, onSelectMedicine = onSelectMedicine)
                                }
                            }
                            if (rowMeds.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                } else {
                    uiState.allMedicines.take(6).forEach { med ->
                        CuratedRemedyCard(med = med, onSelectMedicine = onSelectMedicine)
                    }
                }
            }
        }

        // 5. Standards & Dispensary Quality Footer Card
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, NaturalCardBorder, RoundedCornerShape(20.dp)),
                color = NaturalCardSurface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(NaturalSageContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = NaturalMossPrimary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Ayurvedic Formulary of India (AFI)",
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaturalTextHeading
                        )
                        Text(
                            text = "All medicines prepared strictly as per classical texts with verified botanical parts and authentic Kerala processing.",
                            fontSize = 10.5.sp,
                            color = NaturalTextPrimary.copy(alpha = 0.85f),
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))
        }
    }
}
}

@Composable
private fun HeritageOverviewCard(
    allMedicinesCount: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, NaturalCardBorder, RoundedCornerShape(20.dp)),
        color = NaturalCardSurface
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.sitaram_luxury_logo),
                        contentDescription = "Sitaram Ayurveda Official Logo",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, NaturalEarthGold.copy(alpha = 0.7f), CircleShape)
                    )
                    Column {
                        Text(
                            text = "SITARAM AYURVEDA",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.4.sp,
                            color = NaturalEarthGold
                        )
                        Text(
                            text = "Therapeutic Index",
                            fontFamily = FontFamily.Serif,
                            fontSize = 19.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaturalTextHeading
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(NaturalSageContainer)
                        .border(1.dp, NaturalSageBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 9.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = "AFI Standard",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalMossDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Authentic classical medicine handbook based on Charaka Samhita, Ashtanga Hridaya, and Sahasrayogam standards.",
                fontSize = 11.5.sp,
                color = NaturalTextPrimary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Stats Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(NaturalBackground)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$allMedicinesCount",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalMossPrimary
                    )
                    Text(
                        text = "Formulations",
                        fontSize = 9.5.sp,
                        color = NaturalOliveMuted
                    )
                }

                Box(modifier = Modifier.width(1.dp).height(24.dp).background(NaturalCardBorder))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${FormulationCategory.primaryCategories.size - 1}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalEarthGold
                    )
                    Text(
                        text = "Categories",
                        fontSize = 9.5.sp,
                        color = NaturalOliveMuted
                    )
                }

                Box(modifier = Modifier.width(1.dp).height(24.dp).background(NaturalCardBorder))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "100%",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalPittaGreen
                    )
                    Text(
                        text = "AFI Standard",
                        fontSize = 9.5.sp,
                        color = NaturalOliveMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun SpotlightHeroCard(
    featuredMed: AyurvedaMedicine,
    onSelectMedicine: (AyurvedaMedicine) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, NaturalParchmentBorder, RoundedCornerShape(24.dp))
            .clickable { onSelectMedicine(featuredMed) }
            .testTag("featured_spotlight_card"),
        color = NaturalParchmentContainer,
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header Row: Top Bar with Subtitle Tag and Category Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✦ FORMULATION SPOTLIGHT",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.1.sp,
                    color = NaturalTerracotta
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(NaturalParchmentBorder.copy(alpha = 0.5f))
                        .border(1.dp, NaturalParchmentBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = featuredMed.category.displayName,
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalMossDark,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Medicine Name & Classical Sanskrit Title (100% full width, unconstrained)
            Column(modifier = Modifier.fillMaxWidth()) {
                if (featuredMed.sanskritName.isNotBlank()) {
                    Text(
                        text = featuredMed.sanskritName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalEarthGold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = featuredMed.name,
                    fontFamily = FontFamily.Serif,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaturalTextHeading,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 24.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = featuredMed.shortDescription,
                fontSize = 12.5.sp,
                color = NaturalTextPrimary.copy(alpha = 0.9f),
                lineHeight = 17.5.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Packing: ${featuredMed.effectivePacking}",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NaturalOliveMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .padding(end = 8.dp)
                )

                Button(
                    onClick = { onSelectMedicine(featuredMed) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NaturalMossPrimary),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(38.dp)
                        .testTag("hero_view_monograph_btn")
                ) {
                    Text(
                        text = "View Monograph",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun CuratedRemedyCard(
    med: AyurvedaMedicine,
    onSelectMedicine: (AyurvedaMedicine) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, NaturalCardBorder, RoundedCornerShape(18.dp))
            .clickable { onSelectMedicine(med) }
            .testTag("home_medicine_card_${med.id}"),
        color = NaturalCardSurface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = med.sanskritName,
                    fontSize = 9.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaturalEarthGold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = med.name,
                    fontFamily = FontFamily.Serif,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaturalTextHeading
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = med.primaryBenefit,
                    fontSize = 11.sp,
                    color = NaturalTextPrimary.copy(alpha = 0.85f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(horizontalAlignment = Alignment.End) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(NaturalSageContainer)
                        .padding(horizontal = 7.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = med.category.displayName,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalMossDark
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "View Monograph",
                    tint = NaturalMossPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
