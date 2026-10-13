package com.dwanz.multiprotocolbenchmark.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "benchmark_metrics")
data class BenchmarkMetricEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val technology: String,
    val timestamp: Long,
    val latencyMs: Double,
    val payloadSizeBytes: Long,
    val memoryUsageBytes: Long,
    val cpuUsagePercent: Float,
    val endpointTag: String
)
