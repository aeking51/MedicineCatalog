package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.MedicalInformation
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.model.AyurvedaMedicine
import com.example.data.model.DoshaType
import com.example.ui.components.ClassicalPhotoPresets
import com.example.ui.components.EditProductPhotoDialog
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
import com.example.ui.theme.NaturalSageBorder
import com.example.ui.theme.NaturalSageContainer
import com.example.ui.theme.NaturalTerracotta
import com.example.ui.theme.NaturalTextHeading
import com.example.ui.theme.NaturalTextPrimary

/**
 * Dedicated full-window view for displaying detailed Ayurveda medicine information.
 * Features a prominent 1:1 original square medicine product photo with zoom viewer,
 * structured monograph sections, and clean responsive bottom navigation.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AyurvedaMedicineDetailScreen(
    medicine: AyurvedaMedicine,
    onNavigateBack: () -> Unit,
    onUpdatePhoto: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Intercept Android hardware/gesture back press to return cleanly to previous window
    BackHandler(onBack = onNavigateBack)

    var showEditPhotoDialog by remember { mutableStateOf(false) }
    var showFullScreenImageViewer by remember { mutableStateOf(false) }

    val isGlass = AyurTheme.colors.isGlass

    // Resolve product photo with fallback preset
    val effectivePhotoUrl = medicine.photoUrl.ifBlank {
        ClassicalPhotoPresets.getPresetForCategory(medicine.category)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (isGlass) {
            Image(
                painter = painterResource(id = R.drawable.img_ayurveda_glass_bg),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0x88FFFFFF),
                                Color(0x55E8F5E9),
                                Color(0x75C8E6C9)
                            )
                        )
                    )
            )
        }

        Scaffold(
            modifier = modifier
                .fillMaxSize()
                .then(if (!isGlass) Modifier.background(NaturalBackground) else Modifier)
                .testTag("ayurveda_medicine_detail_window"),
            containerColor = if (isGlass) Color.Transparent else NaturalBackground,
            topBar = {
                MedicineDetailTopBar(
                    medicine = medicine,
                    onNavigateBack = onNavigateBack
                )
            },
            bottomBar = {
                MedicineDetailBottomBar(
                    medicine = medicine,
                    onNavigateBack = onNavigateBack
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.TopCenter
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 920.dp)
                        .testTag("medicine_detail_scroll_container"),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 1. DEDICATED 1:1 SQUARE PRODUCT IMAGE CONTAINER (At the top, 100% visible, no crop)
                    item {
                        MedicineDetailSquarePhotoContainer(
                            medicine = medicine,
                            photoUrl = effectivePhotoUrl,
                            onImageClick = { showFullScreenImageViewer = true }
                        )
                    }

                    // 2. PRODUCT OVERVIEW & IDENTITY CARD
                    item {
                        MedicineDetailHeaderCard(medicine = medicine)
                    }

                    // 3. CLASSICAL HANDBOOK SPECIFICATIONS & REFERENCE RECORD
                    item {
                        SitaramHandbookMonographCard(medicine = medicine)
                    }

                    // 4. KEY THERAPEUTIC ACTIONS & INDICATIONS
                    item {
                        TherapeuticIndicationsSection(medicine = medicine)
                    }

                    // 5. POSOLOGY & ADMINISTRATION TIMING (ANUPANA VEHICLE)
                    item {
                        DosageAndTimingSection(medicine = medicine)
                    }

                    // 6. CLASSICAL DRAVYAGUNA (ENERGETICS & PHARMACOLOGY) MATRIX
                    item {
                        DravyagunaSection(medicine = medicine)
                    }

                    // 7. BOTANICAL INGREDIENTS & CLASSICAL FORMULATION REGISTRY
                    item {
                        IngredientsRegistrySection(medicine = medicine)
                    }

                    // 8. DIET & LIFESTYLE GUIDANCE (PATHYA & APATHYA)
                    item {
                        DietaryGuidanceSection(medicine = medicine)
                    }

                    // Bottom breathing spacer before pinned action bar
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }

    // Interactive Full-Screen Image Viewer Dialog (Supports pinch-to-zoom and double-tap zoom)
    if (showFullScreenImageViewer) {
        FullScreenProductImageViewer(
            imageUrl = effectivePhotoUrl,
            medicineName = medicine.name,
            sanskritName = medicine.sanskritName,
            onDismiss = { showFullScreenImageViewer = false }
        )
    }

    // Modal dialog to update or customize product photo (Admin action)
    if (showEditPhotoDialog && onUpdatePhoto != null) {
        EditProductPhotoDialog(
            medicine = medicine,
            onDismiss = { showEditPhotoDialog = false },
            onSavePhotoUrl = { newUrl ->
                onUpdatePhoto(newUrl)
                showEditPhotoDialog = false
            }
        )
    }
}

/**
 * Top App Bar with back navigation, breadcrumb title, and category badge
 */
