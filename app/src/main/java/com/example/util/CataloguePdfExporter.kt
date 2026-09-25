package com.example.util

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.AyurvedaMedicine
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Professional PDF Report Generator for Sitaram Ayurveda Medicine Catalogue and Stock Inventory.
 * Creates clean, multi-page, AYUSH GMP-styled clinical dossiers in standard A4 format.
 */
object CataloguePdfExporter {

    // A4 Dimensions in Points (72 dpi): 595 x 842 pt
    private const val PAGE_WIDTH = 595
    private const val PAGE_HEIGHT = 842
    private const val MARGIN_LEFT = 36f
    private const val MARGIN_RIGHT = 559f
    private const val MARGIN_TOP = 36f
    private const val MARGIN_BOTTOM = 804f
    private const val CONTENT_WIDTH = MARGIN_RIGHT - MARGIN_LEFT

    // Classical Palette
    private const val COLOR_PRIMARY = 0xFF0F382C.toInt()      // Sitaram NaturalMossDark
    private const val COLOR_PRIMARY_LIGHT = 0xFF1B4D3E.toInt()// NaturalMossPrimary
    private const val COLOR_GOLD = 0xFFC5A059.toInt()         // NaturalEarthGold
    private const val COLOR_DARK_GOLD = 0xFF8C6D2B.toInt()
    private const val COLOR_TEXT_DARK = 0xFF1B2420.toInt()    // NaturalTextHeading
    private const val COLOR_TEXT_MUTED = 0xFF5C6F68.toInt()   // NaturalOliveMuted
    private const val COLOR_PARCHMENT_BG = 0xFFFBF9F5.toInt() // NaturalCardSurface
    private const val COLOR_BORDER = 0xFFE2DDD3.toInt()       // NaturalCardBorder
    private const val COLOR_LINE = 0xFFD8D2C4.toInt()

    // Status Colors
    private const val COLOR_IN_STOCK_BG = 0xFFE8F5E9.toInt()
    private const val COLOR_IN_STOCK_TEXT = 0xFF2E7D32.toInt()
    private const val COLOR_LOW_STOCK_BG = 0xFFFFF3E0.toInt()
    private const val COLOR_LOW_STOCK_TEXT = 0xFFE65100.toInt()
    private const val COLOR_OUT_STOCK_BG = 0xFFFFEBEE.toInt()
    private const val COLOR_OUT_STOCK_TEXT = 0xFFC62828.toInt()

    /**
     * Generates a PDF file containing the medicine catalogue report.
     *
     * @param context Android context
     * @param medicines List of formulations to include
     * @param scopeTitle Label for the export scope (e.g. "Full Catalogue" or "Filtered Selection")
     * @param filterDetails Optional summary of active filters
     * @param includeStockBreakdown Whether to include inventory metrics
     * @return Generated [File] referencing the newly created PDF in cache
     */
    fun generateCataloguePdf(
        context: Context,
        medicines: List<AyurvedaMedicine>,
        scopeTitle: String = "Complete Pharmacopeia Catalogue",
        filterDetails: String = "",
        includeStockBreakdown: Boolean = false
    ): File {
        val pdfDocument = PdfDocument()

        // Setup Paints
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
        val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = 1f
        }

        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        val generatedDateStr = dateFormat.format(Date())

        // Calculate Stock Metrics
        val totalUnits = medicines.sumOf { it.stockUnits }
        val inStockCount = medicines.count { it.stockUnits > 15 }
        val lowStockCount = medicines.count { it.stockUnits in 1..15 }
        val outOfStockCount = medicines.count { it.stockUnits <= 0 }

