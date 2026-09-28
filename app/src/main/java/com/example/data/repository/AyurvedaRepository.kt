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
            id = "SA-88225",
            slNo = 1,
            name = "Dashamoolarishtam",
            sanskritName = "दशमूलारिष्टम् (Sharangadhara Samhita)",
            classicalReference = "Sharangadhara Samhita",
            category = FormulationCategory.ARISHTAM,
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
            id = "SA-00001",
            slNo = 2,
            name = "Abhayarishtam",
            sanskritName = "अभयारिष्टम् (Ashtangahrudayam)",
            classicalReference = "Ashtangahrudayam, Arshorogadhikaram",
            category = FormulationCategory.ARISHTAM,
            packing = "450 ml, 200 ml",
            tagPill = "DIGESTIVE ELIXIR",
            healthGoals = listOf(HealthGoal.DIGESTION, HealthGoal.DETOX),
            shortDescription = "Classic Ayurvedic fermented formulation indicated primarily for hemorrhoids, sluggish digestion, and chronic constipation.",
            primaryBenefit = "Arshas (Hemorrhoids) relief, Apana Vata normalization and bowel peristalsis",
            doshaImpact = "Vata & Kapha Shamaka (Kindles digestive Agni)",
            targetDoshas = listOf(DoshaType.VATA, DoshaType.KAPHA),
            mainIngredientsText = "Abhaya (Terminalia chebula), Dhatri (Emblica officinalis), Kapitha, Vishala",
            usageInstructionsText = "15 to 25 ml twice daily after meals with equal quantity of warm water.",
            photoUrl = ClassicalPhotoPresets.ARISHTA_BOTTLE,
            constituents = listOf("Bio-generated Alcohol (5-10%)", "Tannins", "Chebulic Acid", "Anthraquinones"),
            ingredients = listOf(
                AyurvedaIngredient(name = "Abhaya (Haritaki)", botanicalName = "Terminalia chebula", partUsed = "Fruit Pericarp", classicalRole = "Deepana, Pachana, Arshoghna"),
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
                standardDose = "15 to 25 ml",
                frequency = "Twice Daily",
                timing = "After Meals",
                anupana = "Equal quantity of warm water",
                caution = "Not recommended for children under 5 without Vaidya consultation."
            ),
            indications = listOf(
                "Arshas (Hemorrhoids / Piles)",
                "Udara (Abdominal disorders)",
                "Vibanda (Constipation)",
                "Agnimandya (Impaired digestion)"
            ),
            contraindications = listOf("Severe active peptic ulcers", "Acute diarrhea"),
            pathyaWholesome = listOf("Buttermilk (Takra)", "Fiber-rich leafy vegetables", "Warm water"),
            apathyaAvoid = listOf("Excessive dry spicy foods", "Suppression of natural urges"),
            stockUnits = 120,
            batchNumber = "SIT-ARI-2026-001"
        ),
        AyurvedaMedicine(
            id = "SA-00002",
            slNo = 3,
            name = "Amritarishtam",
            sanskritName = "अमृतारिष्टम् (Bhaishajya Ratnavali)",
            classicalReference = "Bhaishajya Ratnavali, Jwaradhikaram",
            category = FormulationCategory.ARISHTAM,
            packing = "450 ml",
            tagPill = "IMMUNO-FEBRIFUGE",
            healthGoals = listOf(HealthGoal.IMMUNITY, HealthGoal.DETOX),
            shortDescription = "Premier Ayurvedic formulation for acute and relapsing fevers, tonsillitis, and sluggish digestion.",
            primaryBenefit = "Jwara (Fever) resolution, immunomodulation, and deep lymphatic Ama clearance",
            doshaImpact = "Tridosha Shamaka (Primarily Pitta-Kapha Hara)",
            targetDoshas = listOf(DoshaType.PITTA, DoshaType.KAPHA),
            mainIngredientsText = "Amritha (Guduchi), Bilwa, Syonaka, Gambhari",
            usageInstructionsText = "15 to 25 ml twice daily after food with equal quantity of water.",
            photoUrl = ClassicalPhotoPresets.ARISHTA_BOTTLE,
            constituents = listOf("Guduchi Alkaloids", "Flavonoids", "Glycosides", "Bitter Tonics"),
            ingredients = listOf(
                AyurvedaIngredient(name = "Amritha (Guduchi)", botanicalName = "Tinospora cordifolia", partUsed = "Stem", classicalRole = "Jwaraghna (Antipyretic), Rasayana"),
                AyurvedaIngredient(name = "Bilwa", botanicalName = "Aegle marmelos", partUsed = "Root Bark", classicalRole = "Dashamula constituent"),
                AyurvedaIngredient(name = "Syonaka", botanicalName = "Oroxylum indicum", partUsed = "Root Bark", classicalRole = "Shothahara"),
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
                standardDose = "15 to 25 ml",
                frequency = "Twice Daily",
                timing = "After Meals",
                anupana = "Equal volume of boiled and cooled water",
                caution = "Take after food to avoid gastric irritation."
            ),
            indications = listOf(
                "Jwara (Chronic & intermittent fevers)",
                "Jeerna Jwara (Post-fever debility)",
                "Ajeerna (Indigestion)",
                "Yakrit roga (Hepatic sluggishness)"
            ),
            contraindications = listOf("Active hyperacidity during empty stomach"),
            pathyaWholesome = listOf("Green gram soup (Mudga Yusha)", "Boiled vegetables", "Warm water"),
            apathyaAvoid = listOf("Oily heavy curds", "Day sleeping", "Cold refrigerated beverages"),
            stockUnits = 95,
            batchNumber = "SIT-ARI-2026-002"
        ),
        AyurvedaMedicine(
            id = "SA-00003",
            slNo = 4,
            name = "Ashokarishtam",
            sanskritName = "अशोकारिष्टम् (Bhaishajya Ratnavali)",
            classicalReference = "Bhaishajya Ratnavali, Pradaradhikaram",
            category = FormulationCategory.ARISHTAM,
            packing = "450 ml, 200 ml",
            tagPill = "HORMONAL HARMONY",
            healthGoals = listOf(HealthGoal.IMMUNITY, HealthGoal.DETOX),
            shortDescription = "Classical uterine tonic indicated for hormonal harmony, excessive menstrual bleeding, and pelvic comfort.",
            primaryBenefit = "Menstrual cycle regulation, pelvic strength, and uterine tissue nourishment",
            doshaImpact = "Pitta-Kapha Shamaka",
            targetDoshas = listOf(DoshaType.PITTA, DoshaType.KAPHA),
            mainIngredientsText = "Ashoka (Saraca asoca), Dhataki (Woodfordia fruticosa), Musta (Cyperus rotundus), Haritaki",
            usageInstructionsText = "15 to 25 ml twice daily after food or as directed by the physician.",
            photoUrl = ClassicalPhotoPresets.ARISHTA_BOTTLE,
            constituents = listOf("Saracin", "Tannins", "Phytosterols", "Natural Bio-ethanol"),
            ingredients = listOf(
                AyurvedaIngredient(name = "Ashoka", botanicalName = "Saraca asoca", partUsed = "Stem Bark", classicalRole = "Kashaya, Grahi, Artava-regulatory"),
                AyurvedaIngredient(name = "Dhataki", botanicalName = "Woodfordia fruticosa", partUsed = "Flowers", classicalRole = "Fermentation agent"),
                AyurvedaIngredient(name = "Musta", botanicalName = "Cyperus rotundus", partUsed = "Rhizome", classicalRole = "Deepana, Pachana, Srotoshodhana"),
                AyurvedaIngredient(name = "Haritaki", botanicalName = "Terminalia chebula", partUsed = "Fruit Pericarp", classicalRole = "Tridosha shamaka")
            ),
            dravyaguna = DravyagunaProfile(
                rasa = listOf("Kashaya (Astringent)", "Tikta (Bitter)"),
                virya = "Sheeta (Cooling)",
                vipaka = "Katu (Pungent)",
                guna = listOf("Laghu (Light)", "Ruksha (Dry)")
            ),
            dosage = DosageInfo(
                summary = "15-25 ml • Twice Daily After Food",
                standardDose = "15 to 25 ml",
                frequency = "Twice Daily",
                timing = "After food",
                anupana = "Equal volume of lukewarm water",
                caution = "Not recommended during active pregnancy."
            ),
            indications = listOf(
                "Asrigdara (Menorrhagia)",
                "Pradara (Leucorrhea)",
                "Katishoola (Low back pain)",
                "Shweta Pradara (Pelvic discomfort)"
            ),
            contraindications = listOf("Pregnancy", "Amenorrhea without medical consultation"),
            pathyaWholesome = listOf("Nutritious light meals", "Pomegranate", "Milk", "Ghee"),
            apathyaAvoid = listOf("Excessive pungent spices", "Late night heavy meals", "Physical strain"),
            stockUnits = 110,
            batchNumber = "SIT-ARI-2026-003"
        ),
        AyurvedaMedicine(
            id = "SA-13160",
            slNo = 5,
            name = "Triphala Churna",
            sanskritName = "त्रिफला चूर्ण (Three Sacred Fruits)",
            classicalReference = "Charaka Samhita",
            category = FormulationCategory.CHOORNAMS,
            packing = "100 g, 200 g",
            tagPill = "DIGESTIVE DETOX",
            healthGoals = listOf(HealthGoal.DIGESTION, HealthGoal.DETOX),
            shortDescription = "Classic three-fruit formulation for gentle colon cleanse and systemic detox.",
            primaryBenefit = "Gentle bowel motility, antioxidant protection, ocular health",
            doshaImpact = "Tridoshic Harmony (Balances Vata, Pitta, Kapha)",
            targetDoshas = listOf(DoshaType.TRIDOSHIC, DoshaType.PITTA, DoshaType.KAPHA),
            constituents = listOf("Tannins", "Gallic Acid", "Vitamin C", "Chebulagic Acid"),
            ingredients = listOf(
                AyurvedaIngredient(name = "Amalaki (Indian Gooseberry)", botanicalName = "Emblica officinalis", partUsed = "Dried Pericarp", classicalRole = "Natural Vitamin C, cooling Pitta regulator"),
                AyurvedaIngredient(name = "Bibhitaki (Belliric Myrobalan)", botanicalName = "Terminalia bellirica", partUsed = "Fruit Peel", classicalRole = "Pacifies Kapha, supports mucosal health"),
                AyurvedaIngredient(name = "Haritaki (Chebulic Myrobalan)", botanicalName = "Terminalia chebula", partUsed = "Dried Fruit", classicalRole = "Scrapes Ama and pacifies Vata")
            ),
            dravyaguna = DravyagunaProfile(
                rasa = listOf("Pancha-Rasa (5 tastes except Salty)"),
                virya = "Sheeta & Anushna (Balanced temperature)",
                vipaka = "Madhura (Sweet post-digestive)",
                guna = listOf("Laghu (Light)", "Ruksha (Dry)")
            ),
            dosage = DosageInfo(
                summary = "3g - 6g • At Bedtime with warm water",
                standardDose = "3-6g with warm water at bedtime",
                frequency = "Once daily before sleep",
                timing = "30-45 minutes after dinner before bed",
                anupana = "Warm water or pure honey",
                caution = "Avoid during acute diarrhea or severe dehydration."
            ),
            indications = listOf("Constipation", "Eye disorders", "Detoxification", "Agnimandya"),
            contraindications = listOf("Dysentery", "Acute dehydration"),
            pathyaWholesome = listOf("Warm water throughout the day", "Steamed vegetables", "Moong dal soup"),
            apathyaAvoid = listOf("Fried heavy snacks", "Cold curd", "Processed flour"),
            stockUnits = 200,
            batchNumber = "SIT-CHO-2026-001"
        ),
        AyurvedaMedicine(
            id = "SA-92629",
            slNo = 6,
            name = "Ashwagandha Root",
            sanskritName = "अश्वगंधा (Withania somnifera)",
            classicalReference = "Charaka Samhita Chikitsa Sthana 1.1 / AFI Vol. I",
            category = FormulationCategory.CHOORNAMS,
            packing = "450 ml",
            tagPill = "ADAPTOGEN",
            healthGoals = listOf(HealthGoal.STRESS_RELIEF, HealthGoal.IMMUNITY),
            shortDescription = "Supports stress reduction and cognitive focus. Classical adaptogenic root promoting vitality and restful sleep.",
            primaryBenefit = "Somatic vitality, adrenal support, and deep restful restorative sleep.",
            doshaImpact = "Vata & Kapha Pacifying (Vata-Kapha Shamaka)",
            targetDoshas = listOf(DoshaType.VATA, DoshaType.KAPHA),
            constituents = listOf("Flavonoids", "Alkaloids", "Withanolides", "Sitoindosides"),
            ingredients = listOf(
                AyurvedaIngredient(name = "Ashwagandha Root", botanicalName = "Withania somnifera", partUsed = "Sun-dried Root", classicalRole = "Balya, Rasayana, Medhya"),
                AyurvedaIngredient(name = "Black Pepper (Bioenhancer)", botanicalName = "Piper nigrum", partUsed = "Dried Fruit", classicalRole = "Deepana & Sroto-shodhana")
            ),
            dravyaguna = DravyagunaProfile(
                rasa = listOf("Tikta (Bitter)", "Kashaya (Astringent)", "Madhura (Sweet)"),
                virya = "Ushna (Heating / Energizing)",
                vipaka = "Madhura (Nourishing post-digestive)",
                guna = listOf("Laghu (Light)", "Snigdha (Unctuous)")
            ),
            dosage = DosageInfo(
                summary = "3-6g with warm milk • After Breakfast",
                standardDose = "3-6g with warm milk",
                frequency = "Twice Daily",
                timing = "Morning after meal & 30 mins before sleep",
                anupana = "Warm cow milk or ghee",
                caution = "Use with care in high Pitta conditions with burning sensations."
            ),
            indications = listOf("Somatic vitality", "Adrenal support", "Restorative sleep", "Stress relief"),
            contraindications = listOf("Acute high fever", "Severe thyrotoxicosis without supervision"),
            pathyaWholesome = listOf("Warm whole milk", "Soaked almonds", "Ghee", "Warm stewed apples"),
            apathyaAvoid = listOf("Excessive caffeine", "Cold raw dry salads", "Late night eating"),
            stockUnits = 180,
            batchNumber = "SIT-CHO-2026-002"
        ),
        AyurvedaMedicine(
            id = "SA-20904",
            slNo = 7,
            name = "Brahmi Vati",
            sanskritName = "ब्राह्मी वटी (Bhaishajya Ratnavali)",
            classicalReference = "Bhaishajya Ratnavali Manasaroga Chikitsa / AFI",
            category = FormulationCategory.GULIKA_TABLETS_CAPSULES,
            packing = "450 ml",
            tagPill = "NOOTROPIC",
            healthGoals = listOf(HealthGoal.COGNITION, HealthGoal.STRESS_RELIEF),
            shortDescription = "Classical neuro-protective tablet enhancing memory retention, focus, and reducing mental exhaustion.",
            primaryBenefit = "Deep mental clarity, cognitive retention, and nervous system tranquility.",
            doshaImpact = "Pacifies Sadhaka Pitta & Prana Vata",
            targetDoshas = listOf(DoshaType.VATA, DoshaType.PITTA),
            constituents = listOf("Bacosides A & B", "Alkaloids", "Sterols"),
            ingredients = listOf(
                AyurvedaIngredient(name = "Brahmi Herb", botanicalName = "Bacopa monnieri", partUsed = "Whole Aerial Plant", classicalRole = "Medhya Rasayana"),
                AyurvedaIngredient(name = "Shankhpushpi", botanicalName = "Convolvulus pluricaulis", partUsed = "Whole Herb", classicalRole = "Calms nervous excitement"),
                AyurvedaIngredient(name = "Vacha (Sweet Flag)", botanicalName = "Acorus calamus", partUsed = "Purified Rhizome", classicalRole = "Speech clarity, cognitive sharpness")
            ),
            dravyaguna = DravyagunaProfile(
                rasa = listOf("Tikta (Bitter)", "Kashaya (Astringent)"),
                virya = "Sheeta (Cooling)",
                vipaka = "Madhura",
                guna = listOf("Laghu (Light)", "Sara (Promotes gentle movement)")
            ),
            dosage = DosageInfo(
                summary = "1-2 tablets twice daily • Anupana: Saraswatarishta or warm milk",
                standardDose = "1-2 tablets twice daily",
                frequency = "Twice daily",
                timing = "After breakfast and lunch",
                anupana = "Saraswatarishta or warm milk",
                caution = "Take after food if prone to mild gastric sensitivity."
            ),
            indications = listOf("Examination stress", "Poor memory recall", "Mental fatigue", "Restless thoughts"),
            contraindications = listOf("Hypersensitivity to herbal bitters"),
            pathyaWholesome = listOf("A2 Cow Ghee", "Walnuts", "Pomegranate", "Fresh coconut water"),
            apathyaAvoid = listOf("Excessive green chilies", "Fermented alcohol", "Excessive screen time"),
            stockUnits = 140,
            batchNumber = "SIT-GUL-2026-001"
        ),
        AyurvedaMedicine(
            id = "SA-76907",
            slNo = 8,
            name = "Chyawanprash Avaleha",
            sanskritName = "च्यवनप्राश अवलेह (Charaka Samhita)",
            classicalReference = "Charaka Samhita Chikitsa Sthana 1.1.62-74",
            category = FormulationCategory.LEHYAMS,
            packing = "450 ml",
            tagPill = "IMMUNITY BOOSTER",
            healthGoals = listOf(HealthGoal.IMMUNITY),
            shortDescription = "Classical multi-herb botanical jam prepared in fresh amla, ghee, and honey for deep immune defense.",
            primaryBenefit = "Immuno-modulatory rejuvenation, lung health, and vitality for all seasons.",
            doshaImpact = "Tridoshic Balancer (Nourishes all 7 Dhatus)",
            targetDoshas = listOf(DoshaType.TRIDOSHIC, DoshaType.VATA, DoshaType.KAPHA),
            constituents = listOf("Bioflavonoids", "Ascorbic Acid", "Essential Fatty Acids", "Polyphenols"),
            ingredients = listOf(
                AyurvedaIngredient(name = "Fresh Amla (Gooseberry)", botanicalName = "Phyllanthus emblica", partUsed = "Pulp of Fresh Wild Berries", classicalRole = "Dominant ingredient (60%+); supreme Rasayana"),
                AyurvedaIngredient(name = "Dashamula Complex", botanicalName = "Ten Sacred Roots", partUsed = "Decoction of 10 Roots", classicalRole = "Strengthens respiratory system"),
                AyurvedaIngredient(name = "Pippali (Long Pepper)", botanicalName = "Piper longum", partUsed = "Dried Spikes", classicalRole = "Pranavaha Srotas rejuvenator")
            ),
            dravyaguna = DravyagunaProfile(
                rasa = listOf("Madhura (Sweet)", "Amla (Sour)", "Tikta (Bitter)", "Katu (Pungent)"),
                virya = "Sheeta-Ushna Samashitoshna (Balanced)",
                vipaka = "Madhura",
                guna = listOf("Guru (Nourishing / Heavy)", "Snigdha (Unctuous)")
            ),
            dosage = DosageInfo(
                summary = "1-2 tablespoons morning & evening • Anupana: Warm milk",
                standardDose = "1-2 tablespoons morning & evening",
                frequency = "Twice daily",
                timing = "Morning on empty stomach and bedtime",
                anupana = "Warm milk",
                caution = "Diabetic individuals should consult practitioner due to traditional honey matrix."
            ),
            indications = listOf("Immuno-modulatory rejuvenation", "Lung health", "Vitality for all seasons", "General debility"),
            contraindications = listOf("Acute uncontrolled hyperglycemia"),
            pathyaWholesome = listOf("Warm nourishing grains", "Warm milk with turmeric", "Dates"),
            apathyaAvoid = listOf("Refrigerated drinks", "Ice creams", "Stale leftover food"),
            stockUnits = 160,
            batchNumber = "SIT-LEH-2026-001"
        ),
        AyurvedaMedicine(
            id = "SA-25363",
            slNo = 9,
            name = "Kumkumadi Thailam",
            sanskritName = "कुंकुमादि तैलम् (Ashtanga Hrudayam)",
            classicalReference = "Ashtanga Hrudayam / Bhaishajya Ratnavali",
            category = FormulationCategory.THAILAMS,
            packing = "450 ml",
            tagPill = "SKIN RADIANCE",
            healthGoals = listOf(HealthGoal.SKIN_HEALTH),
            shortDescription = "Miraculous Ayurvedic facial elixir infused with pure Kashmiri Saffron for radiant skin luster.",
            primaryBenefit = "Facial radiance, hyperpigmentation correction, and epidermal glow.",
            doshaImpact = "Pacifies Pitta and Rakta (Blood tissue)",
            targetDoshas = listOf(DoshaType.PITTA),
            constituents = listOf("Crocin", "Safranal", "Glycyrrhizin", "Santalols"),
            ingredients = listOf(
                AyurvedaIngredient(name = "Kashmiri Saffron", botanicalName = "Crocus sativus", partUsed = "Crimson Stigmas", classicalRole = "Varnya (Complexion enhancer)"),
                AyurvedaIngredient(name = "Rakta Chandana", botanicalName = "Pterocarpus santalinus", partUsed = "Heartwood", classicalRole = "Deeply cooling, eliminates sun pigmentation"),
                AyurvedaIngredient(name = "Manjistha", botanicalName = "Rubia cordifolia", partUsed = "Stems & Roots", classicalRole = "Premier lymph and micro-capillary purifier")
            ),
            dravyaguna = DravyagunaProfile(
                rasa = listOf("Madhura", "Tikta"),
                virya = "Anushna (Mild soothing warmth)",
                vipaka = "Madhura",
                guna = listOf("Snigdha (Hydrating)", "Sukshma (Deeply penetrative)")
            ),
            dosage = DosageInfo(
                summary = "3-5 drops external application • Anupana: Gentle circular facial massage",
                standardDose = "3-5 drops external application",
                frequency = "Once daily before sleep",
                timing = "Night after washing face",
                anupana = "Gentle circular facial massage",
                caution = "For external facial use only."
            ),
            indications = listOf("Facial radiance", "Hyperpigmentation correction", "Epidermal glow", "Blemish scars"),
            contraindications = listOf("Active cystic pus-filled acne flare-up"),
            pathyaWholesome = listOf("Amla juice", "Watermelon", "Cilantro tea", "Hydrating water"),
            apathyaAvoid = listOf("Excessive direct sun", "Deep-fried salty snacks"),
            stockUnits = 90,
            batchNumber = "SIT-THI-2026-001"
        ),
        AyurvedaMedicine(
            id = "SA-43264",
            slNo = 10,
            name = "Yogaraj Guggulu",
            sanskritName = "योगराज गुग्गुलु (Bhaishajya Ratnavali)",
            classicalReference = "Bhaishajya Ratnavali Amavata Chikitsa",
            category = FormulationCategory.GULIKA_TABLETS_CAPSULES,
            packing = "450 ml",
            tagPill = "JOINT MOBILITY",
            healthGoals = listOf(HealthGoal.JOINT_MOBILITY, HealthGoal.DETOX),
            shortDescription = "Time-honored detoxifying and anti-inflammatory classical Guggulu compound for joint mobility.",
            primaryBenefit = "Joint flexibility, synovial comfort, and elimination of metabolic ama.",
            doshaImpact = "Deep Vata-Kapha Shamaka and Ama-pachana",
            targetDoshas = listOf(DoshaType.VATA, DoshaType.KAPHA),
            constituents = listOf("Guggulsterones Z & E", "Essential Resins", "Piperine"),
            ingredients = listOf(
                AyurvedaIngredient(name = "Shuddha Guggulu", botanicalName = "Commiphora mukul", partUsed = "Purified Oleo-gum-resin", classicalRole = "Anti-inflammatory, scrapes Ama"),
                AyurvedaIngredient(name = "Chitraka", botanicalName = "Plumbago zeylanica", partUsed = "Root Bark", classicalRole = "Agni deepana, digests metabolic waste"),
                AyurvedaIngredient(name = "Rasna", botanicalName = "Pluchea lanceolata", partUsed = "Leaves", classicalRole = "Premier Vata-pacifier for joints")
            ),
            dravyaguna = DravyagunaProfile(
                rasa = listOf("Tikta (Bitter)", "Katu (Pungent)", "Kashaya (Astringent)"),
                virya = "Ushna (Heating)",
                vipaka = "Katu",
                guna = listOf("Laghu", "Ruksha", "Sukshma")
            ),
            dosage = DosageInfo(
                summary = "2 tablets twice daily • Anupana: Warm water or Dashamoolarishtam",
                standardDose = "2 tablets twice daily",
                frequency = "Twice daily",
                timing = "After meals",
                anupana = "Warm water or Dashamoolarishtam",
                caution = "Not suitable during active gastritis or pregnancy."
            ),
            indications = listOf("Joint flexibility", "Synovial comfort", "Amavata (Rheumatoid stiffness)", "Musculoskeletal spasms"),
            contraindications = listOf("Pregnancy", "Active peptic ulceration"),
            pathyaWholesome = listOf("Warm cooked barley", "Garlic-infused milk", "Warm water"),
            apathyaAvoid = listOf("Cold refrigerated foods", "Curd at night", "Sedentary daytime sleep"),
            stockUnits = 135,
            batchNumber = "SIT-GUL-2026-002"
        ),
        AyurvedaMedicine(
            id = "SA-72205",
            slNo = 11,
            name = "Shatavari Kalpa",
            sanskritName = "शतावरी कल्प (API)",
            classicalReference = "Ayurvedic Pharmacopoeia of India (API)",
            category = FormulationCategory.LEHYAMS,
            packing = "450 ml",
            tagPill = "FEMALE VITALITY",
            healthGoals = listOf(HealthGoal.IMMUNITY),
            shortDescription = "Delicious Shatavari granules enriched with cardamom for female vitality, maternal health, and Pitta balance.",
            primaryBenefit = "Hormonal balance, maternal vitality, and reproductive tissue nourishment.",
            doshaImpact = "Pitta-Vata Shamaka, Balya and Stanya-janana",
            targetDoshas = listOf(DoshaType.PITTA, DoshaType.VATA),
            constituents = listOf("Shatavarins I-IV", "Saponins", "Sterols"),
            ingredients = listOf(
                AyurvedaIngredient(name = "Shatavari", botanicalName = "Asparagus racemosus", partUsed = "Tuberous Root", classicalRole = "Stanyajanana, Shukra-Artava balya, Rasayana"),
                AyurvedaIngredient(name = "Elaichi (Cardamom)", botanicalName = "Elettaria cardamomum", partUsed = "Seeds", classicalRole = "Aromatic digestive enhancer"),
                AyurvedaIngredient(name = "Khanda Sharkara", botanicalName = "Raw Cane Sugar Matrix", partUsed = "Crystallized Cane", classicalRole = "Pitta soothing vehicle")
            ),
            dravyaguna = DravyagunaProfile(
                rasa = listOf("Madhura (Sweet)", "Tikta (Bitter)"),
                virya = "Sheeta (Cooling)",
                vipaka = "Madhura",
                guna = listOf("Guru (Heavy)", "Snigdha (Nourishing)")
            ),
            dosage = DosageInfo(
                summary = "1-2 teaspoons twice daily • Anupana: Warm milk",
                standardDose = "1-2 teaspoons twice daily",
                frequency = "Twice daily",
                timing = "Morning & Evening with milk",
                anupana = "Warm cow milk",
                caution = "Safe for nursing mothers; consult practitioner if diabetic."
            ),
            indications = listOf("Hormonal balance", "Maternal vitality", "Reproductive tissue nourishment", "Lactation support"),
            contraindications = listOf("None under recommended dosage"),
            pathyaWholesome = listOf("Warm milk", "Dates", "Almonds", "Ghee"),
            apathyaAvoid = listOf("Excessive spicy pungent foods", "Skipping meals"),
            stockUnits = 125,
            batchNumber = "SIT-LEH-2026-002"
        ),
        AyurvedaMedicine(
            id = "SA-58259",
            slNo = 12,
            name = "Mahanarayan Thailam",
            sanskritName = "महानारायण तैलम् (Bhaishajya Ratnavali)",
            classicalReference = "Bhaishajya Ratnavali Vatavyadhi Chikitsa",
            category = FormulationCategory.THAILAMS,
            packing = "450 ml",
            tagPill = "MUSCULOSKELETAL",
            healthGoals = listOf(HealthGoal.JOINT_MOBILITY),
            shortDescription = "Master classical abhyanga oil formulated with 50+ roots and botanicals for deep tissue rejuvenation.",
            primaryBenefit = "Musculoskeletal strength, tendon relaxation, and alleviation of chronic stiffness.",
            doshaImpact = "Profound Vata Pacifier",
            targetDoshas = listOf(DoshaType.VATA),
            constituents = listOf("Flavonoid Glycosides", "Sesamin", "Bioactive Terpenoids"),
            ingredients = listOf(
                AyurvedaIngredient(name = "Bilva, Ashwagandha & Bala", botanicalName = "Aegle, Withania & Sida sp.", partUsed = "Decoction Roots", classicalRole = "Vata-shamaka, Brumhana, Balya"),
                AyurvedaIngredient(name = "Dashamoola Complex", botanicalName = "Ten Sacred Roots", partUsed = "Root Barks", classicalRole = "Deep neuromuscular relief"),
                AyurvedaIngredient(name = "Tila Thailam (Pure Sesame Oil)", botanicalName = "Sesamum indicum", partUsed = "Cold-pressed seeds", classicalRole = "Transdermal absorption carrier")
            ),
            dravyaguna = DravyagunaProfile(
                rasa = listOf("Madhura", "Tikta"),
                virya = "Ushna (Warm)",
                vipaka = "Madhura",
                guna = listOf("Snigdha", "Guru", "Sukshma")
            ),
            dosage = DosageInfo(
                summary = "External Abhyanga oil massage • Anupana: Followed by hot steam fomentation (Swedana)",
                standardDose = "External Abhyanga oil massage",
                frequency = "Daily or as needed",
                timing = "Before bath or morning Abhyanga routine",
                anupana = "Followed by hot steam fomentation (Swedana)",
                caution = "For external massage use only."
            ),
            indications = listOf("Musculoskeletal strength", "Tendon relaxation", "Alleviation of chronic stiffness", "Vata Vyadhi"),
            contraindications = listOf("Open wounds", "Acute inflammatory swelling (Taruna Jwara)"),
            pathyaWholesome = listOf("Warm bath after massage", "Warm nourishing meals", "Gentle stretching"),
            apathyaAvoid = listOf("Cold showers immediately after oil application", "Cold air currents"),
            stockUnits = 145,
            batchNumber = "SIT-THI-2026-002"
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