@Composable
private fun MedicineDetailTopBar(
    medicine: AyurvedaMedicine,
    onNavigateBack: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = if (AyurTheme.colors.isGlass) Color.Transparent else NaturalBackground,
        border = BorderStroke(0.dp, Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f, fill = false)
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(NaturalCardSurface)
                        .border(1.dp, NaturalCardBorder, CircleShape)
                        .testTag("detail_window_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back to Catalogue",
                        tint = NaturalTextHeading,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Text(
                        text = "CLASSICAL MEDICINE MONOGRAPH",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp,
                        color = NaturalOliveMuted
                    )
                    Text(
                        text = medicine.name,
                        fontFamily = FontFamily.Serif,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalTextHeading,
                        maxLines = 1
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(NaturalSageContainer)
                    .border(1.dp, NaturalSageBorder, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = medicine.category.displayName,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaturalMossDark
                )
            }
        }
    }
}

/**
 * Dedicated 1:1 Square Product Image Container at the top of the detail screen.
 * Displays the complete original 1:1 square medicine packaging image without cropping,
 * stretching, or cutting off any portion of the bottle or label.
 * Free of overlapping badges or text, with an elegant tap-to-expand affordance.
 */
@Composable
private fun MedicineDetailSquarePhotoContainer(
    medicine: AyurvedaMedicine,
    photoUrl: String,
    onImageClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("medicine_detail_square_photo_section"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Dedicated 1:1 Square Container with soft elevation, rounded corners, and clean white background
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.88f)
                .widthIn(max = 420.dp)
                .aspectRatio(1f)
                .shadow(elevation = 2.dp, shape = RoundedCornerShape(24.dp))
                .clip(RoundedCornerShape(24.dp))
                .clickable(onClick = onImageClick)
                .testTag("detail_square_image_container"),
            color = Color.White,
            border = BorderStroke(1.dp, NaturalCardBorder)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                contentAlignment = Alignment.Center
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(photoUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = "Original formulation packaging photo of ${medicine.name}",
                    contentScale = ContentScale.Fit, // STRICT CONTAIN MODE: NEVER CROPS OR STRETCHES
                    alignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("detail_square_photo_image"),
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
                                color = NaturalMossPrimary,
                                strokeWidth = 2.5.dp
                            )
                        }
                    },
                    error = {
                        // High quality botanical fallback if photo network request fails
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFFFAFAF7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Spa,
                                    contentDescription = null,
                                    tint = NaturalMossPrimary,
                                    modifier = Modifier.size(48.dp)
                                )
                                Text(
                                    text = medicine.name,
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NaturalTextHeading
                                )
                                Text(
                                    text = medicine.category.displayName,
                                    fontSize = 11.sp,
                                    color = NaturalOliveMuted
                                )
                            }
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Clean, subtle affordance indicating tap for full-screen inspection
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = NaturalCardSurface,
            border = BorderStroke(1.dp, NaturalCardBorder),
            modifier = Modifier.clickable(onClick = onImageClick)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ZoomIn,
                    contentDescription = null,
                    tint = NaturalMossDark,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Tap photo to inspect 1:1 full screen",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NaturalMossDark
                )
            }
        }
    }
}

