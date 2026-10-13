package com.dwanz.multiprotocolbenchmark.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dwanz.multiprotocolbenchmark.BenchmarkApp
import com.dwanz.multiprotocolbenchmark.presentation.theme.BenchmarkTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as BenchmarkApp).container

        setContent {
            BenchmarkTheme {
                val viewModel: BenchmarkViewModel = viewModel(
                    factory = BenchmarkViewModel.Factory(
                        runBenchmarkUseCase = container.runBenchmarkUseCase,
                        observeMetricsUseCase = container.observeMetricsUseCase
                    )
                )
                BenchmarkDashboardScreen(viewModel = viewModel)
            }
        }
    }
}
