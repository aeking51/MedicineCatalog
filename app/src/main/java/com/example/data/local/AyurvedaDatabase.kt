package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.local.dao.AyurvedaMedicineDao
import com.example.data.local.entity.AyurvedaMedicineEntity

@Database(
    entities = [AyurvedaMedicineEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(AyurvedaConverters::class)
abstract class AyurvedaDatabase : RoomDatabase() {

    abstract fun medicineDao(): AyurvedaMedicineDao

    companion object {
        @Volatile
        private var INSTANCE: AyurvedaDatabase? = null

        fun getDatabase(context: Context): AyurvedaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AyurvedaDatabase::class.java,
                    "ayurveda_sanctuary.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