/**
 * Product Identity & Overview Card immediately following the photo.
 * Organizes Sanskrit naming, commercial title, classical reference text,
 * short clinical summary, and doshic harmony badge.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MedicineDetailHeaderCard(medicine: AyurvedaMedicine) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("detail_hero_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = NaturalCardSurface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NaturalCardBorder, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            // Category, Tag, and Packing Badges (Cleanly separated from the photo)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NaturalParchmentContainer,
                        border = BorderStroke(1.dp, NaturalParchmentBorder)
                    ) {
                        Text(
                            text = medicine.category.displayName.uppercase(),
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaturalTerracotta,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    if (medicine.tagPill.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = NaturalSageContainer,
                            border = BorderStroke(1.dp, NaturalSageBorder)
                        ) {
                            Text(
                                text = medicine.tagPill,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = NaturalMossDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                if (medicine.effectivePacking.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = NaturalBackground,
                        border = BorderStroke(1.dp, NaturalCardBorder)
                    ) {
                        Text(
                            text = medicine.effectivePacking,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NaturalTextHeading,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Classical Sanskrit Transliteration
            if (medicine.sanskritName.isNotBlank()) {
                Text(
                    text = medicine.sanskritName,
                    fontSize = 14.sp,
                    fontStyle = FontStyle.Italic,
                    fontWeight = FontWeight.SemiBold,
                    color = NaturalEarthGold,
                    letterSpacing = 0.5.sp
                )
            }

            // Primary Medicine Name
            Text(
                text = medicine.name,
                fontFamily = FontFamily.Serif,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = NaturalTextHeading,
                lineHeight = 30.sp
            )

            // Textual Authority Reference Citation
            if (medicine.effectiveReference.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "Classical Authority:",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalOliveMuted
                    )
                    Text(
                        text = medicine.effectiveReference,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = NaturalMossDark
                    )
                }
            }

            // Short Clinical Description
            if (medicine.shortDescription.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = medicine.shortDescription,
                    fontSize = 13.sp,
                    color = NaturalTextPrimary,
                    lineHeight = 19.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dosha Harmony Badge
            DoshaHarmonyBadge(medicine = medicine)
        }
    }
}

/**
 * Dosha Harmony badge showing targeted doshas
 */
@Composable
private fun DoshaHarmonyBadge(medicine: AyurvedaMedicine) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = NaturalBackground,
        border = BorderStroke(1.dp, NaturalCardBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = null,
                    tint = NaturalMossPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "DOSHIC HARMONY",
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaturalOliveMuted,
                    letterSpacing = 0.8.sp
                )
            }

            Text(
                text = if (medicine.doshaImpact.isNotBlank()) medicine.doshaImpact else "Tridosha Shamak",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = NaturalTerracotta
            )
        }
    }
}

/**
 * Classical Formulation Handbook Record (Therapeutic Index specifications)
 */
@Composable
private fun SitaramHandbookMonographCard(medicine: AyurvedaMedicine) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("detail_handbook_monograph_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = NaturalCardSurface),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, NaturalCardBorder, RoundedCornerShape(24.dp))
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MedicalServices,
                    contentDescription = null,
                    tint = NaturalEarthGold,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "CLASSICAL HANDBOOK SPECIFICATIONS",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaturalEarthGold,
                    letterSpacing = 1.2.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(NaturalBackground)
                    .border(1.dp, NaturalParchmentBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                HandbookDetailRow(label = "PRODUCT NAME", value = medicine.name)
                HorizontalDivider(color = NaturalCardBorder.copy(alpha = 0.5f))
                HandbookDetailRow(label = "CLASSICAL TEXT REFERENCE", value = medicine.effectiveReference)
                HorizontalDivider(color = NaturalCardBorder.copy(alpha = 0.5f))
                HandbookDetailRow(label = "PACKING & PACK SIZE", value = medicine.effectivePacking)
                HorizontalDivider(color = NaturalCardBorder.copy(alpha = 0.5f))
                HandbookDetailRow(label = "FORMULATION CATEGORY", value = medicine.category.displayName)
                HorizontalDivider(color = NaturalCardBorder.copy(alpha = 0.5f))
                HandbookDetailRow(label = "MAIN BOTANICAL ACTIVES", value = medicine.effectiveMainIngredientsText)
                HorizontalDivider(color = NaturalCardBorder.copy(alpha = 0.5f))
                HandbookDetailRow(label = "POSOLOGY / USAGE SUMMARY", value = medicine.effectiveUsage)
                HorizontalDivider(color = NaturalCardBorder.copy(alpha = 0.5f))
                HandbookDetailRow(label = "PRIMARY INDICATIONS", value = medicine.indications.joinToString(", "))
            }
        }
    }
}

