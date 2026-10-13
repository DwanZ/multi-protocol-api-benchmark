package com.dwanz.multiprotocolbenchmark.domain.model

data class BenchmarkMetric(
    val id: Long,
    val technology: ProtocolTechnology,
    val timestamp: Long,
    val latencyMs: Double,
    val payloadSizeBytes: Long,
    val memoryUsageBytes: Long,
    val cpuUsagePercent: Float,
    val endpointTag: String
)
