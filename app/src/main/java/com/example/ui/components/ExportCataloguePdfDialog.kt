package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AyurvedaMedicine
import com.example.ui.theme.*
import com.example.util.CataloguePdfExporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Dialog allowing users and clinicians to configure and export the medicine catalogue
 * into an official, publication-quality A4 PDF report with stock levels and formulation details.
 */
@Composable
fun ExportCataloguePdfDialog(
    allMedicines: List<AyurvedaMedicine>,
    filteredMedicines: List<AyurvedaMedicine>,
    activeFilterDescription: String = "",
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val hasActiveFilter = filteredMedicines.size != allMedicines.size && filteredMedicines.isNotEmpty()
    var exportFilteredOnly by remember { mutableStateOf(hasActiveFilter) }
    var includeTherapeuticDetails by remember { mutableStateOf(true) }

    var isGenerating by remember { mutableStateOf(false) }
    var generatedPdfFile by remember { mutableStateOf<File?>(null) }

    val targetMedicines = if (exportFilteredOnly) filteredMedicines else allMedicines

    Dialog(
        onDismissRequest = { if (!isGenerating) onDismiss() },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .clip(RoundedCornerShape(22.dp))
                .border(1.dp, NaturalEarthGold.copy(alpha = 0.5f), RoundedCornerShape(22.dp))
                .testTag("export_catalogue_pdf_dialog"),
            color = NaturalCardSurface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                // 1. Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(NaturalMossDark),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                tint = NaturalEarthGold,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "OFFICIAL REPORT EXPORT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                color = NaturalEarthGold
                            )
                            Text(
                                text = "Classical Catalogue PDF",
                                fontFamily = FontFamily.Serif,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = NaturalTextHeading
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        enabled = !isGenerating,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(NaturalSageContainer)
                            .testTag("close_pdf_export_dialog")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = NaturalMossDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = NaturalCardBorder)
                Spacer(modifier = Modifier.height(14.dp))

                // 2. Scope Selection Card
                Text(
                    text = "REPORT SCOPE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.1.sp,
                    color = NaturalOliveMuted
                )
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = NaturalBackground,
                    border = BorderStroke(1.dp, NaturalCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        if (hasActiveFilter) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { exportFilteredOnly = true }
                                    .padding(vertical = 4.dp)
                            ) {
                                RadioButton(
                                    selected = exportFilteredOnly,
                                    onClick = { exportFilteredOnly = true },
                                    colors = RadioButtonDefaults.colors(selectedColor = NaturalMossPrimary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column {
                                    Text(
                                        text = "Current Filtered List (${filteredMedicines.size} Formulations)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = NaturalTextPrimary
                                    )
                                    if (activeFilterDescription.isNotEmpty()) {
                                        Text(
                                            text = activeFilterDescription,
                                            fontSize = 11.sp,
                                            color = NaturalOliveMuted
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { exportFilteredOnly = false }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = !exportFilteredOnly,
                                onClick = { exportFilteredOnly = false },
                                colors = RadioButtonDefaults.colors(selectedColor = NaturalMossPrimary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "Complete Catalogue (${allMedicines.size} Formulations)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NaturalTextPrimary
                                )
                                Text(
                                    text = "Full Sitaram Ayurvedic pharmacopeia archive",
                                    fontSize = 11.sp,
                                    color = NaturalOliveMuted
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Content Inclusions
                Text(
                    text = "SECTIONS & DETAILS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.1.sp,
                    color = NaturalOliveMuted
                )
                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = NaturalBackground,
                    border = BorderStroke(1.dp, NaturalCardBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { includeTherapeuticDetails = !includeTherapeuticDetails }
                                .padding(vertical = 4.dp)
                        ) {
                            Checkbox(
                                checked = includeTherapeuticDetails,
                                onCheckedChange = { includeTherapeuticDetails = it },
                                colors = CheckboxDefaults.colors(checkedColor = NaturalMossPrimary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "Classical References & Therapeutic Indices",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = NaturalTextPrimary
                                )
                                Text(
                                    text = "Dosha actions, packaging size, indications, and key herbs",
                                    fontSize = 10.5.sp,
                                    color = NaturalOliveMuted
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 4. Dossier Summary Preview Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = NaturalSageContainer,
                    border = BorderStroke(1.dp, NaturalSageBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "READY FOR GENERATION",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = NaturalMossDark,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "${targetMedicines.size} Classical Formulations",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = NaturalMossDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 5. Actions / Generation Progress
                if (isGenerating) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = NaturalMossPrimary,
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Compiling Professional PDF Dossier...",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NaturalMossDark
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Main Action: Generate & Open PDF
                        Button(
                            onClick = {
                                isGenerating = true
                                coroutineScope.launch {
                                    val scopeLabel = if (exportFilteredOnly) "Filtered Pharmacopeia Selection" else "Complete Pharmacopeia Catalogue"
                                    val file = withContext(Dispatchers.IO) {
                                        CataloguePdfExporter.generateCataloguePdf(
                                            context = context,
                                            medicines = targetMedicines,
                                            scopeTitle = scopeLabel,
                                            filterDetails = if (exportFilteredOnly) activeFilterDescription else "",
                                            includeStockBreakdown = false
                                        )
                                    }
                                    generatedPdfFile = file
                                    isGenerating = false
                                    Toast.makeText(context, "PDF Report generated: ${file.name}", Toast.LENGTH_SHORT).show()
                                    CataloguePdfExporter.openPdfReport(context, file)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("generate_open_pdf_btn"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NaturalMossPrimary,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Visibility,
                                contentDescription = null,
                                modifier = Modifier.size(17.dp),
                                tint = NaturalEarthGold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Generate & Open PDF",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Secondary Actions Row: Share PDF & Print PDF
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    isGenerating = true
                                    coroutineScope.launch {
                                        val scopeLabel = if (exportFilteredOnly) "Filtered Pharmacopeia Selection" else "Complete Pharmacopeia Catalogue"
                                        val file = withContext(Dispatchers.IO) {
                                            CataloguePdfExporter.generateCataloguePdf(
                                                context = context,
                                                medicines = targetMedicines,
                                                scopeTitle = scopeLabel,
                                                filterDetails = if (exportFilteredOnly) activeFilterDescription else "",
                                                includeStockBreakdown = false
                                            )
                                        }
                                        generatedPdfFile = file
                                        isGenerating = false
                                        CataloguePdfExporter.sharePdfReport(context, file, targetMedicines.size)
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("share_pdf_btn"),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, NaturalMossPrimary),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = NaturalMossDark)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = NaturalMossDark
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Share PDF",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    isGenerating = true
                                    coroutineScope.launch {
                                        val scopeLabel = if (exportFilteredOnly) "Filtered Pharmacopeia Selection" else "Complete Pharmacopeia Catalogue"
                                        val file = withContext(Dispatchers.IO) {
                                            CataloguePdfExporter.generateCataloguePdf(
                                                context = context,
                                                medicines = targetMedicines,
                                                scopeTitle = scopeLabel,
                                                filterDetails = if (exportFilteredOnly) activeFilterDescription else "",
                                                includeStockBreakdown = false
                                            )
                                        }
                                        generatedPdfFile = file
                                        isGenerating = false
                                        CataloguePdfExporter.printPdfReport(context, file)
                                    }
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("print_pdf_btn"),
                                shape = RoundedCornerShape(14.dp),
                                border = BorderStroke(1.dp, NaturalCardBorder),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = NaturalTextPrimary)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Print,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = NaturalOliveMuted
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Print PDF",
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
