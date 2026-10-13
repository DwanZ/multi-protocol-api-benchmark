package com.dwanz.multiprotocolbenchmark.domain.usecase

import com.dwanz.multiprotocolbenchmark.domain.model.BenchmarkMetric
import com.dwanz.multiprotocolbenchmark.domain.model.ProtocolTechnology
import com.dwanz.multiprotocolbenchmark.domain.repository.BenchmarkRepository
import kotlinx.coroutines.flow.Flow

class ObserveMetricsUseCase(
    private val repository: BenchmarkRepository
) {
    operator fun invoke(filter: ProtocolTechnology?): Flow<List<BenchmarkMetric>> =
        if (filter == null) {
            repository.observeAllMetrics()
        } else {
            repository.observeMetricsByTechnology(filter)
        }
}
