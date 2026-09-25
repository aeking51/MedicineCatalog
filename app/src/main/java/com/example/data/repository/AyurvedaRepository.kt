package com.example.data.repository

import com.example.data.model.AppUser
import com.example.data.model.AuditLogEntry
import com.example.data.model.AyurvedaIngredient
import com.example.data.model.AyurvedaMedicine
import com.example.data.model.DailyDoseLog
import com.example.data.model.DailyHabit
import com.example.data.model.DosageInfo
import com.example.data.model.DoshaType
import com.example.data.model.DravyagunaProfile
import com.example.data.model.FormulationCategory
import com.example.data.model.HealthGoal
import com.example.data.model.PrakritiQuestion
import com.example.data.model.UserRole
import com.example.data.model.UserStatus
import com.example.ui.components.ClassicalPhotoPresets

object AyurvedaRepository {

    val allMedicines: List<AyurvedaMedicine> = listOf(
        AyurvedaMedicine(
            id = "dashamoolarishtam",
            slNo = 0,
            name = "Dashamoolarishtam",
            sanskritName = "दशमूलारिष्टम् (Sharangadhara Samhita)",
            classicalReference = "Sharangadhara Samhita",
            category = FormulationCategory.ARISHTA,
            packing = "450 ml",
            tagPill = "VITALITY & DETOX",
            healthGoals = listOf(HealthGoal.DETOX, HealthGoal.DIGESTION, HealthGoal.IMMUNITY),
            shortDescription = "Traditionally used to support digestion, strength, vitality, and recovery from physical exhaustion.",
            primaryBenefit = "Post-partum recovery, respiratory strength, digestive fire kindle and deep detox",
            doshaImpact = "Vata & Kapha Shamaka (Kindles digestive Agni)",
            targetDoshas = listOf(DoshaType.VATA, DoshaType.KAPHA),
            mainIngredientsText = "Dashamoola (Ten Sacred Roots), Dhataki, Draksha, Amalaki, Haritaki",
            usageInstructionsText = "15-25 ml Twice daily after meals with equal quantity of warm water",
            photoUrl = ClassicalPhotoPresets.ARISHTA_BOTTLE,
            constituents = listOf("Natural Bio-fermented Actives (5-10%)", "Flavonoids", "Polyphenols", "Tannins"),
            ingredients = listOf(
                AyurvedaIngredient(name = "Dashamoola (Ten Roots)", botanicalName = "Aegle marmelos & 9 others", partUsed = "Roots", classicalRole = "Tridoshic pacifier, Balya, Rasayana"),
                AyurvedaIngredient(name = "Draksha (Raisins)", botanicalName = "Vitis vinifera", partUsed = "Fruit", classicalRole = "Pitta shamaka, nourishing tonic"),
                AyurvedaIngredient(name = "Dhataki", botanicalName = "Woodfordia fruticosa", partUsed = "Flowers", classicalRole = "Natural fermentation initiator"),
                AyurvedaIngredient(name = "Amalaki", botanicalName = "Emblica officinalis", partUsed = "Fruit", classicalRole = "Antioxidant, Ojas promoter")
            ),
            dravyaguna = DravyagunaProfile(
                rasa = listOf("Tikta (Bitter)", "Kashaya (Astringent)", "Madhura (Sweet)"),
                virya = "Ushna (Heating)",
                vipaka = "Madhura (Post-digestive sweet)",
                guna = listOf("Laghu (Light)", "Teekshna (Sharp)")
            ),
            dosage = DosageInfo(
                summary = "15-25 ml • Twice Daily After Meals",
                standardDose = "15-25 ml",
                frequency = "Twice Daily",
                timing = "After Meals",
                anupana = "Equal volume of warm water",
                caution = "Consult Vaidya during active hyperacidity."
            ),
            indications = listOf(
                "Post-delivery exhaustion (Sutika Rogas)",
                "General fatigue & debility (Daurbalya)",
                "Loss of appetite (Aruchi)",
                "Respiratory weakness (Kasa, Swasa)",
                "Vata disorders (Vata Vyadhi)"
            ),
            contraindications = listOf("Severe active hyperacidity without diluting with water"),
            pathyaWholesome = listOf("Warm cooked foods", "Milk", "Ghee", "Restorative soups"),
            apathyaAvoid = listOf("Excessive cold beverages", "Dry snacks", "Irregular sleep"),
            stockUnits = 150,
            batchNumber = "SIT-DSH-2026-001"
        ),
        AyurvedaMedicine(
            id = "abhayarishtam",
            slNo = 1,
            name = "Abhayarishtam",
            sanskritName = "अभयारिष्टम् (Ashtamgahrudayam)",
            classicalReference = "Ashtamgahrudayam",
            category = FormulationCategory.ARISHTA,
            packing = "450 ml",
            tagPill = "DIGESTIVE ELIXIR",
            healthGoals = listOf(HealthGoal.DIGESTION, HealthGoal.DETOX),
            shortDescription = "Classical fermented decoction elixir for relieving hemorrhoids, abdominal distension and obstinate constipation.",
            primaryBenefit = "Arshas (Hemorrhoids) relief, Apana Vata normalization and bowel peristalsis",
            doshaImpact = "Vata & Kapha Shamaka (Kindles digestive Agni)",
            targetDoshas = listOf(DoshaType.VATA, DoshaType.KAPHA),
            mainIngredientsText = "Abhaya (Terminalia chebula), Dhatri (Emblica officinalis), Kapitha, Vishala",
            usageInstructionsText = "5-25 ml Twice daily after meals with equal quantity of water",
            photoUrl = ClassicalPhotoPresets.ARISHTA_BOTTLE,
            constituents = listOf("Bio-generated Alcohol (5-10%)", "Tannins", "Chebulic Acid", "Anthraquinones"),
            ingredients = listOf(
                AyurvedaIngredient(name = "Abhaya (Haritaki)", botanicalName = "Terminalia chebula", partUsed = "Fruit Pericarp", classicalRole = "Deepana, Pachana, Arshoghna (Dispels Piles)"),
                AyurvedaIngredient(name = "Dhatri (Amalaki)", botanicalName = "Emblica officinalis", partUsed = "Dried Fruit", classicalRole = "Rasayana, Pitta balancing"),
                AyurvedaIngredient(name = "Kapitha", botanicalName = "Feronia elephantum", partUsed = "Fruit Pulp", classicalRole = "Grahi, Agnivardhana"),
                AyurvedaIngredient(name = "Vishala", botanicalName = "Citrullus colocynthis", partUsed = "Root", classicalRole = "Srotoshodhana, Bhedana")
            ),
            dravyaguna = DravyagunaProfile(
                rasa = listOf("Kashaya (Astringent)", "Tikta (Bitter)", "Madhura (Sweet)"),
                virya = "Ushna (Heating)",
                vipaka = "Madhura (Post-digestive sweet)",
                guna = listOf("Laghu (Light)", "Ruksha (Dry)")
            ),
            dosage = DosageInfo(
                summary = "15-25 ml • Twice Daily After Meals",
                standardDose = "5-25 ml",
                frequency = "Twice Daily",
                timing = "After Meals",
                anupana = "Equal volume of lukewarm water",
                caution = "Not recommended for children under 5 without Vaidya consultation."
            ),
            indications = listOf(
                "Arshas (Hemorrhoids / Piles)",
                "Udara (Ascitis / Abdominal Enlargement)",
                "Muthra vibanda (Anuria / Urinary retention)",
                "Vibanda (Chronic Constipation)",
                "Agnimandya (Loss of appetite / Weak digestion)"
            ),
            contraindications = listOf("Severe active peptic ulcers", "Acute diarrhea (Atisara)"),
            pathyaWholesome = listOf("Buttermilk (Takra)", "Fiber-rich leafy vegetables", "Warm water", "Barley porridge"),
            apathyaAvoid = listOf("Excessive dry spicy foods", "Dry baked snacks", "Suppression of natural urges (Vega dharana)"),
            stockUnits = 120,
            batchNumber = "SIT-ARI-2026-001"
        ),
        AyurvedaMedicine(
            id = "amrutharishtam",
            slNo = 2,
            name = "Amrutharishtam",
            sanskritName = "अमृतारिष्टम् (A.F.I. Part 1 Bhaisajya Ratnavali)",
            classicalReference = "A.F.I. Part 1 Bhaisajya Ratnavali",
            category = FormulationCategory.ARISHTA,
            packing = "450 ml",
            tagPill = "IMMUNO-FEBRIFUGE",
            healthGoals = listOf(HealthGoal.IMMUNITY, HealthGoal.DETOX),
            shortDescription = "Premier Ayurvedic formulation for acute and relapsing fevers, tonsillitis, oedema, and poor metabolic fire.",
            primaryBenefit = "Jwara (Fever) resolution, immunomodulation, and deep lymphatic Ama clearance",
            doshaImpact = "Tridosha Shamaka (Primarily Pitta-Kapha Hara)",
            targetDoshas = listOf(DoshaType.PITTA, DoshaType.KAPHA),
            mainIngredientsText = "Amritha (Guduchi), Bilwa, Syonaka, Gambhari",
            usageInstructionsText = "5-25 ml Twice daily after meals with equal quantity of water",
            photoUrl = ClassicalPhotoPresets.ARISHTA_BOTTLE,
            constituents = listOf("Guduchi Alkaloids", "Flavonoids", "Glycosides", "Bitter Tonics"),
            ingredients = listOf(
                AyurvedaIngredient(name = "Amritha (Guduchi)", botanicalName = "Tinospora cordifolia", partUsed = "Stem", classicalRole = "Jwaraghna (Antipyretic), Rasayana, Dahaprashamana"),
                AyurvedaIngredient(name = "Bilwa", botanicalName = "Aegle marmelos", partUsed = "Root Bark", classicalRole = "Dashamula constituent, anti-inflammatory"),
                AyurvedaIngredient(name = "Syonaka", botanicalName = "Oroxylum indicum", partUsed = "Root Bark", classicalRole = "Shothahara (Resolves edema)"),
                AyurvedaIngredient(name = "Gambhari", botanicalName = "Gmelina arborea", partUsed = "Root Bark", classicalRole = "Dhatupushtikara, Deepana")
            ),
            dravyaguna = DravyagunaProfile(
                rasa = listOf("Tikta (Bitter)", "Kashaya (Astringent)"),
                virya = "Ushna (Mild Heating)",
                vipaka = "Madhura (Sweet)",
                guna = listOf("Laghu (Light)")
            ),
            dosage = DosageInfo(
                summary = "15-25 ml • Twice Daily After Meals",
                standardDose = "5-25 ml",
                frequency = "Twice Daily",
                timing = "After Meals",
                anupana = "Equal volume of boiled and cooled water",
                caution = "Take after food to avoid gastric irritation in high Pitta individuals."
            ),
            indications = listOf(
                "Jwara (Acute, chronic & recurrent fevers)",
                "Tundikeri (Uvulitis)",
                "Galayu (Tonsillitis)",
                "Sotha (Oedema / Tissue Swelling)",
                "Agnimandya (Loss of appetite)"
            ),
            contraindications = listOf("Active hyperacidity during empty stomach"),
            pathyaWholesome = listOf("Light green gram soup (Mudga Yusha)", "Pappadam", "Boiled vegetables", "Warm water"),
            apathyaAvoid = listOf("Oily heavy curds", "Day sleeping (Diva swapna)", "Cold refrigerated beverages"),
            stockUnits = 95,
            batchNumber = "SIT-ARI-2026-002"
        ),
        AyurvedaMedicine(
            id = "arjunarishtam",
            slNo = 4,
            name = "Arjunarishtam",
            sanskritName = "अर्जुनारिष्टम् (A.F.I. Part 1 Bhaisajya Ratnavali)",
            classicalReference = "A.F.I. Part 1 Bhaisajya Ratnavali",
            category = FormulationCategory.ARISHTA,
            packing = "450 ml",
            tagPill = "CARDIO TONIC",
            healthGoals = listOf(HealthGoal.IMMUNITY),
            shortDescription = "Renowned Ayurvedic cardio-protective tonic strengthening myocardial tone and relieving exhaustion.",
            primaryBenefit = "Hridroga (Heart disease) support, vascular elasticity, and physical vitality",
            doshaImpact = "Pitta & Kapha Pacifying (Tridosha Balancer)",
            targetDoshas = listOf(DoshaType.PITTA, DoshaType.KAPHA, DoshaType.VATA),
            mainIngredientsText = "Arjuna, Mrdwika (Raisins), Madhuka (Madhuca indica)",
            usageInstructionsText = "5-25 ml Twice daily after meals with equal quantity of water",
            photoUrl = ClassicalPhotoPresets.ARISHTA_BOTTLE,
            constituents = listOf("Arjunolic Acid", "Flavonoids", "Bio-calcium", "Triterpenoids"),
            ingredients = listOf(
                AyurvedaIngredient(name = "Arjuna", botanicalName = "Terminalia arjuna", partUsed = "Stem Bark", classicalRole = "Hridya (Cardiotonic), Sandhaniya, Kashaya-dominant"),
                AyurvedaIngredient(name = "Mrdwika (Draksha)", botanicalName = "Vitis vinifera", partUsed = "Dried Fruit", classicalRole = "Rasayana, Preenana (Nourishing)"),
                AyurvedaIngredient(name = "Madhuka", botanicalName = "Madhuca indica", partUsed = "Flower", classicalRole = "Balya, Brumhana")
            ),
            dravyaguna = DravyagunaProfile(
                rasa = listOf("Kashaya (Astringent)", "Madhura (Sweet)"),
                virya = "Sheeta (Cooling)",
                vipaka = "Katu (Pungent)",
                guna = listOf("Laghu (Light)", "Ruksha (Dry)")
            ),
            dosage = DosageInfo(
                summary = "15-25 ml • Twice Daily After Meals",
                standardDose = "5-25 ml",
                frequency = "Twice Daily",
                timing = "After meals",
                anupana = "Equal quantity of lukewarm water",
                caution = "Safe for long-term cardiovascular support under medical supervision."
            ),
            indications = listOf(
                "Hridgadha (Heart diseases / Angina / Palpitations)",
                "Balakshaya (Debility & General Weakness)",
                "Swasa (Breathlessness on exertion)"
            ),
            contraindications = listOf("None reported under standard posology"),
            pathyaWholesome = listOf("Pomegranate", "Cow's ghee in moderation", "Garlic infused milk", "Daily gentle walking"),
            apathyaAvoid = listOf("Excessive saturated fats", "Stressful mental agitation", "Excessive sodium intake"),
            stockUnits = 68,
            batchNumber = "SIT-ARI-2026-004"
        ),
        AyurvedaMedicine(
            id = "aravindasavam",
            slNo = 1,
            name = "Aravindasavam",
            sanskritName = "अरविन्दासवम् (A.F.I. Part 1 Bhaisajya Ratnavali)",
            classicalReference = "A.F.I. Part 1 Bhaisajya Ratnavali",
            category = FormulationCategory.ASAVA,
            packing = "450 ml",
            tagPill = "PAEDIATRIC TONIC",
            healthGoals = listOf(HealthGoal.IMMUNITY, HealthGoal.DIGESTION),
            shortDescription = "Celebrated Ayurvedic elixir for infants and growing children, enhancing physical strength, immunity and intellect.",
            primaryBenefit = "Bala roga (Pediatric complaints) relief, growth promotion, and digestive kindle",
            doshaImpact = "Balances Vata, Pitta, and Kapha in pediatrics",
            targetDoshas = listOf(DoshaType.TRIDOSHIC),
            mainIngredientsText = "Aravinda (Nelumbo nucifera), Ushira, Kashmari, Neelotpala",
            usageInstructionsText = "5-25 ml Twice daily with equal quantity of water",
            photoUrl = ClassicalPhotoPresets.ARISHTA_BOTTLE,
            constituents = listOf("Lotus Bioflavonoids", "Saponins", "Essential Micro-nutrients"),
            ingredients = listOf(
                AyurvedaIngredient(name = "Aravinda (Kamala)", botanicalName = "Nelumbo nucifera", partUsed = "Lotus Flower", classicalRole = "Medhya, Balya, Hridya"),
                AyurvedaIngredient(name = "Ushira", botanicalName = "Vetiveria zizanioides", partUsed = "Root", classicalRole = "Cooling, Dahaprashamana"),
                AyurvedaIngredient(name = "Kashmari", botanicalName = "Gmelina arborea", partUsed = "Fruit", classicalRole = "Brumhana, Rasayana"),
                AyurvedaIngredient(name = "Neelotpala", botanicalName = "Nymphaea stellata", partUsed = "Blue Water Lily", classicalRole = "Pitta shamaka, Nervine soother")
            ),
            dravyaguna = DravyagunaProfile(
                rasa = listOf("Madhura (Sweet)", "Kashaya (Astringent)", "Tikta (Bitter)"),
                virya = "Sheeta (Cooling)",
                vipaka = "Madhura (Sweet)",
                guna = listOf("Laghu (Light)")
            ),
            dosage = DosageInfo(
                summary = "5-15 ml (Children: 2.5-10 ml) • Twice Daily",
                standardDose = "5-25 ml",
                frequency = "Twice Daily",
                timing = "After food",
                anupana = "Equal volume of boiled warm water",
                caution = "Adjust dose according to the age and body weight of child."
            ),
            indications = listOf(
                "Bala roga (General pediatric disorders)",
                "Karshya (Emaciation / Failure to thrive)",
                "Balakshaya (Weakness & Loss of vitality)",
                "Atisara (Pediatric Diarrhoea)",
                "Agnimandya (Loss of appetite / Poor nutrient absorption)"
            ),
            contraindications = listOf("None under age-appropriate dosing"),
            pathyaWholesome = listOf("Warm rice porridge", "Cow milk", "Fresh fruit stews", "Ghee"),
            apathyaAvoid = listOf("Junk foods with artificial preservatives", "Excessive cold sweets"),
            stockUnits = 80,
            batchNumber = "SIT-ASA-2026-001"
        ),
        AyurvedaMedicine(
            id = "aviltholadi_bhasmam",
            slNo = 1,
            name = "Aviltholadi Bhasmam",
            sanskritName = "अविल्तोलादि भस्मम् (Sahasrayogam)",
            classicalReference = "Sahasrayogam",
            category = FormulationCategory.BHASMA_KSHARA,
            packing = "50 g",
            tagPill = "METABOLIC ALKALI",
            healthGoals = listOf(HealthGoal.DETOX, HealthGoal.DIGESTION),
            shortDescription = "Traditional Ayurvedic alkaline medicinal calx for scraping profound deep Ama, ascites, edema and abdominal masses.",
            primaryBenefit = "Sopha (Oedema), Gulma (Abdominal tumors), and Udara (Ascites) resolution",
            doshaImpact = "Kapha-Vata Shamaka & Chedana (Deep tissue scraping)",
            targetDoshas = listOf(DoshaType.KAPHA, DoshaType.VATA),
            mainIngredientsText = "Puthikatwak, Apamarga, Danthi, Arka",
            usageInstructionsText = "1 g at a time mixed with hot water",
            photoUrl = ClassicalPhotoPresets.HERBAL_POWDER_CHOORNAM,
            constituents = listOf("Purified Bio-alkalis", "Potassium carbonate", "Organic micro-elements"),
            ingredients = listOf(
                AyurvedaIngredient(name = "Puthikatwak", botanicalName = "Holoptelea integrifolia", partUsed = "Bark ash", classicalRole = "Ksharana, Lekhana (Scraping)"),
                AyurvedaIngredient(name = "Apamarga", botanicalName = "Achyranthes aspera", partUsed = "Whole plant ash", classicalRole = "Kshara, Srotoshodhana"),
                AyurvedaIngredient(name = "Danthi", botanicalName = "Baliospermum montanum", partUsed = "Root", classicalRole = "Bhedana, Rechana"),
                AyurvedaIngredient(name = "Arka", botanicalName = "Calotropis procera", partUsed = "Latex & Wood", classicalRole = "Dipana, Vata-Kapha hara")
            ),
            dravyaguna = DravyagunaProfile(
                rasa = listOf("Katu (Pungent)", "Lavana (Salty)"),
                virya = "Ushna (Hot / Piercing)",
                vipaka = "Katu (Pungent)",
                guna = listOf("Tikshna (Sharp)", "Laghu (Light)")
            ),
            dosage = DosageInfo(
                summary = "500mg - 1g • Twice Daily",
                standardDose = "1 g at a time",
                frequency = "Twice Daily",
                timing = "Before or with meals",
                anupana = "Hot water or buttermilk",
                caution = "Strictly consume under Vaidya supervision due to concentrated Kshara potency."
            ),
            indications = listOf(
                "Sopha (Oedema / Peripheral Swelling)",
                "Gulma (Chronic obstructive disorders / Phantom tumors)",
                "Udara (Ascitis / Fluid accumulation in abdomen)"
            ),
            contraindications = listOf("Pregnancy", "Severe dehydration", "Erosive gastritis"),
            pathyaWholesome = listOf("Takra (Spiced buttermilk)", "Barley water", "Dry warm foods"),
            apathyaAvoid = listOf("Excessive heavy oily curds", "Day sleep", "High sodium intake"),
            stockUnits = 42,
            batchNumber = "SIT-BHA-2026-001"
        ),
        AyurvedaMedicine(
            id = "dhanwantharam_gulika",
            slNo = 6,
            name = "Dhanwantharam Gulika",
            sanskritName = "धन्वन्तरं गुळिका (A.F.I. Part 1 Sahasrayogam)",
            classicalReference = "A.F.I. Part 1 Sahasrayogam",
            category = FormulationCategory.GULIKA,
            packing = "100 Nos.",
            tagPill = "VATA CARMINATIVE",
            healthGoals = listOf(HealthGoal.DIGESTION, HealthGoal.STRESS_RELIEF),
            shortDescription = "Classic pill formulation from Sahasrayogam for respiratory dyspnea, colic, hiccups, hiccups and abdominal distension.",
            primaryBenefit = "Normalizes downward flow of Apana & Prana Vata, eases gastric spasms",
            doshaImpact = "Vata & Kapha Anulomana",
            targetDoshas = listOf(DoshaType.VATA, DoshaType.KAPHA),
            mainIngredientsText = "Ela (Cardamom), Viswa (Dry Ginger), Haritaki, Jathiphala",
            usageInstructionsText = "Internal: 1-2 tablets twice daily chewed or dissolved in warm water",
            photoUrl = ClassicalPhotoPresets.GULIKA_TABLETS,
            constituents = listOf("Essential Volatile Oils", "Gingerols", "Tannins", "Cardamom Resins"),
            ingredients = listOf(
                AyurvedaIngredient(name = "Ela (Cardamom)", botanicalName = "Elettaria cardamomum", partUsed = "Seed", classicalRole = "Rochana, Deepana, Hridya"),
                AyurvedaIngredient(name = "Viswa (Dry Ginger)", botanicalName = "Zingiber officinale", partUsed = "Rhizome", classicalRole = "Pachana, Vata-Kapha Shamaka"),
                AyurvedaIngredient(name = "Haritaki", botanicalName = "Terminalia chebula", partUsed = "Fruit", classicalRole = "Anulomana (Peristaltic mover)"),
                AyurvedaIngredient(name = "Jathiphala", botanicalName = "Myristica fragrans", partUsed = "Nut", classicalRole = "Grahi, Shoolahara")
            ),
            dravyaguna = DravyagunaProfile(
                rasa = listOf("Katu (Pungent)", "Madhura (Sweet)", "Tikta (Bitter)"),
                virya = "Ushna (Warm)",
                vipaka = "Madhura",
                guna = listOf("Laghu (Light)", "Sugandha (Aromatic)")
            ),
            dosage = DosageInfo(
                summary = "1 - 2 Tablets • As needed or Twice Daily",
                standardDose = "1-2 Tablets (500mg - 1g)",
                frequency = "Twice Daily or SOS",
                timing = "With first morsel of food or after meals",
                anupana = "Warm cumin water (Jeeraka jala) or ginger decoction",
                caution = "Safe across age groups and postpartum care."
            ),
            indications = listOf(
                "Swasa (Dyspnea & Respiratory distress)",
                "Kasa (Cough & Bronchial congestion)",
                "Hikka (Persistent Hiccups)",
                "Chardi (Vomiting & Nausea)",
                "Shoola (Abdominal Colic & Spasms)",
                "Anaha (Abdominal distension / Flatulence)",
                "Rajayakshma (Tuberculosis adjunctive care)"
            ),
            contraindications = listOf("None known"),
            pathyaWholesome = listOf("Warm ginger tea", "Light soups", "Cooked warm meals"),
            apathyaAvoid = listOf("Cold chilled salads", "Carbonated soda", "Excessive heavy pulses"),
            stockUnits = 140,
            batchNumber = "SIT-GUL-2026-006"
        ),
        AyurvedaMedicine(
            id = "arogyavardhini_gulika",
            slNo = 1,
            name = "Arogyavardhini Gulika",
            sanskritName = "आरोग्यवर्धिनी गुळिका (A.F.I. Part 1 Rasa Ratna Samuchayam)",
            classicalReference = "A.F.I. Part 1 Rasa Ratna Samuchayam",
            category = FormulationCategory.GULIKA,
            packing = "100 Nos.",
            tagPill = "HEPATIC & METABOLIC",
            healthGoals = listOf(HealthGoal.SKIN_HEALTH, HealthGoal.DETOX, HealthGoal.DIGESTION),
            shortDescription = "High-efficacy herbo-mineral tablet for liver disorders, sluggish metabolism, skin conditions and hyperlipidemia.",
            primaryBenefit = "Deep hepatoprotection, blood purification, and metabolic Ama incineration",
            doshaImpact = "Balances Pitta & Kapha (Deepana & Pachana)",
            targetDoshas = listOf(DoshaType.PITTA, DoshaType.KAPHA),
            mainIngredientsText = "Shuddha Paradha, Shuddha Gandhaka, Loha bhasma, Abraka bhasma, Triphala",
            usageInstructionsText = "Internal: 1-2 tablets twice daily after food with warm water or milk",
            photoUrl = ClassicalPhotoPresets.GULIKA_TABLETS,
            constituents = listOf("Purified Mineral Calces", "Picrorhiza Bitters (Kutki)", "Triphala Tannins"),
            ingredients = listOf(
                AyurvedaIngredient(name = "Katuki (Picrorhiza)", botanicalName = "Picrorhiza kurroa", partUsed = "Rhizome", classicalRole = "Bhedana, Pitta-rechana, Yakrid-rakshaka"),
                AyurvedaIngredient(name = "Triphala (3 Myrobalans)", botanicalName = "Emblica, Terminalia sp.", partUsed = "Fruit pericarp", classicalRole = "Tridoshahara, Rasayana"),
                AyurvedaIngredient(name = "Loha Bhasma", botanicalName = "Incinerated Iron Calx", partUsed = "Purified Bhasma", classicalRole = "Panduhara, Balya"),
                AyurvedaIngredient(name = "Abhrak Bhasma", botanicalName = "Incinerated Mica Calx", partUsed = "Purified Bhasma", classicalRole = "Rasayana, Deepana")
            ),
            dravyaguna = DravyagunaProfile(
                rasa = listOf("Tikta (Bitter)", "Kashaya (Astringent)"),
                virya = "Sheeta (Balanced)",
                vipaka = "Katu (Pungent)",
                guna = listOf("Laghu (Light)", "Ruksha (Dry)")
            ),
            dosage = DosageInfo(
                summary = "1 - 2 Tablets (250mg - 500mg) • Twice Daily",
                standardDose = "1-2 Tablets",
                frequency = "Twice Daily",
                timing = "After Meals",
                anupana = "Warm water, honey, or lukewarm milk",
                caution = "Administer under physician direction. Contraindicated during pregnancy."
            ),
            indications = listOf(
                "Skin diseases (Kushta / Eczema / Psoriasis)",
                "Dyslipidemia (Elevated cholesterol & triglycerides)",
                "Obesity (Medoroga / Adipose tissue accumulation)",
                "Jwara (Chronic low-grade fevers)",
                "Yakrit roga (Fatty liver / Hepatic congestion)"
            ),
            contraindications = listOf("Pregnancy & Lactation", "Severe renal impairment"),
            pathyaWholesome = listOf("Mudga (Green gram)", "Bitter gourd", "Warm water", "Barley"),
            apathyaAvoid = listOf("Fried greasy food", "Excessive alcohol", "Heavy animal fats"),
            stockUnits = 110,
            batchNumber = "SIT-GUL-2026-001"
        ),
        AyurvedaMedicine(
            id = "chandraprabha_gulika",
            slNo = 3,
            name = "Chandraprabha Gulika",
            sanskritName = "चन्द्रप्रभा गुळिका (Bhaisajya Ratnavali)",
            classicalReference = "Bhaisajya Ratnavali",
            category = FormulationCategory.GULIKA,
            packing = "100 Nos.",
            tagPill = "URO-GENITAL TONIC",
            healthGoals = listOf(HealthGoal.DETOX, HealthGoal.IMMUNITY),
            shortDescription = "Premier classical formulation illuminating reproductive, urinary, and endocrine health with 37 bioactives.",
            primaryBenefit = "Mutrakrcchra (Dysuria), Prameha (Glycemic / Urinary balance), and lumbar vitality",
            doshaImpact = "Tridosha Rasayana (Restores Ojas and kidney channels)",
            targetDoshas = listOf(DoshaType.TRIDOSHIC),
            mainIngredientsText = "Chandraprabha, Vacha, Musta, Bhunimba, Shilajit, Guggulu",
            usageInstructionsText = "Internal: 1-2 tablets twice daily with water or milk",
            photoUrl = ClassicalPhotoPresets.GULIKA_TABLETS,
            constituents = listOf("Fulvic Acid (Shilajit)", "Guggulsterones", "Essential Bitters"),
            ingredients = listOf(
                AyurvedaIngredient(name = "Shuddha Shilajit", botanicalName = "Asphaltum punjabianum", partUsed = "Purified Mineral Resin", classicalRole = "Yogavahi, Rasayana, Mehadhara"),
                AyurvedaIngredient(name = "Shuddha Guggulu", botanicalName = "Commiphora mukul", partUsed = "Purified Oleo-resin", classicalRole = "Vedanasthapana, Medohara"),
                AyurvedaIngredient(name = "Chandraprabha (Karpoora)", botanicalName = "Cinnamomum camphora", partUsed = "Extract", classicalRole = "Srotovishodhana, Vata-hara"),
                AyurvedaIngredient(name = "Musta", botanicalName = "Cyperus rotundus", partUsed = "Tuber", classicalRole = "Deepana, Pachana, Kaphahara")
            ),
            dravyaguna = DravyagunaProfile(
                rasa = listOf("Tikta (Bitter)", "Katu (Pungent)", "Kashaya (Astringent)", "Madhura (Sweet)"),
                virya = "Sheeta (Balanced Cooling)",
                vipaka = "Madhura (Nourishing post-digestive)",
                guna = listOf("Laghu (Light)", "Snigdha (Nourishing)")
            ),
            dosage = DosageInfo(
                summary = "1 - 2 Tablets (500mg - 1g) • Twice Daily",
                standardDose = "1-2 Tablets",
                frequency = "Twice Daily",
                timing = "After Meals",
                anupana = "Warm Cow's Milk or lukewarm water",
                caution = "Take after food. Consult Vaidya during active uric acid flares."
            ),
            indications = listOf(
                "Polyuria (Prameha / Diabetic urinary symptoms)",
                "Dysuria (Mutrakrcchra / Painful micturition)",
                "Renal Calculi (Ashmari prevention)",
                "Anuria / Urinary frequency",
                "Hydrocele & Scrotal enlargement",
                "Anemia (Pandu) & Lower back exhaustion"
            ),
            contraindications = listOf("Severe hyperkalemia"),
            pathyaWholesome = listOf("Barley (Yava)", "Moong dal", "Old shali rice", "Pomegranate"),
            apathyaAvoid = listOf("Excessive curd", "Fermented foods", "Sedentary lifestyle"),
            stockUnits = 88,
            batchNumber = "SIT-GUL-2026-003"
        ),
        AyurvedaMedicine(
            id = "manasamithram_gulika",
            slNo = 23,
            name = "Manasamithram Gulika",
            sanskritName = "मानसामित्रं गुळिका (A.F.I. Part 1 Sahasrayogam)",
            classicalReference = "A.F.I. Part 1 Sahasrayogam",
            category = FormulationCategory.GULIKA,
            packing = "100 Nos.",
            tagPill = "MEDHYA NOOTROPIC",
            healthGoals = listOf(HealthGoal.COGNITION, HealthGoal.STRESS_RELIEF),
            shortDescription = "Prestigious neuro-psychiatric Ayurvedic tablet calming Prana Vata, anxiety, insomnia, and speech impediments.",
            primaryBenefit = "Mental tranquility, deep restorative sleep, and nervous system nourishment",
            doshaImpact = "Calms aggravated Prana Vata and Sadhaka Pitta",
            targetDoshas = listOf(DoshaType.VATA, DoshaType.PITTA),
            mainIngredientsText = "Bala, Nagabala, Bilva, Prisniparni, Pravala pishti, Swarna Bhasma",
            usageInstructionsText = "Internal: 1 tablet once or twice daily with warm milk",
            photoUrl = ClassicalPhotoPresets.GULIKA_TABLETS,
            constituents = listOf("Purified Coral (Pravala)", "Silver & Gold micro-bhasmas", "Nervine Alkaloids"),
            ingredients = listOf(
                AyurvedaIngredient(name = "Bala & Nagabala", botanicalName = "Sida cordifolia sp.", partUsed = "Root", classicalRole = "Balya, Brumhana, Vata-shamaka"),
                AyurvedaIngredient(name = "Bilwa", botanicalName = "Aegle marmelos", partUsed = "Root", classicalRole = "Dashamula nerve calmer"),
                AyurvedaIngredient(name = "Pravala Pishti", botanicalName = "Processed Coral calx", partUsed = "Purified Marine Calx", classicalRole = "Pitta-shamaka, Medhya"),
                AyurvedaIngredient(name = "Swarna Bhasma", botanicalName = "Purified Gold Calx", partUsed = "Micro-calx", classicalRole = "Supreme Rasayana, Ojas booster")
            ),
            dravyaguna = DravyagunaProfile(
                rasa = listOf("Madhura (Sweet)", "Tikta (Bitter)"),
                virya = "Sheeta (Cooling)",
                vipaka = "Madhura (Nourishing)",
                guna = listOf("Guru (Grounding)", "Snigdha (Unctuous)")
            ),
            dosage = DosageInfo(
                summary = "1 Tablet • Night Before Sleep or Twice Daily",
                standardDose = "1 Tablet (250mg)",
                frequency = "Once or Twice Daily",
                timing = "30 mins before sleep or after breakfast",
                anupana = "Warm Cow's Milk, Saraswatharishtam, or Brahmi Ghrita",
                caution = "Take only under classical Ayurvedic prescription."
            ),
            indications = listOf(
                "Psychiatric diseases (Unmada / Mental agitation)",
                "Epilepsy (Apasmara adjunctive support)",
                "Speech disorders & Stuttering",
                "Chronic stress & Panic tendencies",
                "Anxiety neurosis & Insomnia"
            ),
            contraindications = listOf("Do not exceed prescribed dosage"),
            pathyaWholesome = listOf("Meditation", "Warm milk with nutmeg", "Cow's ghee", "Sweet fruits"),
            apathyaAvoid = listOf("Excessive caffeine", "Violent screen stimulation", "Irregular sleep hours"),
            stockUnits = 55,
            batchNumber = "SIT-GUL-2026-023"
        ),
        AyurvedaMedicine(
            id = "ashwagandha_root",
            name = "Ashwagandha Root",
            sanskritName = "अश्वगंधा (Withania somnifera)",
            category = FormulationCategory.CHURNA,
            tagPill = "ADAPTOGEN",
            healthGoals = listOf(HealthGoal.STRESS_RELIEF, HealthGoal.IMMUNITY),
            shortDescription = "Supports stress reduction and cognitive focus. Part of your Morning Ritual.",
            primaryBenefit = "Somatic vitality, adrenal support, and deep restful sleep",
            doshaImpact = "Vata & Kapha Pacifying (Vata-Kapha Shamaka)",
            targetDoshas = listOf(DoshaType.VATA, DoshaType.KAPHA),
            constituents = listOf("Flavonoids", "Alkaloids", "Withanolides", "Sitoindosides"),
            ingredients = listOf(
                AyurvedaIngredient(
                    name = "Ashwagandha Root",
                    sanskritName = "अश्वगंधा",
                    botanicalName = "Withania somnifera",
                    partUsed = "Sun-dried Rhizome & Root",
                    classicalRole = "Balya (Strength giver), Rasayana (Rejuvenator), Medhya (Nervine tonic)"
                ),
                AyurvedaIngredient(
                    name = "Black Pepper (Bioenhancer)",
                    sanskritName = "मरिच",
                    botanicalName = "Piper nigrum",
                    partUsed = "Dried Fruit",
                    classicalRole = "Deepana & Sroto-shodhana (Improves cellular assimilation)"
                )
            ),
            dravyaguna = DravyagunaProfile(
                rasa = listOf("Tikta (Bitter)", "Kashaya (Astringent)", "Madhura (Sweet)"),
                virya = "Ushna (Heating / Energizing)",
                vipaka = "Madhura (Nourishing post-digestive)",
                guna = listOf("Laghu (Light)", "Snigdha (Unctuous)")
            ),
            dosage = DosageInfo(
                summary = "500mg • After Breakfast",
                standardDose = "500mg - 1000mg (1/2 to 1 teaspoon powder)",
                frequency = "Twice Daily",
                timing = "Morning after meal & 30 mins before sleep",
                anupana = "Warm Cow's Milk, Ghee, or warm water with honey",
                caution = "Use with care in high Pitta conditions with burning sensations."
            ),
            indications = listOf("Chronic mental fatigue", "Anxiety & restlessness", "Muscle debility", "Insomnia"),
            contraindications = listOf("Acute high fever (Ama condition)", "Severe thyrotoxicosis without supervision"),
            pathyaWholesome = listOf("Warm whole milk", "Soaked almonds", "Ghee", "Warm stewed apples"),
            apathyaAvoid = listOf("Excessive caffeine", "Cold raw dry salads", "Late night eating"),
            isDailyVitality = false
        ),
        AyurvedaMedicine(
            id = "triphala_churna",
            name = "Triphala Churna",
            sanskritName = "त्रिफला चूर्ण (Three Sacred Fruits)",
            category = FormulationCategory.CHURNA,
            tagPill = "DIGESTIVE DETOX",
            shortDescription = "Classic three-fruit formulation for gentle colon cleanse and systemic detox.",
            primaryBenefit = "Gentle bowel motility, antioxidant protection, ocular health",
            doshaImpact = "Tridoshic Harmony (Balances Vata, Pitta, Kapha)",
            targetDoshas = listOf(DoshaType.TRIDOSHIC, DoshaType.PITTA, DoshaType.KAPHA),
            constituents = listOf("Tannins", "Gallic Acid", "Vitamin C", "Chebulagic Acid"),
            ingredients = listOf(
                AyurvedaIngredient(
                    name = "Amalaki (Indian Gooseberry)",
                    sanskritName = "आमलकी",
                    botanicalName = "Emblica officinalis",
                    partUsed = "Dried Pericarp",
                    classicalRole = "Rich natural Vitamin C, cooling Pitta regulator, Rasayana"
                ),
                AyurvedaIngredient(
                    name = "Bibhitaki (Belliric Myrobalan)",
                    sanskritName = "बिभीतकी",
                    botanicalName = "Terminalia bellirica",
                    partUsed = "Fruit Peel",
                    classicalRole = "Pacifies Kapha, supports lungs and mucosal health"
                ),
                AyurvedaIngredient(
                    name = "Haritaki (Chebulic Myrobalan)",
                    sanskritName = "हरीतकी",
                    botanicalName = "Terminalia chebula",
                    partUsed = "Dried Fruit",
                    classicalRole = "King of Medicines; scrapes Ama (toxins) and pacifies Vata"
                )
            ),
            dravyaguna = DravyagunaProfile(
                rasa = listOf("Pancha-Rasa (Contains 5 tastes except salty Lavana)"),
                virya = "Sheeta & Anushna (Balanced temperature)",
                vipaka = "Madhura (Sweet post-digestive)",
                guna = listOf("Laghu (Light)", "Ruksha (Dry)")
            ),
            dosage = DosageInfo(
                summary = "3g - 5g • At Bedtime",
                standardDose = "1/2 to 1 teaspoon (3g - 5g)",
                frequency = "Once daily before sleep",
                timing = "30-45 minutes after dinner before bed",
                anupana = "Warm water or equal parts ghee and raw honey",
                caution = "Avoid during acute diarrhea or early pregnancy."
            ),
            indications = listOf("Sluggish bowel habits", "Toxin accumulation (Ama)", "Eye fatigue", "Weak metabolism"),
            contraindications = listOf("Dysentery", "Acute dehydration"),
            pathyaWholesome = listOf("Warm water throughout the day", "Steamed vegetables", "Moong dal soup"),
            apathyaAvoid = listOf("Fried heavy snacks", "Cold curd", "Processed flour")
        ),
        AyurvedaMedicine(
            id = "brahmi_vati",
            name = "Brahmi Vati",
            sanskritName = "ब्राह्मी वटी (Classical Cognitive Tablet)",
            category = FormulationCategory.VATI,
            tagPill = "NOOTROPIC",
            shortDescription = "Enhances memory retention, concentration, and soothes emotional agitation.",
            primaryBenefit = "Brain fog clearance, memory enhancement, nervous equilibrium",
            doshaImpact = "Pacifies Sadhaka Pitta & Prana Vata",
            targetDoshas = listOf(DoshaType.VATA, DoshaType.PITTA),
            constituents = listOf("Bacosides A & B", "Alkaloids", "Sterols"),
            ingredients = listOf(
                AyurvedaIngredient(
                    name = "Brahmi Herb",
                    sanskritName = "ब्राह्मी",
                    botanicalName = "Bacopa monnieri",
                    partUsed = "Whole Aerial Plant",
                    classicalRole = "Medhya Rasayana (Potent neuroprotective adaptogen)"
                ),
                AyurvedaIngredient(
                    name = "Shankhpushpi",
                    sanskritName = "शंखपुष्पी",
                    botanicalName = "Convolvulus pluricaulis",
                    partUsed = "Whole Herb",
                    classicalRole = "Calms nervous excitement and promotes cerebral microcirculation"
                ),
                AyurvedaIngredient(
                    name = "Vacha (Sweet Flag)",
                    sanskritName = "वचा",
                    botanicalName = "Acorus calamus",
                    partUsed = "Purified Rhizome",
                    classicalRole = "Speech clarity, cognitive sharpness, clears srotas"
                )
            ),
            dravyaguna = DravyagunaProfile(
                rasa = listOf("Tikta (Bitter)", "Kashaya (Astringent)"),
                virya = "Sheeta (Cooling to mind & blood)",
                vipaka = "Madhura",
                guna = listOf("Laghu (Light)", "Sara (Promotes gentle movement)")
            ),
            dosage = DosageInfo(
                summary = "1-2 Tablets • After Meals",
                standardDose = "1 to 2 tablets (250mg - 500mg)",
                frequency = "Twice daily",
                timing = "After breakfast and lunch",
                anupana = "Warm water, warm milk, or Brahmi Ghrita",
                caution = "Take after food if prone to mild gastric sensitivity."
            ),
            indications = listOf("Examination stress", "Poor memory recall", "Mental fatigue", "Restless thoughts"),
            contraindications = listOf("Hypersensitivity to herbal bitters"),
            pathyaWholesome = listOf("A2 Cow Ghee", "Walnuts", "Pomegranate", "Fresh coconut water"),
            apathyaAvoid = listOf("Excessive green chilies", "Fermented alcohol", "Excessive screen time before bed")
        ),
        AyurvedaMedicine(
            id = "chyawanprash_awaleha",
            name = "Chyawanprash Awaleha",
            sanskritName = "च्यवनप्राश अवलेह (The Grand Vitality Jam)",
            category = FormulationCategory.RASAYANA,
            tagPill = "IMMUNITY BOOSTER",
            shortDescription = "Classical multi-herb botanical jam prepared in fresh amla, ghee, and honey.",
            primaryBenefit = "Deep immune barrier (Ojas), respiratory defense, tissue anti-aging",
            doshaImpact = "Tridoshic Balancer (Nourishes all 7 Dhatus)",
            targetDoshas = listOf(DoshaType.TRIDOSHIC, DoshaType.VATA, DoshaType.KAPHA),
            constituents = listOf("Bioflavonoids", "Ascorbic Acid", "Essential Fatty Acids", "Polyphenols"),
            ingredients = listOf(
                AyurvedaIngredient(
                    name = "Fresh Amla (Gooseberry)",
                    sanskritName = "आमलकी",
                    botanicalName = "Phyllanthus emblica",
                    partUsed = "Pulp of Fresh Wild Berries",
                    classicalRole = "Dominant ingredient (60%+); supreme Rasayana and cellular protector"
                ),
                AyurvedaIngredient(
                    name = "Dashamula Complex",
                    sanskritName = "दशमूल",
                    botanicalName = "Ten Sacred Roots",
                    partUsed = "Decoction of 10 Classical Roots",
                    classicalRole = "Strengthens respiratory system and deep organ vitality"
                ),
                AyurvedaIngredient(
                    name = "Pippali (Long Pepper)",
                    sanskritName = "पिप्पली",
                    botanicalName = "Piper longum",
                    partUsed = "Dried Spikes",
                    classicalRole = "Pranavaha Srotas rejuvenator; clears bronchial phlegm"
                ),
                AyurvedaIngredient(
                    name = "Pure Cow's Ghee & Sesame Oil",
                    sanskritName = "घृत एवं तैल",
                    botanicalName = "A2 Ghee & Sesamum indicum",
                    partUsed = "Cold-pressed / Cultured base",
                    classicalRole = "Lipid carrier driving herbs deep into cellular membranes"
                )
            ),
            dravyaguna = DravyagunaProfile(
                rasa = listOf("Madhura (Sweet)", "Amla (Sour)", "Tikta (Bitter)", "Katu (Pungent)"),
                virya = "Sheeta-Ushna Samashitoshna (Balanced)",
                vipaka = "Madhura",
                guna = listOf("Guru (Nourishing / Heavy)", "Snigdha (Unctuous)")
            ),
            dosage = DosageInfo(
                summary = "10g - 15g • Early Morning",
                standardDose = "1 tablespoon (12g - 15g)",
                frequency = "Once or twice daily",
                timing = "Morning on empty stomach or before breakfast",
                anupana = "Warm cow's milk or warm water",
                caution = "Diabetic individuals should consult practitioner due to jaggery/honey content."
            ),
            indications = listOf("Frequent seasonal colds", "General debility", "Weak lungs", "Post-illness fatigue"),
            contraindications = listOf("Acute uncontrolled hyperglycemia"),
            pathyaWholesome = listOf("Warm nourishing grains", "Warm milk with a pinch of turmeric", "Dates"),
            apathyaAvoid = listOf("Refrigerated drinks", "Ice creams", "Stale leftover food")
        ),
        AyurvedaMedicine(
            id = "kumkumadi_tailam",
            name = "Kumkumadi Tailam",
            sanskritName = "कुमकुमादि तैलम् (Miraculous Saffron Oil)",
            category = FormulationCategory.TAILA,
            tagPill = "SKIN RADIANCE",
            shortDescription = "Artisanal saffron-infused elixir crafted for blemishes and golden skin luster.",
            primaryBenefit = "Clears pigmentation, softens texture, imparts natural radiance",
            doshaImpact = "Pacifies Pitta and Rakta (Blood tissue)",
            targetDoshas = listOf(DoshaType.PITTA),
            constituents = listOf("Crocin", "Safranal", "Glycyrrhizin", "Santalols"),
            ingredients = listOf(
                AyurvedaIngredient(
                    name = "Kashmiri Saffron",
                    sanskritName = "कुंकुम",
                    botanicalName = "Crocus sativus",
                    partUsed = "Crimson Stigmas",
                    classicalRole = "Varnya (Complexion enhancer) and blood purifier"
                ),
                AyurvedaIngredient(
                    name = "Rakta Chandana (Red Sandalwood)",
                    sanskritName = "रक्त चन्दन",
                    botanicalName = "Pterocarpus santalinus",
                    partUsed = "Heartwood",
                    classicalRole = "Deeply cooling, eliminates sun pigmentation and redness"
                ),
                AyurvedaIngredient(
                    name = "Manjistha (Indian Madder)",
                    sanskritName = "मञ्जिष्ठा",
                    botanicalName = "Rubia cordifolia",
                    partUsed = "Stems & Roots",
                    classicalRole = "Premier lymph and micro-capillary purifier"
                ),
                AyurvedaIngredient(
                    name = "Pure Sesame Oil base",
                    sanskritName = "तिल तैल",
                    botanicalName = "Sesamum indicum",
                    partUsed = "Cold-pressed seed oil",
                    classicalRole = "Penetrates all 7 layers of the epidermis (*Twak*)"
                )
            ),
            dravyaguna = DravyagunaProfile(
                rasa = listOf("Madhura", "Tikta"),
                virya = "Anushna (Mild soothing warmth)",
                vipaka = "Madhura",
                guna = listOf("Snigdha (Hydrating)", "Sukshma (Deeply penetrative)")
            ),
            dosage = DosageInfo(
                summary = "3-4 Drops • Night Ritual",
                standardDose = "3 to 5 drops",
                frequency = "Once daily before sleep",
                timing = "Night after washing face with pure rosewater",
                anupana = "Topical application gently massaged upwards",
                caution = "For external facial use only."
            ),
            indications = listOf("Uneven skin tone", "Under-eye dark circles", "Blemish scars", "Dry patches"),
            contraindications = listOf("Active cystic pus-filled acne flare-up"),
            pathyaWholesome = listOf("Amla juice", "Watermelon", "Cilantro tea", "Hydrating water"),
            apathyaAvoid = listOf("Excessive direct sun without shade", "Excessive deep-fried salty snacks")
        ),
        AyurvedaMedicine(
            id = "amritarishta",
            name = "Amritarishta",
            sanskritName = "अमृतातर्ष (Fermented Giloy Nectar)",
            category = FormulationCategory.ARISHTA,
            tagPill = "LIVER & FEVER DETOX",
            shortDescription = "Naturally fermented herbal tonic targeting deep metabolic endotoxins.",
            primaryBenefit = "Clears stubborn metabolic toxins, strengthens spleen and liver function",
            doshaImpact = "Pacifies Pitta and Kapha",
            targetDoshas = listOf(DoshaType.PITTA, DoshaType.KAPHA),
            constituents = listOf("Tinosporaside", "Cordifolioside", "Self-generated herbal bio-alcohols"),
            ingredients = listOf(
                AyurvedaIngredient(
                    name = "Guduchi (Giloy / Amrita)",
                    sanskritName = "गुडूची",
                    botanicalName = "Tinospora cordifolia",
                    partUsed = "Fresh Stems",
                    classicalRole = "Jwarahara (Fever pacifier) and deep immunomodulator"
                ),
                AyurvedaIngredient(
                    name = "Dashamula",
                    sanskritName = "दशमूल",
                    botanicalName = "Ten Root Decoction",
                    partUsed = "Roots",
                    classicalRole = "Relieves inflammatory body aches"
                ),
                AyurvedaIngredient(
                    name = "Dhataki Flowers",
                    sanskritName = "धातकी",
                    botanicalName = "Woodfordia fruticosa",
                    partUsed = "Dried Blossoms",
                    classicalRole = "Natural fermentation initiator producing fine biological delivery"
                )
            ),
            dravyaguna = DravyagunaProfile(
                rasa = listOf("Tikta (Intensely bitter)", "Kashaya (Astringent)"),
                virya = "Ushna (Penetrative)",
                vipaka = "Madhura",
                guna = listOf("Laghu (Rapidly absorbed)", "Tikshna")
            ),
            dosage = DosageInfo(
                summary = "15ml - 20ml • After Lunch & Dinner",
                standardDose = "15ml to 25ml",
                frequency = "Twice daily",
                timing = "Immediately after lunch and dinner",
                anupana = "Diluted with equal quantity (1:1) of warm water",
                caution = "Do not take undiluted on completely empty stomach."
            ),
            indications = listOf("Post-viral lethargy", "Low digestive fire", "Chronic recurrent low-grade fever"),
            contraindications = listOf("Acute bleeding ulcers", "Children under 5 without medical supervision"),
            pathyaWholesome = listOf("Light khichdi", "Boiled vegetable broth", "Pomegranate"),
            apathyaAvoid = listOf("Heavy red meat", "Excessive sour pickles", "Stale curd")
        ),
        AyurvedaMedicine(
            id = "dashamula_kwatha",
            name = "Dashamula Kwatha",
            sanskritName = "दशमूल क्वाथ (Ten Roots Decoction)",
            category = FormulationCategory.KWATHA,
            tagPill = "VATA HARMONY",
            shortDescription = "Sacred decoction of ten forest roots that grounds aggravated Vata dosha.",
            primaryBenefit = "Joint comfort, nerve relaxation, easing menstrual cramps & stiffness",
            doshaImpact = "Supreme Vata Shamaka (Grounds erratic nervous energy)",
            targetDoshas = listOf(DoshaType.VATA),
            constituents = listOf("Flavonoid Glycosides", "Sitosterols", "Lupenone"),
            ingredients = listOf(
                AyurvedaIngredient(
                    name = "Bilva (Bael root)",
                    sanskritName = "बिल्व",
                    botanicalName = "Aegle marmelos",
                    partUsed = "Root Bark",
                    classicalRole = "Pacifies Vata-Kapha and supports intestinal gut lining"
                ),
                AyurvedaIngredient(
                    name = "Agnimantha",
                    sanskritName = "अग्निमन्थ",
                    botanicalName = "Premna integrifolia",
                    partUsed = "Root",
                    classicalRole = "Anti-inflammatory and relieves nerve tension"
                ),
                AyurvedaIngredient(
                    name = "Gokshura (Small Caltrops)",
                    sanskritName = "गोक्षुर",
                    botanicalName = "Tribulus terrestris",
                    partUsed = "Roots & Fruit",
                    classicalRole = "Urinary soothing and lower back support"
                )
            ),
            dravyaguna = DravyagunaProfile(
                rasa = listOf("Kashaya (Astringent)", "Tikta (Bitter)"),
                virya = "Ushna (Warm & soothing)",
                vipaka = "Katu",
                guna = listOf("Guru", "Ruksha")
            ),
            dosage = DosageInfo(
                summary = "40ml - 50ml • Twice Daily",
                standardDose = "40ml to 60ml freshly steeped",
                frequency = "Twice daily",
                timing = "30 minutes before meals",
                anupana = "Warm water or with a pinch of fresh ginger paste",
                caution = "Take warm; do not drink cold decoctions."
            ),
            indications = listOf("Lower back ache", "Sciatica stiffness", "Severe postpartum recovery", "Dry cough"),
            contraindications = listOf("Dehydration with severe burning sensation"),
            pathyaWholesome = listOf("Warm cooked oatmeal", "Ghee", "Warm sesame oil massages"),
            apathyaAvoid = listOf("Cold windy exposure", "Carbonated chilled water", "Dry dry cereals")
        ),
        AyurvedaMedicine(
            id = "gokshuradi_guggulu",
            name = "Gokshuradi Guggulu",
            sanskritName = "गोक्षुरादि गुग्गुलु (Renal & Fluid Balance Tablet)",
            category = FormulationCategory.VATI,
            tagPill = "URINARY & DETOX",
            shortDescription = "Synergistic resin tablet for kidney micro-circulation and fluid balance.",
            primaryBenefit = "Uric acid equilibrium, urinary comfort, joint stiffness relief",
            doshaImpact = "Pacifies Vata, Pitta, and Kapha in urinary channels",
            targetDoshas = listOf(DoshaType.PITTA, DoshaType.VATA),
            constituents = listOf("Guggulsterones", "Saponins", "Resins"),
            ingredients = listOf(
                AyurvedaIngredient(
                    name = "Gokshura",
                    sanskritName = "गोक्षुर",
                    botanicalName = "Tribulus terrestris",
                    partUsed = "Fruit & Root",
                    classicalRole = "Mutrala (Diuretic without potassium loss) and kidney tonic"
                ),
                AyurvedaIngredient(
                    name = "Shuddha Guggulu (Purified Resin)",
                    sanskritName = "शुद्ध गुग्गुलु",
                    botanicalName = "Commiphora mukul",
                    partUsed = "Purified Exudate",
                    classicalRole = "Scrapes metabolic crystallization from micro-channels"
                ),
                AyurvedaIngredient(
                    name = "Musta (Nut Grass)",
                    sanskritName = "मुस्ता",
                    botanicalName = "Cyperus rotundus",
                    partUsed = "Rhizome",
                    classicalRole = "Digestive fire promoter, relieves burning micturition"
                )
            ),
            dravyaguna = DravyagunaProfile(
                rasa = listOf("Madhura", "Tikta", "Katu"),
                virya = "Sheeta-Ushna (Neutralizing)",
                vipaka = "Madhura",
                guna = listOf("Laghu", "Ruksha")
            ),
            dosage = DosageInfo(
                summary = "2 Tablets • Twice Daily",
                standardDose = "1 to 2 tablets (500mg each)",
                frequency = "Twice daily",
                timing = "After breakfast and after dinner",
                anupana = "Warm water or Punarnavadi Kwatha",
                caution = "Stay well hydrated throughout the day while taking."
            ),
            indications = listOf("Elevated uric acid", "Joint aches with swelling", "Urinary tract irritation"),
            contraindications = listOf("Kidney failure requiring dialysis without nephrologist approval"),
            pathyaWholesome = listOf("Barley water", "Cucumber", "Coriander seed infusion", "Fresh coconut water"),
            apathyaAvoid = listOf("High purine red meat", "Refined white sugar", "Alcohol", "Sour vinegar")
        )
    )