@Composable
private fun HandbookDetailRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            color = NaturalMossDark,
            letterSpacing = 0.8.sp
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value.ifBlank { "—" },
            fontSize = 13.sp,
            color = NaturalTextHeading,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

/**
 * Key therapeutic actions, primary benefit highlight, and clinical indications
 */
@Composable
private fun TherapeuticIndicationsSection(medicine: AyurvedaMedicine) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = NaturalCardSurface,
        border = BorderStroke(1.dp, NaturalCardBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Eco,
                    contentDescription = null,
                    tint = NaturalMossPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "THERAPEUTIC ACTIONS & INDICATIONS",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaturalMossPrimary,
                    letterSpacing = 0.8.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Primary Highlighted Benefit Banner
            if (medicine.primaryBenefit.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NaturalParchmentContainer.copy(alpha = 0.65f),
                    border = BorderStroke(1.dp, NaturalParchmentBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = "✦", fontSize = 14.sp, color = NaturalEarthGold, fontWeight = FontWeight.Bold)
                        Column {
                            Text(
                                text = "PRIMARY CLINICAL BENEFIT",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = NaturalTerracotta,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = medicine.primaryBenefit,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NaturalTextHeading,
                                lineHeight = 18.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Itemized Benefits / Indications
            val benefitsList = medicine.effectiveBenefits
            if (benefitsList.isNotEmpty()) {
                benefitsList.forEach { benefit ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "•",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaturalEarthGold
                        )
                        Text(
                            text = benefit,
                            fontSize = 12.5.sp,
                            color = NaturalTextPrimary,
                            lineHeight = 18.sp
                        )
                    }
                }
            } else {
                Text(
                    text = "Formulated to harmonize vitiated doshas, stimulate internal metabolic vitality (Agni), and promote deep tissue rejuvenation (Rasayana).",
                    fontSize = 12.5.sp,
                    color = NaturalOliveMuted,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

/**
 * Dosage instructions, recommended Anupana vehicle, and cautions
 */
@Composable
private fun DosageAndTimingSection(medicine: AyurvedaMedicine) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = NaturalCardSurface,
        border = BorderStroke(1.dp, NaturalCardBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Schedule,
                    contentDescription = null,
                    tint = NaturalEarthGold,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "POSOLOGY & ADMINISTRATION TIMING",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaturalEarthGold,
                    letterSpacing = 0.8.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Standard Dose and Frequency
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = NaturalParchmentContainer.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, NaturalParchmentBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "STANDARD DOSE",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaturalTerracotta,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = medicine.dosage.standardDose,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaturalTextHeading
                        )
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = NaturalSageContainer.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, NaturalSageBorder)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "FREQUENCY & TIMING",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaturalMossDark,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = medicine.dosage.frequency,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaturalTextHeading
                        )
                    }
                }
            }

            if (medicine.dosage.timing.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Recommended Timing: ${medicine.dosage.timing}",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    color = NaturalTextPrimary,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Anupana (Carrier Vehicle) Callout Box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = NaturalBackground,
                border = BorderStroke(1.dp, NaturalCardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(text = "🥛", fontSize = 20.sp)
                    Column {
                        Text(
                            text = "RECOMMENDED ANUPANA (ADJUVANT VEHICLE)",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaturalOliveMuted,
                            letterSpacing = 0.8.sp
                        )
                        Text(
                            text = medicine.dosage.anupana,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NaturalTextHeading
                        )
                    }
                }
            }

            // Caution if present
            if (medicine.dosage.caution.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFF8E1),
                    border = BorderStroke(1.dp, Color(0xFFFFE082))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = Color(0xFFF57F17),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Caution: ${medicine.dosage.caution}",
                            fontSize = 11.5.sp,
                            color = Color(0xFF5D4037)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Dravyaguna (Classical Pharmacology & Energetics) Matrix
 */
