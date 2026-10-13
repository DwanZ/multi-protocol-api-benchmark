package com.dwanz.multiprotocolbenchmark.domain.repository

import com.dwanz.multiprotocolbenchmark.domain.model.BenchmarkMetric
import com.dwanz.multiprotocolbenchmark.domain.model.CharacterSnapshot
import com.dwanz.multiprotocolbenchmark.domain.model.ProtocolTechnology
import kotlinx.coroutines.flow.Flow

data class BenchmarkRunResult(
    val character: CharacterSnapshot,
    val metric: BenchmarkMetric
)

interface BenchmarkRepository {
    fun observeAllMetrics(): Flow<List<BenchmarkMetric>>
    fun observeMetricsByTechnology(technology: ProtocolTechnology): Flow<List<BenchmarkMetric>>
    suspend fun runBenchmark(technology: ProtocolTechnology): BenchmarkRunResult
}
