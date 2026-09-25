package com.example.data.repository

import android.util.Log
import com.example.data.model.AyurvedaIngredient
import com.example.data.model.AyurvedaMedicine
import com.example.data.model.DosageInfo
import com.example.data.model.DoshaType
import com.example.data.model.DravyagunaProfile
import com.example.data.model.FormulationCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

/**
 * Supabase Central Cloud Database Repository for Android.
 * Communicates directly with the cloud PostgreSQL PostgREST endpoint.
 */
object SupabaseRepository {

    private const val TAG = "SupabaseRepo"
    const val SUPABASE_URL = "https://ksnsfilauqzxsegpjpdt.supabase.co"
    const val SUPABASE_KEY = "sb_secret_YB3vSW9nBcJd-9CXXMkjww_eCDvaprk"

    val isCloudConfigured: Boolean
        get() = SUPABASE_URL.isNotBlank() && SUPABASE_KEY.isNotBlank()

    /**
     * Fetch formulations from Supabase products table.
     */
    suspend fun fetchMedicinesSuspend(): List<AyurvedaMedicine> = withContext(Dispatchers.IO) {
        try {
            val endpoint = "$SUPABASE_URL/rest/v1/products?select=*&order=id.asc"
            val url = URL(endpoint)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("apikey", SUPABASE_KEY)
                setRequestProperty("Authorization", "Bearer $SUPABASE_KEY")
                setRequestProperty("Accept", "application/json")
                connectTimeout = 15000
                readTimeout = 15000
            }

            val responseCode = conn.responseCode
            if (responseCode in 200..299) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val body = reader.readText()
                reader.close()

                val jsonArray = JSONArray(body)
                val medicines = mutableListOf<AyurvedaMedicine>()

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val id = obj.optString("code").ifBlank { obj.optString("id", "MED-$i") }
                    val name = obj.optString("name", "Ayurvedic Formulation")
                    val catName = obj.optString("category_name", "Churna")
                    val classicalRef = obj.optString("classical_reference", "Classical Text")
                    val dosageText = obj.optString("dosage", "As directed by physician")
                    val indications = obj.optString("indications", "")
                    val description = obj.optString("description", "")
                    val photoUrl = obj.optString("image_url", "")
                    val stock = obj.optInt("stock", 25)

                    // Parse packings
                    val packingsList = mutableListOf<String>()
                    val packingsJson = obj.opt("packings")
                    if (packingsJson is JSONArray) {
                        for (p in 0 until packingsJson.length()) {
                            packingsList.add(packingsJson.optString(p))
                        }
                    }

                    // Parse ingredients
                    val ingredientsList = mutableListOf<AyurvedaIngredient>()
                    val ingJson = obj.opt("ingredients")
                    if (ingJson is JSONArray) {
                        for (j in 0 until ingJson.length()) {
                            val ingItem = ingJson.optString(j)
                            ingredientsList.add(AyurvedaIngredient(name = ingItem))
                        }
                    }

                    val categoryEnum = mapCategory(catName)
                    val sanskritName = obj.optString("sanskrit_name").ifBlank { obj.optString("sanskritName").ifBlank { name } }
                    val resolvedPhoto = if (photoUrl.isNotBlank() && (photoUrl.startsWith("http://") || photoUrl.startsWith("https://"))) {
                        photoUrl
                    } else {
                        com.example.ui.components.ClassicalPhotoPresets.getPresetForCategory(categoryEnum)
                    }

                    val goals = mutableListOf<com.example.data.model.HealthGoal>()
                    val goalsJson = obj.opt("health_goals") ?: obj.opt("healthGoals")
                    if (goalsJson is JSONArray) {
                        for (g in 0 until goalsJson.length()) {
                            try {
                                val gStr = goalsJson.optString(g).uppercase()
                                goals.add(com.example.data.model.HealthGoal.valueOf(gStr))
                            } catch (_: Exception) {}
                        }
                    }

                    medicines.add(
                        AyurvedaMedicine(
                            id = id,
                            name = name,
                            sanskritName = sanskritName,
                            category = categoryEnum,
                            ingredients = ingredientsList,
                            dosageInstructions = dosageText,
                            healthGoals = goals,
                            shortDescription = description.ifBlank { "$name is a classical Ayurvedic formulation ($catName)." },
                            primaryBenefit = indications.ifBlank { "Promotes holistic balance & vitality" },
                            indications = indications.split(",").map { it.trim() }.filter { it.isNotBlank() },
                            classicalReference = classicalRef,
                            packing = packingsList.joinToString(", ").ifBlank { "Standard Unit" },
                            photoUrl = resolvedPhoto,
                            stockUnits = stock
                        )
                    )
                }