@Composable
private fun DravyagunaSection(medicine: AyurvedaMedicine) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = NaturalCardSurface,
        border = BorderStroke(1.dp, NaturalCardBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MedicalServices,
                    contentDescription = null,
                    tint = NaturalOliveMuted,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "DRAVYAGUNA (CLASSICAL ENERGETICS)",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaturalOliveMuted,
                    letterSpacing = 0.8.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4-Quadrant Energetics Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Virya (Potency)
                EnergeticCard(
                    title = "VIRYA (POTENCY)",
                    value = medicine.dravyaguna.virya,
                    subtext = if (medicine.dravyaguna.virya.contains("Ushna", ignoreCase = true)) "Heating" else "Cooling",
                    modifier = Modifier.weight(1f)
                )

                // Vipaka (Post-Digestive Effect)
                EnergeticCard(
                    title = "VIPAKA",
                    value = medicine.dravyaguna.vipaka,
                    subtext = "Post-digestive transformation",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Rasa (Tastes)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = NaturalBackground,
                border = BorderStroke(1.dp, NaturalCardBorder)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "RASA (TASTE PROFILE)",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalOliveMuted,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = medicine.dravyaguna.rasa.joinToString(" • "),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = NaturalTextHeading
                    )
                }
            }

            if (medicine.dravyaguna.guna.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = NaturalBackground,
                    border = BorderStroke(1.dp, NaturalCardBorder)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "GUNA (PHYSICAL QUALITIES)",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaturalOliveMuted,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = medicine.dravyaguna.guna.joinToString(" • "),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NaturalTextHeading
                        )
                    }
                }
            }
        }
    }
}

/**
 * Metric card for Virya and Vipaka
 */
@Composable
private fun EnergeticCard(
    title: String,
    value: String,
    subtext: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = NaturalBackground,
        border = BorderStroke(1.dp, NaturalCardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = title,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = NaturalOliveMuted,
                letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = NaturalTextHeading
            )
            Text(
                text = subtext,
                fontSize = 10.sp,
                color = NaturalOliveMuted
            )
        }
    }
}

/**
 * Itemized botanical ingredients list with Latin names and classical roles
 */
@Composable
private fun IngredientsRegistrySection(medicine: AyurvedaMedicine) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = NaturalCardSurface,
        border = BorderStroke(1.dp, NaturalCardBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = NaturalMossPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "BOTANICAL INGREDIENTS (${medicine.ingredients.size})",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalMossPrimary,
                        letterSpacing = 0.8.sp
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = NaturalSageContainer
                ) {
                    Text(
                        text = "100% Classical Botanical",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalMossDark,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (medicine.ingredients.isNotEmpty()) {
                medicine.ingredients.forEachIndexed { index, ing ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (ing.sanskritName.isNotBlank()) "${ing.name} (${ing.sanskritName})" else ing.name,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NaturalTextHeading
                                )
                                if (ing.botanicalName.isNotBlank()) {
                                    Text(
                                        text = ing.botanicalName,
                                        fontSize = 11.sp,
                                        fontStyle = FontStyle.Italic,
                                        color = NaturalEarthGold
                                    )
                                }
                            }

                            if (ing.partUsed.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = NaturalBackground,
                                    border = BorderStroke(1.dp, NaturalCardBorder)
                                ) {
                                    Text(
                                        text = ing.partUsed,
                                        fontSize = 9.5.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = NaturalOliveMuted,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        if (ing.classicalRole.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Classical Role: ${ing.classicalRole}",
                                fontSize = 11.sp,
                                color = NaturalOliveMuted
                            )
                        }

                        if (index < medicine.ingredients.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(top = 8.dp),
                                thickness = 0.5.dp,
                                color = NaturalCardBorder
                            )
                        }
                    }
                }
            } else {
                Text(
                    text = "Full classical botanical recipe prepared according to standard Ayurvedic Formulary of India (AFI) protocols.",
                    fontSize = 12.sp,
                    color = NaturalOliveMuted
                )
            }
        }
    }
}

/**
 * Dietary and lifestyle guidance (Pathya and Apathya)
 */
