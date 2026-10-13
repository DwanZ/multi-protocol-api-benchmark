package com.dwanz.multiprotocolbenchmark.data.metrics

import android.os.Debug
import com.dwanz.multiprotocolbenchmark.data.local.BenchmarkDao
import com.dwanz.multiprotocolbenchmark.data.local.BenchmarkMetricEntity

data class BenchmarkResult<T>(
    val data: T,
    val metric: BenchmarkMetricEntity
)

class BenchmarkExecutor(
    private val benchmarkDao: BenchmarkDao
) {
    suspend fun <T> measure(
        technology: String,
        endpointTag: String,
        block: suspend () -> Pair<T, Long>
    ): BenchmarkResult<T> {
        val runtime = Runtime.getRuntime()
        val startMemory = runtime.totalMemory() - runtime.freeMemory()
        val startCpuNs = Debug.threadCpuTimeNanos()
        val startTime = System.nanoTime()

        val (response, payloadSize) = block()

        val endTime = System.nanoTime()
        val endCpuNs = Debug.threadCpuTimeNanos()
        val endMemory = runtime.totalMemory() - runtime.freeMemory()

        val latencyMs = (endTime - startTime) / 1_000_000.0
        val memoryDelta = maxOf(0L, endMemory - startMemory)
        val wallNs = (endTime - startTime).coerceAtLeast(1L)
        val cpuNs = (endCpuNs - startCpuNs).coerceAtLeast(0L)
        val cpuUsagePercent = ((cpuNs.toDouble() / wallNs.toDouble()) * 100.0)
            .toFloat()
            .coerceIn(0f, 100f)

        val entity = BenchmarkMetricEntity(
            technology = technology,
            timestamp = System.currentTimeMillis(),
            latencyMs = latencyMs,
            payloadSizeBytes = payloadSize,
            memoryUsageBytes = memoryDelta,
            cpuUsagePercent = cpuUsagePercent,
            endpointTag = endpointTag
        )
        benchmarkDao.insertMetric(entity)
        return BenchmarkResult(response, entity)
    }
}
