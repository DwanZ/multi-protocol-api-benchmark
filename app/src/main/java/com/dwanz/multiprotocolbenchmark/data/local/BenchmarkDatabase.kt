package com.dwanz.multiprotocolbenchmark.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [BenchmarkMetricEntity::class],
    version = 1,
    exportSchema = false
)
abstract class BenchmarkDatabase : RoomDatabase() {
    abstract fun benchmarkDao(): BenchmarkDao

    companion object {
        @Volatile
        private var instance: BenchmarkDatabase? = null

        fun getInstance(context: Context): BenchmarkDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    BenchmarkDatabase::class.java,
                    "benchmark.db"
                ).build().also { instance = it }
            }
        }
    }
}
