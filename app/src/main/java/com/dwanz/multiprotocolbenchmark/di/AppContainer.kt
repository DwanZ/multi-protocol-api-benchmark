package com.dwanz.multiprotocolbenchmark.di

import android.content.Context
import com.dwanz.multiprotocolbenchmark.data.local.BenchmarkDatabase
import com.dwanz.multiprotocolbenchmark.data.metrics.BenchmarkExecutor
import com.dwanz.multiprotocolbenchmark.data.repository.BenchmarkRepositoryImpl
import com.dwanz.multiprotocolbenchmark.domain.repository.BenchmarkRepository
import com.dwanz.multiprotocolbenchmark.domain.usecase.ObserveMetricsUseCase
import com.dwanz.multiprotocolbenchmark.domain.usecase.RunBenchmarkUseCase

class AppContainer(context: Context) {
    private val database = BenchmarkDatabase.getInstance(context)
    private val dao = database.benchmarkDao()
    private val executor = BenchmarkExecutor(dao)

    val repository: BenchmarkRepository = BenchmarkRepositoryImpl(dao, executor)
    val runBenchmarkUseCase = RunBenchmarkUseCase(repository)
    val observeMetricsUseCase = ObserveMetricsUseCase(repository)
}
