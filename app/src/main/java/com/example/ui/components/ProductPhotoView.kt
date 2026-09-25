package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.data.model.AyurvedaMedicine
import com.example.data.model.FormulationCategory
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
 * Standard curation of verified botanical and classical formulation photographs.
 */
object ClassicalPhotoPresets {
    val ARISHTA_BOTTLE = "https://images.unsplash.com/photo-1546868871-7041f2a55e12?w=800&auto=format&fit=crop&q=80"
    val HERBAL_POWDER_CHOORNAM = "https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?w=800&auto=format&fit=crop&q=80"
    val GULIKA_TABLETS = "https://images.unsplash.com/photo-1584308666744-24d5c474f2ae?w=800&auto=format&fit=crop&q=80"
    val MEDICATED_OIL_TAILAM = "https://images.unsplash.com/photo-1608571423902-eed4a5ad8108?w=800&auto=format&fit=crop&q=80"
    val MEDICATED_GHEE_GHRITAM = "https://images.unsplash.com/photo-1589927986089-35812388d1f4?w=800&auto=format&fit=crop&q=80"
    val DECOCTION_KWATHA = "https://images.unsplash.com/photo-1615485290382-441e4d049cb5?w=800&auto=format&fit=crop&q=80"
    val HANDBOOK_MANUSCRIPT = "https://images.unsplash.com/photo-1544367567-0f2fcb009e0b?w=800&auto=format&fit=crop&q=80"

    fun getPresetForCategory(category: FormulationCategory): String {
        return when (category) {
            FormulationCategory.ARISHTA, FormulationCategory.ASAVA, FormulationCategory.ARKAM -> ARISHTA_BOTTLE
            FormulationCategory.CHURNA, FormulationCategory.BHASMA_KSHARA -> HERBAL_POWDER_CHOORNAM
            FormulationCategory.GULIKA, FormulationCategory.VATI -> GULIKA_TABLETS
            FormulationCategory.TAILA -> MEDICATED_OIL_TAILAM
            FormulationCategory.GHRITA, FormulationCategory.RASAYANA -> MEDICATED_GHEE_GHRITAM
            FormulationCategory.KWATHA -> DECOCTION_KWATHA
            else -> ARISHTA_BOTTLE
        }
    }
}

/**
 * Rich Product Photo Display component for classical Ayurveda formulations.
 * Displays the product packaging image with fallback to classical apothecary render.
 */
@Composable
fun ProductPhotoView(
    medicine: AyurvedaMedicine,
    modifier: Modifier = Modifier,
    aspectRatio: Float = 1.33f,
    showPackingBadge: Boolean = true,
    showReferenceBadge: Boolean = true,
    showCategoryBadge: Boolean = true,
    showEditAction: Boolean = false,
    onEditPhotoClick: (() -> Unit)? = null
) {
    val photoUrl = medicine.photoUrl.ifBlank {
        ClassicalPhotoPresets.getPresetForCategory(medicine.category)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .clip(RoundedCornerShape(18.dp))
            .background(NaturalParchmentContainer.copy(alpha = 0.5f))
            .border(1.dp, NaturalCardBorder, RoundedCornerShape(18.dp))
            .testTag("product_photo_${medicine.id}")
    ) {
        if (photoUrl.isNotBlank()) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photoUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "Photo of ${medicine.name} (${medicine.effectivePacking})",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(NaturalParchmentContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(28.dp),
                            color = NaturalMossPrimary,
                            strokeWidth = 2.dp
                        )
                    }
                },
                error = {
                    ClassicalApothecaryIllustration(
                        medicine = medicine,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            )
        } else {
            ClassicalApothecaryIllustration(
                medicine = medicine,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Top Subtle Gradient Shadow for Badges Legibility
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.5f), Color.Transparent)
                    )
                )
                .align(Alignment.TopCenter)
        )

        // Bottom Subtle Gradient Shadow for Packing Volume Legibility
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f))
                    )
                )
                .align(Alignment.BottomCenter)
        )

        // Top Badges: Category & Classical Text Reference
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showCategoryBadge) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = NaturalMossDark.copy(alpha = 0.85f),
                    border = BorderStroke(0.5.dp, NaturalEarthGold)
                ) {
                    Text(
                        text = medicine.category.displayName.uppercase(),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = 0.6.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            if (showReferenceBadge && medicine.classicalReference.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color.Black.copy(alpha = 0.6f),
                    border = BorderStroke(0.5.dp, Color.White.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = medicine.classicalReference,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }
        }

        // Bottom Bar: Packing Size Badge
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showPackingBadge && medicine.effectivePacking.isNotBlank()) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF1B4D3E).copy(alpha = 0.9f),
                    border = BorderStroke(1.dp, NaturalEarthGold)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Inventory2,
                            contentDescription = null,
                            tint = NaturalEarthGold,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = "PACKING: ${medicine.effectivePacking}",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }
        }

        // Optional Edit Photo Floating Button
        if (showEditAction && onEditPhotoClick != null) {
            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopEnd)
                    .clickable(onClick = onEditPhotoClick)
                    .testTag("edit_photo_button_${medicine.id}")
            ) {
                Box(modifier = Modifier.padding(6.dp)) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Update Product Photo",
                        tint = NaturalMossDark,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Compact Product Thumbnail suitable for registry rows and compact list items.
 */
@Composable
fun ProductThumbnailPhoto(
    medicine: AyurvedaMedicine,
    modifier: Modifier = Modifier,
    size: Int = 56
) {
    val photoUrl = medicine.photoUrl.ifBlank {
        ClassicalPhotoPresets.getPresetForCategory(medicine.category)
    }

    Box(
        modifier = modifier
            .size(size.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(NaturalParchmentContainer)
            .border(1.dp, NaturalCardBorder, RoundedCornerShape(12.dp))
    ) {
        if (photoUrl.isNotBlank()) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(photoUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = medicine.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 1.5.dp,
                            color = NaturalMossPrimary
                        )
                    }
                },
                error = {
                    CompactApothecaryIcon(medicine = medicine)
                }
            )
        } else {
            CompactApothecaryIcon(medicine = medicine)
        }
    }
}