        var currentPageNumber = 1
        var pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, currentPageNumber).create()
        var currentPage = pdfDocument.startPage(pageInfo)
        var canvas = currentPage.canvas

        // Draw Cover Page Header
        var currentY = drawDocumentHeader(
            canvas = canvas,
            paint = paint,
            fillPaint = fillPaint,
            strokePaint = strokePaint,
            scopeTitle = scopeTitle,
            filterDetails = filterDetails,
            generatedDate = generatedDateStr,
            medicinesCount = medicines.size
        )

        // Draw Executive Stock KPI Block
        if (includeStockBreakdown) {
            currentY = drawStockSummaryKpi(
                canvas = canvas,
                paint = paint,
                fillPaint = fillPaint,
                strokePaint = strokePaint,
                startY = currentY,
                totalMedicines = medicines.size,
                totalUnits = totalUnits,
                inStockCount = inStockCount,
                lowStockCount = lowStockCount,
                outOfStockCount = outOfStockCount
            )
        }

        // Section Title
        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        paint.textSize = 12f
        paint.color = COLOR_PRIMARY
        canvas.drawText("THERAPEUTIC FORMULATION ROSTER (${medicines.size} ITEMS)", MARGIN_LEFT, currentY + 16f, paint)

        strokePaint.color = COLOR_GOLD
        strokePaint.strokeWidth = 1.5f
        canvas.drawLine(MARGIN_LEFT, currentY + 22f, MARGIN_RIGHT, currentY + 22f, strokePaint)
        currentY += 32f

        // Draw Formulation Cards
        for ((index, medicine) in medicines.withIndex()) {
            val itemHeight = calculateCardHeight(medicine)

            // Check if card fits on current page (leaving space for footer)
            if (currentY + itemHeight > MARGIN_BOTTOM - 24f) {
                // Draw footer for current page
                drawPageFooter(canvas, paint, strokePaint, currentPageNumber)
                pdfDocument.finishPage(currentPage)

                // Start Next Page
                currentPageNumber++
                pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, currentPageNumber).create()
                currentPage = pdfDocument.startPage(pageInfo)
                canvas = currentPage.canvas

                // Draw Running Header
                currentY = drawRunningHeader(canvas, paint, strokePaint)
            }

            // Draw Single Medicine Card
            currentY = drawMedicineCard(
                canvas = canvas,
                paint = paint,
                fillPaint = fillPaint,
                strokePaint = strokePaint,
                startY = currentY,
                index = index + 1,
                medicine = medicine
            )
        }

        // Draw footer on last page
        drawPageFooter(canvas, paint, strokePaint, currentPageNumber)
        pdfDocument.finishPage(currentPage)

        // Save to cache directory
        val reportsDir = File(context.cacheDir, "reports").apply { mkdirs() }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val pdfFile = File(reportsDir, "Sitaram_Catalogue_Stock_Report_$timeStamp.pdf")

        FileOutputStream(pdfFile).use { output ->
            pdfDocument.writeTo(output)
        }
        pdfDocument.close()

        return pdfFile
    }

    private fun calculateCardHeight(medicine: AyurvedaMedicine): Float {
        var h = 64f // Base height with title, reference, and indications
        if (medicine.mainIngredientsText.isNotEmpty() || medicine.ingredients.isNotEmpty()) {
            h += 14f
        }
        if (medicine.usageInstructionsText.isNotEmpty() || medicine.dosageInstructions.isNotEmpty()) {
            h += 13f
        }
        return h + 10f // bottom margin
    }

    private fun drawDocumentHeader(
        canvas: Canvas,
        paint: Paint,
        fillPaint: Paint,
        strokePaint: Paint,
        scopeTitle: String,
        filterDetails: String,
        generatedDate: String,
        medicinesCount: Int
    ): Float {
        // Top Royal Banner Box
        val bannerHeight = 74f
        val bannerRect = RectF(MARGIN_LEFT, MARGIN_TOP, MARGIN_RIGHT, MARGIN_TOP + bannerHeight)
        fillPaint.color = COLOR_PRIMARY
        canvas.drawRoundRect(bannerRect, 6f, 6f, fillPaint)

        // Gold Accent Border
        strokePaint.color = COLOR_GOLD
        strokePaint.strokeWidth = 1f
        canvas.drawRoundRect(bannerRect, 6f, 6f, strokePaint)

        // Header Titles
        paint.color = Color.WHITE
        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        paint.textSize = 15f
        canvas.drawText("SITARAM AYURVEDA PHARMACY", MARGIN_LEFT + 14f, MARGIN_TOP + 22f, paint)

        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        paint.textSize = 8f
        paint.color = COLOR_GOLD
        canvas.drawText("ESTABLISHED 1921 • FIRST AYURVEDIC GMP PHARMACY IN INDIA", MARGIN_LEFT + 14f, MARGIN_TOP + 34f, paint)

        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.NORMAL)
        paint.textSize = 11f
        paint.color = Color.WHITE
        canvas.drawText("OFFICIAL FORMULATION CATALOGUE & INVENTORY STOCK REPORT", MARGIN_LEFT + 14f, MARGIN_TOP + 52f, paint)

        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        paint.textSize = 7.5f
        paint.color = 0xFFD0D7D4.toInt()
        canvas.drawText("Comprehensive Classical Pharmacopeia, Therapeutic Indices & Warehouse Stock Status", MARGIN_LEFT + 14f, MARGIN_TOP + 65f, paint)

        var y = MARGIN_TOP + bannerHeight + 10f

        // Document Metadata Row
        fillPaint.color = COLOR_PARCHMENT_BG
        val metaRect = RectF(MARGIN_LEFT, y, MARGIN_RIGHT, y + 26f)
        canvas.drawRoundRect(metaRect, 4f, 4f, fillPaint)

        strokePaint.color = COLOR_BORDER
        canvas.drawRoundRect(metaRect, 4f, 4f, strokePaint)

        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        paint.textSize = 7.5f
        paint.color = COLOR_TEXT_MUTED
        canvas.drawText("GENERATED:", MARGIN_LEFT + 8f, y + 16f, paint)

        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        paint.color = COLOR_TEXT_DARK
        canvas.drawText(generatedDate, MARGIN_LEFT + 62f, y + 16f, paint)

        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        paint.color = COLOR_TEXT_MUTED
        canvas.drawText("SCOPE:", MARGIN_LEFT + 220f, y + 16f, paint)

        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        paint.color = COLOR_TEXT_DARK
        val scopeText = if (filterDetails.isNotEmpty()) "$scopeTitle ($filterDetails)" else "$scopeTitle ($medicinesCount items)"
        drawTruncatedText(canvas, scopeText, MARGIN_LEFT + 258f, y + 16f, 250f, paint)

        return y + 34f
    }

    private fun drawStockSummaryKpi(
        canvas: Canvas,
        paint: Paint,
        fillPaint: Paint,
        strokePaint: Paint,
        startY: Float,
        totalMedicines: Int,
        totalUnits: Int,
        inStockCount: Int,
        lowStockCount: Int,
        outOfStockCount: Int
    ): Float {
        val boxHeight = 44f
        val gap = 7f
        val boxWidth = (CONTENT_WIDTH - (gap * 3)) / 4f

        val kpiData = listOf(
            Triple("TOTAL REMEDIES", "$totalMedicines", COLOR_PRIMARY),
            Triple("TOTAL WAREHOUSE STOCK", "$totalUnits units", COLOR_DARK_GOLD),
            Triple("ADEQUATE STOCK (>15)", "$inStockCount", COLOR_IN_STOCK_TEXT),
            Triple("LOW / OUT OF STOCK", "${lowStockCount + outOfStockCount}", if (lowStockCount + outOfStockCount > 0) COLOR_LOW_STOCK_TEXT else COLOR_IN_STOCK_TEXT)
        )

        for ((i, kpi) in kpiData.withIndex()) {
            val left = MARGIN_LEFT + i * (boxWidth + gap)
            val rect = RectF(left, startY, left + boxWidth, startY + boxHeight)

            fillPaint.color = COLOR_PARCHMENT_BG
            canvas.drawRoundRect(rect, 4f, 4f, fillPaint)

            strokePaint.color = COLOR_BORDER
            strokePaint.strokeWidth = 1f
            canvas.drawRoundRect(rect, 4f, 4f, strokePaint)

            // Accent bar on top of each card
            fillPaint.color = kpi.third
            canvas.drawRoundRect(RectF(left, startY, left + boxWidth, startY + 3f), 2f, 2f, fillPaint)

            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            paint.textSize = 6.5f
            paint.color = COLOR_TEXT_MUTED
            canvas.drawText(kpi.first, left + 7f, startY + 16f, paint)

            paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
            paint.textSize = 14f
            paint.color = kpi.third
            canvas.drawText(kpi.second, left + 7f, startY + 35f, paint)
        }

        return startY + boxHeight + 14f
    }

    private fun drawRunningHeader(
        canvas: Canvas,
        paint: Paint,
        strokePaint: Paint
    ): Float {
        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        paint.textSize = 8.5f
        paint.color = COLOR_PRIMARY
        canvas.drawText("SITARAM AYURVEDA • THERAPEUTIC FORMULATION CATALOGUE & INVENTORY REPORT", MARGIN_LEFT, MARGIN_TOP + 12f, paint)

        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        paint.textSize = 7.5f
        paint.color = COLOR_TEXT_MUTED
        canvas.drawText("AYUSH GMP Certified • Batch & Stock Verification", MARGIN_RIGHT - 185f, MARGIN_TOP + 12f, paint)

        strokePaint.color = COLOR_LINE
        strokePaint.strokeWidth = 0.8f
        canvas.drawLine(MARGIN_LEFT, MARGIN_TOP + 18f, MARGIN_RIGHT, MARGIN_TOP + 18f, strokePaint)

        return MARGIN_TOP + 28f
    }

    private fun drawMedicineCard(
        canvas: Canvas,
        paint: Paint,
        fillPaint: Paint,
        strokePaint: Paint,
        startY: Float,
        index: Int,
        medicine: AyurvedaMedicine
    ): Float {
        val cardHeight = calculateCardHeight(medicine) - 10f
        val cardRect = RectF(MARGIN_LEFT, startY, MARGIN_RIGHT, startY + cardHeight)

        // Background
        fillPaint.color = if (index % 2 == 1) COLOR_PARCHMENT_BG else Color.WHITE
        canvas.drawRoundRect(cardRect, 5f, 5f, fillPaint)

        // Border
        strokePaint.color = COLOR_BORDER
        strokePaint.strokeWidth = 0.8f
        canvas.drawRoundRect(cardRect, 5f, 5f, strokePaint)

        var textY = startY + 15f

        // 1. Line 1: Index, Trade Name, Sanskrit Name & Category
        paint.typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        paint.textSize = 10f
        paint.color = COLOR_PRIMARY
        val titlePrefix = "$index. ${medicine.name}"
        canvas.drawText(titlePrefix, MARGIN_LEFT + 8f, textY, paint)

        val titleWidth = paint.measureText(titlePrefix)

        if (medicine.sanskritName.isNotEmpty()) {
            paint.typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
            paint.textSize = 8.5f
            paint.color = COLOR_TEXT_MUTED
            drawTruncatedText(canvas, "(${medicine.sanskritName})", MARGIN_LEFT + 8f + titleWidth + 6f, textY, 150f, paint)
        }

        // Category Tag
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        paint.textSize = 7f
        paint.color = COLOR_DARK_GOLD
        val catTag = "[ ${medicine.category.displayName.uppercase()} ]"
        canvas.drawText(catTag, MARGIN_LEFT + 320f, textY, paint)

        // Stock Badge (Far Right)
        drawStockBadge(canvas, paint, fillPaint, strokePaint, medicine, startY + 5f)

        textY += 13f

        // 2. Line 2: Classical Reference, Packing, Dosha Impact, Batch
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        paint.textSize = 7.5f
        paint.color = COLOR_TEXT_MUTED
        val refLabel = "REF: "
        canvas.drawText(refLabel, MARGIN_LEFT + 8f, textY, paint)

        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        paint.color = COLOR_TEXT_DARK
        val refValue = medicine.classicalReference.ifEmpty { "Classical Treatise" }
        canvas.drawText(refValue, MARGIN_LEFT + 28f, textY, paint)

        val refWidth = paint.measureText(refValue) + 36f

        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        paint.color = COLOR_TEXT_MUTED
        canvas.drawText("PACK: ", MARGIN_LEFT + refWidth, textY, paint)

        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        paint.color = COLOR_TEXT_DARK
        val packValue = medicine.packing.ifEmpty { "Standard Bottle" }
        canvas.drawText(packValue, MARGIN_LEFT + refWidth + 26f, textY, paint)

        val packWidth = refWidth + paint.measureText(packValue) + 34f

        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        paint.color = COLOR_TEXT_MUTED
        canvas.drawText("DOSHA: ", MARGIN_LEFT + packWidth, textY, paint)

        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        paint.color = COLOR_PRIMARY_LIGHT
        val doshaValue = medicine.doshaImpact.ifEmpty { "Tridoshahara" }
        drawTruncatedText(canvas, doshaValue, MARGIN_LEFT + packWidth + 36f, textY, 120f, paint)

        textY += 13f

        // 3. Line 3: Primary Benefit & Key Indications
        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        paint.textSize = 7.5f
        paint.color = COLOR_TEXT_MUTED
        canvas.drawText("ACTION: ", MARGIN_LEFT + 8f, textY, paint)

        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        paint.color = COLOR_TEXT_DARK
        val benefitText = medicine.primaryBenefit.ifEmpty { medicine.shortDescription }
        drawTruncatedText(canvas, benefitText, MARGIN_LEFT + 46f, textY, 460f, paint)

        textY += 12f

        // 4. Line 4: Key Ingredients (if available)
        val ingredientsText = when {
            medicine.mainIngredientsText.isNotEmpty() -> medicine.mainIngredientsText
            medicine.ingredients.isNotEmpty() -> medicine.ingredients.take(6).joinToString(", ") { it.name }
            else -> ""
        }
        if (ingredientsText.isNotEmpty()) {
            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            paint.textSize = 7f
            paint.color = COLOR_TEXT_MUTED
            canvas.drawText("HERBS: ", MARGIN_LEFT + 8f, textY, paint)

            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            paint.color = 0xFF4A5568.toInt()
            drawTruncatedText(canvas, ingredientsText, MARGIN_LEFT + 44f, textY, 460f, paint)

            textY += 12f
        }

        // 5. Line 5: Usage / Dosage (if available)
        val usageText = when {
            medicine.usageInstructionsText.isNotEmpty() -> medicine.usageInstructionsText
            medicine.dosageInstructions.isNotEmpty() -> medicine.dosageInstructions
            else -> ""
        }
        if (usageText.isNotEmpty()) {
            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
            paint.textSize = 7f
            paint.color = COLOR_TEXT_MUTED
            canvas.drawText("DOSAGE: ", MARGIN_LEFT + 8f, textY, paint)

            paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            paint.color = 0xFF4A5568.toInt()
            drawTruncatedText(canvas, usageText, MARGIN_LEFT + 48f, textY, 455f, paint)
        }

        return startY + cardHeight + 8f
    }

    private fun drawStockBadge(
        canvas: Canvas,
        paint: Paint,
        fillPaint: Paint,
        strokePaint: Paint,
        medicine: AyurvedaMedicine,
        badgeY: Float
    ) {
        val (badgeText, bgColor, textColor, borderColor) = when {
            medicine.stockUnits <= 0 -> {
                Quadruple("OUT OF STOCK", COLOR_OUT_STOCK_BG, COLOR_OUT_STOCK_TEXT, COLOR_OUT_STOCK_TEXT)
            }
            medicine.isLowStock || medicine.stockUnits <= 15 -> {
                Quadruple("LOW STOCK: ${medicine.stockUnits} u", COLOR_LOW_STOCK_BG, COLOR_LOW_STOCK_TEXT, COLOR_LOW_STOCK_TEXT)
            }
            else -> {
                Quadruple("IN STOCK: ${medicine.stockUnits} u", COLOR_IN_STOCK_BG, COLOR_IN_STOCK_TEXT, COLOR_IN_STOCK_TEXT)
            }
        }

        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        paint.textSize = 7f
        val textWidth = paint.measureText(badgeText)
        val badgeWidth = textWidth + 14f
        val badgeHeight = 14f
        val badgeLeft = MARGIN_RIGHT - badgeWidth - 6f

        val badgeRect = RectF(badgeLeft, badgeY, badgeLeft + badgeWidth, badgeY + badgeHeight)
        fillPaint.color = bgColor
        canvas.drawRoundRect(badgeRect, 3f, 3f, fillPaint)

        strokePaint.color = borderColor
        strokePaint.strokeWidth = 0.8f
        canvas.drawRoundRect(badgeRect, 3f, 3f, strokePaint)

        paint.color = textColor
        canvas.drawText(badgeText, badgeLeft + 7f, badgeY + 10f, paint)
    }

    private fun drawPageFooter(
        canvas: Canvas,
        paint: Paint,
        strokePaint: Paint,
        pageNumber: Int
    ) {
        strokePaint.color = COLOR_LINE
        strokePaint.strokeWidth = 0.8f
        canvas.drawLine(MARGIN_LEFT, MARGIN_BOTTOM - 12f, MARGIN_RIGHT, MARGIN_BOTTOM - 12f, strokePaint)

        paint.typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        paint.textSize = 7f
        paint.color = COLOR_TEXT_MUTED
        canvas.drawText("Sitaram AyurGuide Pharmacopeia • Confidential Clinical & Stock Audit Record", MARGIN_LEFT, MARGIN_BOTTOM - 2f, paint)

        val pageStr = "Page $pageNumber"
        val pageStrWidth = paint.measureText(pageStr)
        canvas.drawText(pageStr, MARGIN_RIGHT - pageStrWidth, MARGIN_BOTTOM - 2f, paint)
    }

    private fun drawTruncatedText(
        canvas: Canvas,
        text: String,
        x: Float,
        y: Float,
        maxWidth: Float,
        paint: Paint
    ) {
        var measured = text
        if (paint.measureText(measured) > maxWidth) {
            while (measured.isNotEmpty() && paint.measureText("$measured…") > maxWidth) {
                measured = measured.dropLast(1)
            }
            measured = "$measured…"
        }
        canvas.drawText(measured, x, y, paint)
    }

    /**
     * Launches the system PDF viewer for the given generated report [file].
     */
    fun openPdfReport(context: Context, file: File) {
        try {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, "Open Medicine Catalogue PDF"))
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open PDF viewer: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Launches the system share sheet to send the PDF report via email, WhatsApp, cloud storage, etc.
     */
    fun sharePdfReport(context: Context, file: File, medicinesCount: Int) {
        try {
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Sitaram Ayurveda - Medicine Catalogue & Stock Report")
                putExtra(
                    Intent.EXTRA_TEXT,
                    "Attached is the official Sitaram Ayurveda Therapeutic Index & Stock Dossier ($medicinesCount formulations) generated on ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())}."
                )
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Share Medicine Catalogue PDF"))
        } catch (e: Exception) {
            Toast.makeText(context, "Could not share PDF: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    /**
     * Sends the PDF report to the Android Print Spooler for wireless / local printer printing.
     */
    fun printPdfReport(context: Context, file: File) {
        try {
            val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
            if (printManager == null) {
                Toast.makeText(context, "Print service unavailable on this device", Toast.LENGTH_SHORT).show()
                return
            }
            printManager.print("Sitaram_Catalogue_Report", PdfFilePrintDocumentAdapter(file), null)
        } catch (e: Exception) {
            Toast.makeText(context, "Print failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}

/**
 * Adapter enabling direct wireless/network printing of a generated PDF file via Android's [PrintManager].
 */
class PdfFilePrintDocumentAdapter(private val file: File) : PrintDocumentAdapter() {
    override fun onLayout(
        oldAttributes: PrintAttributes?,
        newAttributes: PrintAttributes?,
        cancellationSignal: CancellationSignal?,
        callback: LayoutResultCallback?,
        extras: Bundle?
    ) {
        if (cancellationSignal?.isCanceled == true) {
            callback?.onLayoutCancelled()
            return
        }
        val info = PrintDocumentInfo.Builder(file.name)
            .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
            .build()
        callback?.onLayoutFinished(info, true)
    }

    override fun onWrite(
        pages: Array<out PageRange>?,
        destination: ParcelFileDescriptor?,
        cancellationSignal: CancellationSignal?,
        callback: WriteResultCallback?
    ) {
        if (cancellationSignal?.isCanceled == true) {
            callback?.onWriteCancelled()
            return
        }
        try {
            FileInputStream(file).use { input ->
                FileOutputStream(destination?.fileDescriptor).use { output ->
                    input.copyTo(output)
                }
            }
            callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
        } catch (e: Exception) {
            callback?.onWriteFailed(e.message)
        }
    }
}
