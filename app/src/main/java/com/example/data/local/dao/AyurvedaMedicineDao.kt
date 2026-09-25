package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.AyurvedaMedicineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AyurvedaMedicineDao {

    @Query("SELECT * FROM ayurveda_medicines ORDER BY name ASC")
    fun getAllMedicines(): Flow<List<AyurvedaMedicineEntity>>

    @Query("SELECT * FROM ayurveda_medicines WHERE id = :id LIMIT 1")
    fun getMedicineById(id: String): Flow<AyurvedaMedicineEntity?>

    @Query("SELECT * FROM ayurveda_medicines WHERE name LIKE '%' || :query || '%' OR sanskritName LIKE '%' || :query || '%' OR primaryBenefit LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchMedicines(query: String): Flow<List<AyurvedaMedicineEntity>>

    @Query("SELECT * FROM ayurveda_medicines WHERE name LIKE '%' || :query || '%' OR sanskritName LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchMedicinesByName(query: String): Flow<List<AyurvedaMedicineEntity>>

    @Query("SELECT * FROM ayurveda_medicines WHERE ingredients LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchMedicinesByIngredient(query: String): Flow<List<AyurvedaMedicineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicine(medicine: AyurvedaMedicineEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicines(medicines: List<AyurvedaMedicineEntity>)

    @Update
    suspend fun updateMedicine(medicine: AyurvedaMedicineEntity)

    @Query("DELETE FROM ayurveda_medicines WHERE id = :id")
    suspend fun deleteMedicineById(id: String)

    @Query("DELETE FROM ayurveda_medicines")
    suspend fun deleteAllMedicines()

    @Query("UPDATE ayurveda_medicines SET stockUnits = :newStock, isLowStock = :isLowStock WHERE id = :id")
    suspend fun updateStock(id: String, newStock: Int, isLowStock: Boolean)
}
