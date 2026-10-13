package com.dwanz.multiprotocolbenchmark.domain.usecase

import com.dwanz.multiprotocolbenchmark.domain.model.ProtocolTechnology
import com.dwanz.multiprotocolbenchmark.domain.repository.BenchmarkRepository
import com.dwanz.multiprotocolbenchmark.domain.repository.BenchmarkRunResult

class RunBenchmarkUseCase(
    private val repository: BenchmarkRepository
) {
    suspend operator fun invoke(technology: ProtocolTechnology): BenchmarkRunResult =
        repository.runBenchmark(technology)
}
