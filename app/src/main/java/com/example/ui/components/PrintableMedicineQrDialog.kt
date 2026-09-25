package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AyurvedaMedicine
import com.example.ui.theme.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel

/**
 * Generates an Android [Bitmap] containing a high-contrast QR Code for the given [content].
 */
fun generateQrBitmap(
    content: String,
    sizePx: Int = 512,
    darkColor: Int = AndroidColor.parseColor("#0F382C"),
    lightColor: Int = AndroidColor.WHITE
): Bitmap? {
    return try {
        val hints = mapOf(
            EncodeHintType.CHARACTER_SET to "UTF-8",
            EncodeHintType.MARGIN to 1,
            EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.H
        )
        val bitMatrix = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, sizePx, sizePx, hints)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (bitMatrix.get(x, y)) darkColor else lightColor
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        bitmap
    } catch (e: Exception) {
        null
    }
}

/**
 * Dialog displaying a printable, high-resolution QR label linking to the medicine's digital monograph.
 */
@Composable
fun PrintableMedicineQrDialog(
    medicine: AyurvedaMedicine,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isDeepLinkMode by remember { mutableStateOf(false) }

    // Derive target URL: Public Web Monograph or App Deep Link
    // Defaults to public web monograph URL compatible with standard smartphone cameras
    val webMonographUrl = remember(medicine.id) {
        "https://ais-dev-h6umrrfka2hqdt2a6hwoi7-919348880295.asia-southeast1.run.app/monograph?id=${medicine.id}"
    }
    val appDeepLinkUrl = remember(medicine.id) {
        "sitaram://medicine/${medicine.id}"
    }
    val activeUrl = if (isDeepLinkMode) appDeepLinkUrl else webMonographUrl

    // Generate QR Bitmap
    val qrBitmap = remember(activeUrl) {
        generateQrBitmap(activeUrl, sizePx = 600)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(24.dp))
                .border(1.dp, NaturalEarthGold.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .testTag("printable_medicine_qr_dialog"),
            color = NaturalCardSurface,
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(NaturalMossDark)
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(NaturalEarthGold),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "S",
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = NaturalMossDark
                                )
                            }
                            Column {
                                Text(
                                    text = "Printable QR Monograph",
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "Digital Classical Formulation Label",
                                    fontSize = 11.sp,
                                    color = NaturalEarthGold
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                // Scrollable Content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Target URL Selector (Web Monograph vs App Deep Link)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(NaturalBackground)
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { isDeepLinkMode = false },
                            color = if (!isDeepLinkMode) NaturalMossPrimary else Color.Transparent
                        ) {
                            Text(
                                text = "🌐 Web Monograph",
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                fontWeight = if (!isDeepLinkMode) FontWeight.Bold else FontWeight.Normal,
                                color = if (!isDeepLinkMode) Color.White else NaturalOliveMuted
                            )
                        }
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { isDeepLinkMode = true },
                            color = if (isDeepLinkMode) NaturalMossPrimary else Color.Transparent
                        ) {
                            Text(
                                text = "📱 App Deep Link",
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 12.sp,
                                fontWeight = if (isDeepLinkMode) FontWeight.Bold else FontWeight.Normal,
                                color = if (isDeepLinkMode) Color.White else NaturalOliveMuted
                            )
                        }
                    }

                    // Printable Label Card (Framed to mimic real bottle/carton sticker)
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, NaturalEarthGold, RoundedCornerShape(20.dp))
                            .clip(RoundedCornerShape(20.dp)),
                        color = Color.White,
                        shadowElevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Header banner
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "SITARAM AYURVEDA",
                                        fontFamily = FontFamily.Serif,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 12.sp,
                                        color = NaturalMossDark,
                                        letterSpacing = 1.sp
                                    )
                                    Text(
                                        text = "AYUSH GMP CERTIFIED • ESTD 1921",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NaturalEarthGold
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(NaturalBackground)
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = medicine.batchNumber,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NaturalTextHeading
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Medicine Titles
                            if (medicine.sanskritName.isNotEmpty()) {
                                Text(
                                    text = medicine.sanskritName,
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = NaturalEarthGold,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Text(
                                text = medicine.name,
                                fontFamily = FontFamily.Serif,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = NaturalMossDark,
                                textAlign = TextAlign.Center
                            )

                            Row(
                                modifier = Modifier.padding(top = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(NaturalSageContainer)
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = medicine.category.displayName,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NaturalMossDark
                                    )
                                }
                                if (medicine.classicalReference.isNotEmpty()) {
                                    Text(
                                        text = "Ref: ${medicine.classicalReference}",
                                        fontSize = 10.sp,
                                        color = NaturalOliveMuted,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // QR Image Display
                            Box(
                                modifier = Modifier
                                    .size(200.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .border(1.dp, NaturalCardBorder, RoundedCornerShape(14.dp))
                                    .background(Color.White)
                                    .padding(8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                if (qrBitmap != null) {
                                    Image(
                                        bitmap = qrBitmap.asImageBitmap(),
                                        contentDescription = "QR Code for ${medicine.name}",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(36.dp),
                                        color = NaturalMossPrimary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "❖ SCAN FOR DIGITAL MONOGRAPH & DOSAGE ❖",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = NaturalMossDark,
                                letterSpacing = 0.5.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Footer Regulatory Information
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(0.5.dp, NaturalCardBorder, RoundedCornerShape(8.dp))
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(text = "DOSHA", fontSize = 8.sp, color = NaturalOliveMuted)
                                    Text(
                                        text = medicine.doshaImpact.ifEmpty { "Tridoshic" },
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NaturalTextHeading,
                                        maxLines = 1
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "PACKING", fontSize = 8.sp, color = NaturalOliveMuted)
                                    Text(
                                        text = medicine.packing.ifEmpty { "Standard" },
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NaturalTextHeading
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(text = "AYUSH LIC", fontSize = 8.sp, color = NaturalOliveMuted)
                                    Text(
                                        text = "KL-TCR-1921",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = NaturalTextHeading
                                    )
                                }
                            }
                        }
                    }

                    // Monograph URL Box
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, NaturalCardBorder, RoundedCornerShape(12.dp)),
                        color = NaturalBackground
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Monograph URL Target:",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = NaturalOliveMuted
                                )
                                Text(
                                    text = activeUrl,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = NaturalMossDark,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("Medicine Monograph URL", activeUrl)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Monograph URL copied!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Monograph Link",
                                    tint = NaturalMossPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // Action Bar at Bottom
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = NaturalBackground,
                    tonalElevation = 4.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "${medicine.name} — Digital Medicine Monograph")
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        """
                                        🌿 ${medicine.name} (${medicine.sanskritName})
                                        Category: ${medicine.category.displayName}
                                        Batch: ${medicine.batchNumber}
                                        Dosha: ${medicine.doshaImpact}
                                        
                                        Digital Clinical Monograph:
                                        $activeUrl
                                        
                                        Sitaram Ayurveda • Estd 1921
                                        """.trimIndent()
                                    )
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Medicine Monograph"))
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("share_monograph_button"),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NaturalCardBorder)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = NaturalMossDark
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Share",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = NaturalMossDark
                            )
                        }

                        Button(
                            onClick = {
                                // Trigger standard system share / print intent
                                val printIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, "PRINT: ${medicine.name} QR Label")
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "Printable QR Label for ${medicine.name} [Batch: ${medicine.batchNumber}]\n$activeUrl"
                                    )
                                }
                                context.startActivity(Intent.createChooser(printIntent, "Print or Export QR Label"))
                                Toast.makeText(context, "Opening print service...", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .weight(1.3f)
                                .height(46.dp)
                                .testTag("print_qr_label_button"),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NaturalMossPrimary,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Print,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = NaturalEarthGold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Print QR Label",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