@Composable
private fun CompactApothecaryIcon(medicine: AyurvedaMedicine) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(NaturalParchmentContainer, NaturalSageContainer)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = when (medicine.category) {
                FormulationCategory.ARISHTA, FormulationCategory.ASAVA, FormulationCategory.ARKAM -> Icons.Default.LocalPharmacy
                FormulationCategory.CHURNA, FormulationCategory.BHASMA_KSHARA -> Icons.Default.Eco
                FormulationCategory.GULIKA, FormulationCategory.VATI -> Icons.Default.LocalPharmacy
                else -> Icons.Default.LocalPharmacy
            },
            contentDescription = null,
            tint = NaturalMossPrimary,
            modifier = Modifier.size(24.dp)
        )
    }
}

/**
 * Classical Apothecary Vector / Canvas Illustration for formulations when photo is loading/offline.
 */
@Composable
private fun ClassicalApothecaryIllustration(
    medicine: AyurvedaMedicine,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2C221E),
                        Color(0xFF1B4D3E)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Decorative geometric watermark
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2
            val cy = size.height / 2
            val radius = size.minDimension * 0.35f

            drawCircle(
                color = Color(0xFFDFB15B).copy(alpha = 0.12f),
                radius = radius,
                center = Offset(cx, cy),
                style = Stroke(width = 2.dp.toPx())
            )
            drawCircle(
                color = Color(0xFFDFB15B).copy(alpha = 0.08f),
                radius = radius * 0.8f,
                center = Offset(cx, cy),
                style = Stroke(width = 1.dp.toPx())
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = NaturalEarthGold.copy(alpha = 0.2f),
                border = BorderStroke(1.dp, NaturalEarthGold),
                modifier = Modifier.size(46.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = NaturalEarthGold,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "SITARAM AYURVEDA",
                fontFamily = FontFamily.Serif,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = NaturalEarthGold,
                letterSpacing = 1.2.sp
            )

            Text(
                text = medicine.name,
                fontFamily = FontFamily.Serif,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            if (medicine.effectivePacking.isNotBlank()) {
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Net Volume: ${medicine.effectivePacking}",
                    fontSize = 10.sp,
                    color = Color.White.copy(alpha = 0.75f)
                )
            }
        }
    }
}

/**
 * Dialog allowing the user to update a product photo by entering a URL
 * or picking from classical Ayurvedic photography presets.
 */
@Composable
fun EditProductPhotoDialog(
    medicine: AyurvedaMedicine,
    onDismiss: () -> Unit,
    onSavePhotoUrl: (String) -> Unit
) {
    var urlInput by remember { mutableStateOf(medicine.photoUrl) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .border(1.dp, NaturalCardBorder, RoundedCornerShape(22.dp)),
            color = NaturalCardSurface
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "PRODUCT PHOTO",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaturalOliveMuted,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = medicine.name,
                            fontFamily = FontFamily.Serif,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = NaturalTextHeading
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = NaturalOliveMuted)
                    }
                }

                // Current Preview Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, NaturalCardBorder, RoundedCornerShape(14.dp))
                ) {
                    val previewUrl = urlInput.ifBlank { ClassicalPhotoPresets.getPresetForCategory(medicine.category) }
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(previewUrl).crossfade(true).build(),
                        contentDescription = "Preview",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        loading = {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = NaturalMossPrimary)
                            }
                        },
                        error = {
                            ClassicalApothecaryIllustration(medicine = medicine, modifier = Modifier.fillMaxSize())
                        }
                    )
                }

                // Input Field
                OutlinedTextField(
                    value = urlInput,
                    onValueChange = { urlInput = it },
                    label = { Text("Product Photo URL", fontSize = 12.sp) },
                    placeholder = { Text("https://example.com/product.jpg", fontSize = 11.sp) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NaturalMossPrimary,
                        unfocusedBorderColor = NaturalCardBorder
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                // Quick presets
                Text(
                    text = "Quick Select Classical Presets:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = NaturalOliveMuted
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PresetPill(
                        label = "Arishta Bottle",
                        isSelected = urlInput == ClassicalPhotoPresets.ARISHTA_BOTTLE,
                        onClick = { urlInput = ClassicalPhotoPresets.ARISHTA_BOTTLE }
                    )
                    PresetPill(
                        label = "Choornam Jar",
                        isSelected = urlInput == ClassicalPhotoPresets.HERBAL_POWDER_CHOORNAM,
                        onClick = { urlInput = ClassicalPhotoPresets.HERBAL_POWDER_CHOORNAM }
                    )
                    PresetPill(
                        label = "Gulika Pills",
                        isSelected = urlInput == ClassicalPhotoPresets.GULIKA_TABLETS,
                        onClick = { urlInput = ClassicalPhotoPresets.GULIKA_TABLETS }
                    )
                }

                // Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, NaturalCardBorder)
                    ) {
                        Text("Cancel", fontSize = 12.sp, color = NaturalTextPrimary)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            onSavePhotoUrl(urlInput.trim())
                            onDismiss()
                        },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = NaturalMossPrimary)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save Photo", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isSelected) NaturalMossPrimary else NaturalParchmentContainer,
        border = BorderStroke(1.dp, if (isSelected) NaturalMossPrimary else NaturalParchmentBorder),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color.White else NaturalTextPrimary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
        )
    }
}