    val defaultDailyDoses: List<DailyDoseLog> = listOf(
        DailyDoseLog(
            id = "dose_ashwagandha",
            medicineId = "ashwagandha_root",
            medicineName = "Ashwagandha Root",
            doseLabel = "500mg",
            timing = "After Breakfast",
            iconEmoji = "🍵",
            isLogged = true,
            loggedAtTime = "8:15 AM"
        ),
        DailyDoseLog(
            id = "dose_triphala",
            medicineId = "triphala_churna",
            medicineName = "Triphala Churna",
            doseLabel = "3g",
            timing = "Before Bedtime",
            iconEmoji = "🌿",
            isLogged = false,
            loggedAtTime = null
        ),
        DailyDoseLog(
            id = "dose_brahmi",
            medicineId = "brahmi_vati",
            medicineName = "Brahmi Vati",
            doseLabel = "1 Tablet",
            timing = "After Lunch",
            iconEmoji = "✨",
            isLogged = false,
            loggedAtTime = null
        )
    )

    val defaultHabits: List<DailyHabit> = listOf(
        DailyHabit(
            id = "habit_pranayama",
            title = "Afternoon Pranayama",
            scheduledTime = "4:30 PM",
            iconEmoji = "🧘",
            description = "10 minutes of Nadi Shodhana (Alternate Nostril Breathing) for calming nervous tension.",
            isCompleted = false
        ),
        DailyHabit(
            id = "habit_gandusha",
            title = "Morning Oil Pulling (Gandusha)",
            scheduledTime = "7:00 AM",
            iconEmoji = "🪥",
            description = "Swish 1 tbsp warm sesame oil for 5-10 minutes to strengthen gums and draw oral toxins.",
            isCompleted = true
        ),
        DailyHabit(
            id = "habit_golden_milk",
            title = "Evening Golden Milk (Haldi Doodh)",
            scheduledTime = "9:30 PM",
            iconEmoji = "🥛",
            description = "Warm milk with turmeric, crushed black pepper, and nutmeg for deep restorative sleep.",
            isCompleted = false
        )
    )