                Log.d(TAG, "Successfully fetched ${medicines.size} formulations from Supabase Cloud.")
                medicines
            } else {
                Log.w(TAG, "Supabase fetch returned HTTP $responseCode")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error connecting to Supabase: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Persists or updates a formulation directly in Supabase Cloud.
     */
    suspend fun saveMedicineSuspend(medicine: AyurvedaMedicine): Boolean = withContext(Dispatchers.IO) {
        try {
            val endpoint = "$SUPABASE_URL/rest/v1/products"
            val url = URL(endpoint)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                setRequestProperty("apikey", SUPABASE_KEY)
                setRequestProperty("Authorization", "Bearer $SUPABASE_KEY")
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Prefer", "resolution=merge-duplicates,return=representation")
                doOutput = true
                connectTimeout = 15000
                readTimeout = 15000
            }

            val payload = JSONObject().apply {
                put("code", medicine.id)
                put("name", medicine.name)
                put("category_name", medicine.category.displayName)
                put("classical_reference", medicine.classicalReference)
                put("dosage", medicine.dosageInstructions)
                put("indications", medicine.indications.joinToString(", "))
                put("description", medicine.shortDescription)
                put("image_url", medicine.photoUrl)
                put("stock", medicine.stockUnits)

                val packArray = JSONArray()
                if (medicine.packing.isNotBlank()) packArray.put(medicine.packing)
                put("packings", packArray)

                val ingArray = JSONArray()
                medicine.ingredients.forEach { ingArray.put(it.name) }
                put("ingredients", ingArray)
            }

            val writer = OutputStreamWriter(conn.outputStream)
            writer.write(payload.toString())
            writer.flush()
            writer.close()

            val code = conn.responseCode
            val success = code in 200..299
            Log.d(TAG, "Saved medicine to Supabase ($code): $success")
            success
        } catch (e: Exception) {
            Log.e(TAG, "Error saving to Supabase: ${e.message}", e)
            false
        }
    }

    /**
     * Deletes a formulation from Supabase Cloud.
     */
    suspend fun deleteMedicineSuspend(medicineId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val endpoint = "$SUPABASE_URL/rest/v1/products?or=(code.eq.$medicineId,id.eq.$medicineId)"
            val url = URL(endpoint)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "DELETE"
                setRequestProperty("apikey", SUPABASE_KEY)
                setRequestProperty("Authorization", "Bearer $SUPABASE_KEY")
                connectTimeout = 15000
                readTimeout = 15000
            }

            val code = conn.responseCode
            val success = code in 200..299
            Log.d(TAG, "Deleted medicine from Supabase ($code): $success")
            success
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting from Supabase: ${e.message}", e)
            false
        }
    }

    /**
     * Fetch all categories directly from Supabase Cloud categories table.
     */
    suspend fun fetchCategoriesSuspend(): List<String> = withContext(Dispatchers.IO) {
        try {
            val endpoint = "$SUPABASE_URL/rest/v1/categories?select=name&order=id.asc"
            val url = URL(endpoint)
            val conn = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                setRequestProperty("apikey", SUPABASE_KEY)
                setRequestProperty("Authorization", "Bearer $SUPABASE_KEY")
                setRequestProperty("Accept", "application/json")
                connectTimeout = 15000
                readTimeout = 15000
            }

            val responseCode = conn.responseCode
            if (responseCode in 200..299) {
                val reader = BufferedReader(InputStreamReader(conn.inputStream))
                val body = reader.readText()
                reader.close()

                val jsonArray = JSONArray(body)
                val list = mutableListOf<String>()
                for (i in 0 until jsonArray.length()) {
                    val name = jsonArray.getJSONObject(i).optString("name")
                    if (name.isNotBlank()) list.add(name)
                }
                Log.d(TAG, "Successfully fetched ${list.size} categories from Supabase Cloud.")
                list
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching categories from Supabase: ${e.message}", e)
            emptyList()
        }
    }

    private fun mapCategory(cat: String): FormulationCategory {
        return FormulationCategory.fromString(cat)
    }
}
