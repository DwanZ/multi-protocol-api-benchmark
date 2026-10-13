package com.dwanz.multiprotocolbenchmark.presentation

import com.dwanz.multiprotocolbenchmark.domain.model.BenchmarkMetric
import com.dwanz.multiprotocolbenchmark.domain.model.CharacterSnapshot
import com.dwanz.multiprotocolbenchmark.domain.model.ProtocolTechnology

data class BenchmarkUiState(
    val isRunning: Boolean = false,
    val runningTechnology: ProtocolTechnology? = null,
    val latestMetric: BenchmarkMetric? = null,
    val latestCharacter: CharacterSnapshot? = null,
    val history: List<BenchmarkMetric> = emptyList(),
    val historyFilter: ProtocolTechnology? = null,
    val errorMessage: String? = null
)