    val prakritiQuestions: List<PrakritiQuestion> = listOf(
        PrakritiQuestion(
            id = 1,
            trait = "Body Frame & Physical Build",
            optionVata = "Slender, light, prominent joints, difficulty gaining weight",
            optionPitta = "Medium frame, moderate muscular tone, steady weight",
            optionKapha = "Broad frame, sturdy build, tendency to gain weight easily"
        ),
        PrakritiQuestion(
            id = 2,
            trait = "Digestive Fire (Agni) & Appetite",
            optionVata = "Irregular: sometimes voracious, sometimes forget to eat; prone to gas",
            optionPitta = "Strong & fiery: irritable if meals are delayed, fast digestion",
            optionKapha = "Slow & steady: can comfortably skip meals, slow metabolism"
        ),
        PrakritiQuestion(
            id = 3,
            trait = "Mental Temperament & Reaction to Stress",
            optionVata = "Quick, imaginative, prone to worry, anxiety, and scattered thoughts",
            optionPitta = "Sharp, focused, ambitious, prone to impatience, anger, and perfectionism",
            optionKapha = "Calm, affectionate, steady, resistant to sudden change, unhurried"
        ),
        PrakritiQuestion(
            id = 4,
            trait = "Sleep Quality & Patterns",
            optionVata = "Light, restless, prone to waking up between 2 AM and 4 AM",
            optionPitta = "Moderate (6-7 hrs), vivid dreams, wakes up alert and ready",
            optionKapha = "Deep, heavy (8+ hrs), difficult to wake up in early morning"
        )
    )

