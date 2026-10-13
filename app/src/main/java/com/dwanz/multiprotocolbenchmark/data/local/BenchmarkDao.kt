package com.dwanz.multiprotocolbenchmark.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BenchmarkDao {
    @Query("SELECT * FROM benchmark_metrics WHERE technology = :tech ORDER BY timestamp DESC")
    fun getMetricsByTechnology(tech: String): Flow<List<BenchmarkMetricEntity>>

    @Query("SELECT * FROM benchmark_metrics ORDER BY timestamp DESC")
    fun getAllMetrics(): Flow<List<BenchmarkMetricEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetric(metric: BenchmarkMetricEntity)
}