@Composable
private fun DietaryGuidanceSection(medicine: AyurvedaMedicine) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = NaturalCardSurface,
        border = BorderStroke(1.dp, NaturalCardBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "DIET & LIFESTYLE HARMONIZATION",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Bold,
                color = NaturalOliveMuted,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Wholesome Foods (Pathya)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = NaturalSageContainer.copy(alpha = 0.45f),
                border = BorderStroke(1.dp, NaturalSageBorder)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "🌿 WHOLESOME REGIMEN (PATHYA)",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = NaturalMossDark,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (medicine.pathyaWholesome.isNotEmpty()) {
                            medicine.pathyaWholesome.joinToString(" • ")
                        } else {
                            "Warm seasoned moong soup, steamed leafy vegetables, cumin water, adequate rest, and balanced circadian rhythm."
                        },
                        fontSize = 12.sp,
                        color = NaturalTextPrimary,
                        lineHeight = 17.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Foods to Avoid (Apathya)
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFDECEA).copy(alpha = 0.6f),
                border = BorderStroke(1.dp, Color(0xFFF5C6CB))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "🚫 SUBSTANCES TO AVOID (APATHYA)",
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD32F2F),
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (medicine.apathyaAvoid.isNotEmpty()) {
                            medicine.apathyaAvoid.joinToString(" • ")
                        } else {
                            "Excessive pungent, sour, deeply fried foods, refrigerated iced water, late night heavy meals, and emotional stress."
                        },
                        fontSize = 12.sp,
                        color = NaturalTextPrimary,
                        lineHeight = 17.sp
                    )
                }
            }
        }
    }
}

/**
 * Pinned Bottom Action Bar with a clean, full-width "Return to Medicine Catalogue" button.
 * Uses high-contrast dark green branding and respects navigation bar insets.
 */
@Composable
private fun MedicineDetailBottomBar(
    medicine: AyurvedaMedicine,
    onNavigateBack: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = NaturalCardSurface,
        border = BorderStroke(1.dp, NaturalCardBorder),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = onNavigateBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("detail_window_bottom_back_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NaturalMossPrimary,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Return to Medicine Catalogue",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

/**
 * Full-screen Interactive Image Viewer.
 * Displays the complete original 1:1 square medicine product photo in maximum detail.
 * Supports pinch-to-zoom (up to 5x) and double-tap zoom (1x to 2.5x),
 * with a high-contrast backdrop, product title, and close button.
 */
@Composable
fun FullScreenProductImageViewer(
    imageUrl: String,
    medicineName: String,
    sanskritName: String = "",
    onDismiss: () -> Unit
) {
    // Intercept back gesture in full-screen view
    BackHandler(onBack = onDismiss)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = false
        )
    ) {
        var scale by remember { mutableFloatStateOf(1f) }
        var offset by remember { mutableStateOf(Offset.Zero) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xF00A130E))
                .systemBarsPadding()
                .testTag("full_screen_image_viewer_dialog")
        ) {
            // Header Bar with Medicine Title and Close Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = medicineName,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif,
                        maxLines = 1
                    )
                    Text(
                        text = if (sanskritName.isNotBlank()) "$sanskritName • 1:1 Original Format" else "Pinch or double-tap to zoom • 1:1 Original Format",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 12.sp
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.2f))
                        .testTag("close_full_screen_image_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close full screen image viewer",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Interactive Centered 1:1 Square Image Container
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 72.dp)
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = {
                                scale = if (scale > 1.5f) 1f else 2.5f
                                offset = Offset.Zero
                            }
                        )
                    }
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, _ ->
                            scale = (scale * zoom).coerceIn(1f, 5f)
                            if (scale > 1f) {
                                val maxOffset = (scale - 1f) * 600f
                                offset = Offset(
                                    x = (offset.x + pan.x).coerceIn(-maxOffset, maxOffset),
                                    y = (offset.y + pan.y).coerceIn(-maxOffset, maxOffset)
                                )
                            } else {
                                offset = Offset.Zero
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .widthIn(max = 500.dp)
                        .aspectRatio(1f)
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offset.x,
                            translationY = offset.y
                        ),
                    color = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = "Full-screen photo of $medicineName",
                            contentScale = ContentScale.Fit, // STRICT CONTAIN MODE: NEVER CROPPED
                            alignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize(),
                            loading = {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = NaturalMossPrimary,
                                        strokeWidth = 2.5.dp
                                    )
                                }
                            }
                        )
                    }
                }
            }

            // Bottom Helper Controls & Reset Zoom
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                horizontalArrangement = if (scale > 1.05f) Arrangement.SpaceBetween else Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Black.copy(alpha = 0.6f),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f))
                ) {
                    Text(
                        text = "${(scale * 100).toInt()}% • 1:1 Contain",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }

                if (scale > 1.05f) {
                    Button(
                        onClick = {
                            scale = 1f
                            offset = Offset.Zero
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White.copy(alpha = 0.25f),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Reset Zoom", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }
    }
}
