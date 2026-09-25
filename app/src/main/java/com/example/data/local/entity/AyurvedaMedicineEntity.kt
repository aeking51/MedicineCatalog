package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.AyurvedaIngredient
import com.example.data.model.AyurvedaMedicine
import com.example.data.model.DosageInfo
import com.example.data.model.DoshaType
import com.example.data.model.DravyagunaProfile
import com.example.data.model.FormulationCategory

@Entity(tableName = "ayurveda_medicines")
data class AyurvedaMedicineEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val ingredients: List<AyurvedaIngredient>,
    val dosageInstructions: String,
    val benefits: List<String>,
    val sanskritName: String = "",
    val category: String = FormulationCategory.CHURNA.name,
    val tagPill: String = "HERBAL",
    val shortDescription: String = "",
    val primaryBenefit: String = "",
    val doshaImpact: String = "",
    val targetDoshas: List<DoshaType> = listOf(DoshaType.TRIDOSHIC),
    val constituents: List<String> = emptyList(),
    // Dravyaguna profile flattened
    val rasaList: List<String> = listOf("Madhura"),
    val virya: String = "Sheeta",
    val vipaka: String = "Madhura",
    val gunaList: List<String> = listOf("Laghu"),
    // Dosage info flattened
    val dosageSummary: String = "500mg daily",
    val dosageStandardDose: String = "500mg",
    val dosageFrequency: String = "Twice daily",
    val dosageTiming: String = "After meals",
    val dosageAnupana: String = "Warm water",
    val dosageCaution: String = "",
    // Clinical lists
    val indications: List<String> = emptyList(),
    val contraindications: List<String> = emptyList(),
    val pathyaWholesome: List<String> = emptyList(),
    val apathyaAvoid: List<String> = emptyList(),
    val isDailyVitality: Boolean = false,
    val stockUnits: Int = 45,
    val batchNumber: String = "AYUR-2026-B12",
    val isLowStock: Boolean = false,
    val slNo: Int = 0,
    val classicalReference: String = "",
    val packing: String = "",
    val mainIngredientsText: String = "",
    val usageInstructionsText: String = "",
    val photoUrl: String = "",
    val lastUpdatedTimestamp: Long = System.currentTimeMillis()
)

fun AyurvedaMedicineEntity.toDomainModel(): AyurvedaMedicine {
    val categoryEnum = runCatching {
        FormulationCategory.valueOf(category)
    }.getOrDefault(FormulationCategory.CHURNA)

    return AyurvedaMedicine(
        id = id,
        name = name,
        ingredients = ingredients,
        dosageInstructions = dosageInstructions,
        benefits = benefits,
        sanskritName = sanskritName,
        category = categoryEnum,
        tagPill = tagPill,
        shortDescription = shortDescription,
        primaryBenefit = primaryBenefit,
        doshaImpact = doshaImpact,
        targetDoshas = if (targetDoshas.isNotEmpty()) targetDoshas else listOf(DoshaType.TRIDOSHIC),
        constituents = constituents,
        dravyaguna = DravyagunaProfile(
            rasa = rasaList,
            virya = virya,
            vipaka = vipaka,
            guna = gunaList
        ),
        dosage = DosageInfo(
            summary = dosageSummary,
            standardDose = dosageStandardDose,
            frequency = dosageFrequency,
            timing = dosageTiming,
            anupana = dosageAnupana,
            caution = dosageCaution
        ),
        indications = indications,
        contraindications = contraindications,
        pathyaWholesome = pathyaWholesome,
        apathyaAvoid = apathyaAvoid,
        isDailyVitality = isDailyVitality,
        stockUnits = stockUnits,
        batchNumber = batchNumber,
        isLowStock = isLowStock,
        slNo = slNo,
        classicalReference = classicalReference,
        packing = packing,
        mainIngredientsText = mainIngredientsText,
        usageInstructionsText = usageInstructionsText,
        photoUrl = photoUrl
    )
}

fun AyurvedaMedicine.toEntity(): AyurvedaMedicineEntity {
    return AyurvedaMedicineEntity(
        id = id,
        name = name,
        ingredients = ingredients,
        dosageInstructions = effectiveDosageInstructions,
        benefits = effectiveBenefits,
        sanskritName = sanskritName,
        category = category.name,
        tagPill = tagPill,
        shortDescription = shortDescription,
        primaryBenefit = primaryBenefit,
        doshaImpact = doshaImpact,
        targetDoshas = targetDoshas,
        constituents = constituents,
        rasaList = dravyaguna.rasa,
        virya = dravyaguna.virya,
        vipaka = dravyaguna.vipaka,
        gunaList = dravyaguna.guna,
        dosageSummary = dosage.summary,
        dosageStandardDose = dosage.standardDose,
        dosageFrequency = dosage.frequency,
        dosageTiming = dosage.timing,
        dosageAnupana = dosage.anupana,
        dosageCaution = dosage.caution,
        indications = indications,
        contraindications = contraindications,
        pathyaWholesome = pathyaWholesome,
        apathyaAvoid = apathyaAvoid,
        isDailyVitality = isDailyVitality,
        stockUnits = stockUnits,
        batchNumber = batchNumber,
        isLowStock = isLowStock,
        slNo = slNo,
        classicalReference = classicalReference,
        packing = packing,
        mainIngredientsText = mainIngredientsText,
        usageInstructionsText = usageInstructionsText,
        photoUrl = photoUrl,
        lastUpdatedTimestamp = System.currentTimeMillis()
    )
}