    val defaultUsers: List<AppUser> = listOf(
        AppUser(
            id = "user_admin_jerin",
            name = "Jerin MR",
            email = "sys.jerin@gmail.com",
            role = UserRole.ADMIN,
            prakriti = DoshaType.TRIDOSHIC,
            status = UserStatus.ACTIVE,
            designation = "Chief Administrator & System Director",
            phone = "+91 98450 11001",
            registeredDate = "Oct 15, 2024",
            lastActive = "Active now",
            adherencePercent = 98,
            clinicalNotes = "Primary system administrator. Full clinical pharmacopoeia, formulation inventory, and user directory management."
        ),
        AppUser(
            id = "user_practitioner_meera",
            name = "Dr. Meera Nambiar",
            email = "dr.meera@ayurguide.org",
            role = UserRole.PRACTITIONER,
            prakriti = DoshaType.PITTA,
            status = UserStatus.ACTIVE,
            designation = "Senior Ayurvedic Physician (BAMS, MD)",
            phone = "+91 98450 22002",
            registeredDate = "Nov 02, 2024",
            lastActive = "12 mins ago",
            adherencePercent = 94,
            clinicalNotes = "Specialist in Dravyaguna (Herbal pharmacology) & Kayachikitsa."
        ),
        AppUser(
            id = "user_patient_arjun",
            name = "Arjun Mehta",
            email = "arjun.m@example.com",
            role = UserRole.PATIENT,
            prakriti = DoshaType.VATA,
            status = UserStatus.ACTIVE,
            designation = "Wellness Seeker",
            phone = "+91 98450 44004",
            registeredDate = "Jan 12, 2026",
            lastActive = "Just now",
            adherencePercent = 88,
            assignedPractitioner = "Dr. Meera Nambiar",
            clinicalNotes = "Practicing Dinacharya routine and herbal tea regimen for grounding nervous system."
        )
    )

