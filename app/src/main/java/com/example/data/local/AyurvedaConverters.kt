package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.AyurvedaIngredient
import com.example.data.model.DoshaType
import org.json.JSONArray
import org.json.JSONObject

class AyurvedaConverters {

    @TypeConverter
    fun fromStringList(list: List<String>?): String {
        if (list == null) return "[]"
        val jsonArray = JSONArray()
        list.forEach { jsonArray.put(it) }
        return jsonArray.toString()
    }

    @TypeConverter
    fun toStringList(jsonString: String?): List<String> {
        if (jsonString.isNullOrBlank()) return emptyList()
        return try {
            val jsonArray = JSONArray(jsonString)
            val result = mutableListOf<String>()
            for (i in 0 until jsonArray.length()) {
                result.add(jsonArray.getString(i))
            }
            result
        } catch (_: Exception) {
            emptyList()
        }
    }

    @TypeConverter
    fun fromDoshaList(list: List<DoshaType>?): String {
        if (list == null) return "[]"
        val jsonArray = JSONArray()
        list.forEach { jsonArray.put(it.name) }
        return jsonArray.toString()
    }

    @TypeConverter
    fun toDoshaList(jsonString: String?): List<DoshaType> {
        if (jsonString.isNullOrBlank()) return listOf(DoshaType.TRIDOSHIC)
        return try {
            val jsonArray = JSONArray(jsonString)
            val result = mutableListOf<DoshaType>()
            for (i in 0 until jsonArray.length()) {
                val name = jsonArray.getString(i)
                runCatching { DoshaType.valueOf(name) }.getOrNull()?.let { result.add(it) }
            }
            if (result.isEmpty()) listOf(DoshaType.TRIDOSHIC) else result
        } catch (_: Exception) {
            listOf(DoshaType.TRIDOSHIC)
        }
    }

    @TypeConverter
    fun fromIngredientList(list: List<AyurvedaIngredient>?): String {
        if (list == null) return "[]"
        val jsonArray = JSONArray()
        list.forEach { item ->
            val obj = JSONObject().apply {
                put("name", item.name)
                put("sanskritName", item.sanskritName)
                put("botanicalName", item.botanicalName)
                put("partUsed", item.partUsed)
                put("classicalRole", item.classicalRole)
            }
            jsonArray.put(obj)
        }
        return jsonArray.toString()
    }

    @TypeConverter
    fun toIngredientList(jsonString: String?): List<AyurvedaIngredient> {
        if (jsonString.isNullOrBlank()) return emptyList()
        return try {
            val jsonArray = JSONArray(jsonString)
            val result = mutableListOf<AyurvedaIngredient>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                result.add(
                    AyurvedaIngredient(
                        name = obj.optString("name"),
                        sanskritName = obj.optString("sanskritName"),
                        botanicalName = obj.optString("botanicalName"),
                        partUsed = obj.optString("partUsed"),
                        classicalRole = obj.optString("classicalRole")
                    )
                )
            }
            result
        } catch (_: Exception) {
            emptyList()
        }
    }
}
