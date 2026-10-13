package com.dwanz.multiprotocolbenchmark.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.dwanz.multiprotocolbenchmark.domain.model.ProtocolTechnology
import com.dwanz.multiprotocolbenchmark.domain.usecase.ObserveMetricsUseCase
import com.dwanz.multiprotocolbenchmark.domain.usecase.RunBenchmarkUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class BenchmarkViewModel(
    private val runBenchmarkUseCase: RunBenchmarkUseCase,
    private val observeMetricsUseCase: ObserveMetricsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BenchmarkUiState())
    val uiState: StateFlow<BenchmarkUiState> = _uiState.asStateFlow()

    private val filterFlow = MutableStateFlow<ProtocolTechnology?>(null)

    init {
        viewModelScope.launch {
            filterFlow
                .flatMapLatest { filter -> observeMetricsUseCase(filter) }
                .collect { metrics ->
                    _uiState.update { it.copy(history = metrics) }
                }
        }
    }

    fun run(technology: ProtocolTechnology) {
        if (_uiState.value.isRunning) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isRunning = true,
                    runningTechnology = technology,
                    errorMessage = null
                )
            }
            try {
                val result = runBenchmarkUseCase(technology)
                _uiState.update {
                    it.copy(
                        isRunning = false,
                        runningTechnology = null,
                        latestMetric = result.metric,
                        latestCharacter = result.character,
                        errorMessage = null
                    )
                }
            } catch (t: Throwable) {
                _uiState.update {
                    it.copy(
                        isRunning = false,
                        runningTechnology = null,
                        errorMessage = t.message ?: "Benchmark failed"
                    )
                }
            }
        }
    }

    fun setHistoryFilter(filter: ProtocolTechnology?) {
        filterFlow.value = filter
        _uiState.update { it.copy(historyFilter = filter) }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    class Factory(
        private val runBenchmarkUseCase: RunBenchmarkUseCase,
        private val observeMetricsUseCase: ObserveMetricsUseCase
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BenchmarkViewModel::class.java)) {
                return BenchmarkViewModel(runBenchmarkUseCase, observeMetricsUseCase) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