    val defaultAuditLogs: List<AuditLogEntry> = listOf(
        AuditLogEntry(
            id = "log_1",
            timestamp = "10:45 AM Today",
            actorName = "Jerin MR (Admin)",
            actionType = "FORMULATION_VERIFY",
            targetItem = "Ashwagandha Churna",
            details = "Batch #AYUR-2026-B12 passed heavy metals & microbial purity assays under API guidelines.",
            isWarning = false
        ),
        AuditLogEntry(
            id = "log_2",
            timestamp = "09:30 AM Today",
            actorName = "Dr. Meera Nambiar (Vaidya)",
            actionType = "PRESCRIPTION_ISSUED",
            targetItem = "Triphala Churna (3g)",
            details = "Prescribed to patient Arjun Mehta for Pitta digestive regulation with warm water anupana.",
            isWarning = false
        ),
        AuditLogEntry(
            id = "log_3",
            timestamp = "Yesterday 04:15 PM",
            actorName = "Inventory System",
            actionType = "LOW_STOCK_ALERT",
            targetItem = "Kumkumadi Tailam",
            details = "Stock dropped to 8 units. Automated re-order threshold reached for raw Saffron (Kumkuma).",
            isWarning = true
        ),
        AuditLogEntry(
            id = "log_4",
            timestamp = "Yesterday 11:20 AM",
            actorName = "Jerin MR (Admin)",
            actionType = "ADMIN_ELEVATION",
            targetItem = "System Security Policy",
            details = "Strict RBAC privilege barrier enforced: Only Admin can elevate users to Admin privilege.",
            isWarning = false
        ),
        AuditLogEntry(
            id = "log_5",
            timestamp = "Mar 01, 2026",
            actorName = "Jerin MR (Admin)",
            actionType = "USER_REGISTERED",
            targetItem = "Dr. Meera Nambiar",
            details = "Ayurvedic physician clinical credentials verified and onboarded into active directory.",
            isWarning = false
        )
    )
}
