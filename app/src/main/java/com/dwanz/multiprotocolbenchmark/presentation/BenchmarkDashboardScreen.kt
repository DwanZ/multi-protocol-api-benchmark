package com.dwanz.multiprotocolbenchmark.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dwanz.multiprotocolbenchmark.domain.model.BenchmarkMetric
import com.dwanz.multiprotocolbenchmark.domain.model.CharacterSnapshot
import com.dwanz.multiprotocolbenchmark.domain.model.ProtocolTechnology
import java.text.DateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun BenchmarkDashboardScreen(viewModel: BenchmarkViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(state.errorMessage) {
        val message = state.errorMessage ?: return@LaunchedEffect
        snackbarHostState.showSnackbar(message)
        viewModel.clearError()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Multi-Protocol Benchmark") })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Compare GetCharacter (id=1) across REST, GraphQL, and local gRPC.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            item {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ProtocolTechnology.entries.forEach { technology ->
                        Button(
                            onClick = { viewModel.run(technology) },
                            enabled = !state.isRunning
                        ) {
                            Text("Run ${technology.label}")
                        }
                    }
                }
            }

            if (state.isRunning) {
                item {
                    Column {
                        Text(
                            text = "Running ${state.runningTechnology?.label ?: "benchmark"}…",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            }

            item {
                LatestResultCard(
                    metric = state.latestMetric,
                    character = state.latestCharacter
                )
            }

            item {
                Text(
                    text = "History",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = state.historyFilter == null,
                        onClick = { viewModel.setHistoryFilter(null) },
                        label = { Text("All") }
                    )
                    ProtocolTechnology.entries.forEach { technology ->
                        FilterChip(
                            selected = state.historyFilter == technology,
                            onClick = { viewModel.setHistoryFilter(technology) },
                            label = { Text(technology.label) }
                        )
                    }
                }
            }

            if (state.history.isEmpty()) {
                item {
                    Text(
                        text = "No runs yet. Execute a protocol to populate Room history.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                items(state.history, key = { it.id }) { metric ->
                    HistoryRow(metric)
                }
            }
        }
    }
}

@Composable
private fun LatestResultCard(
    metric: BenchmarkMetric?,
    character: CharacterSnapshot?
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Latest run",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (metric == null) {
                Text("No result yet.")
            } else {
                Text("${metric.technology.label} · ${metric.endpointTag}")
                if (character != null) {
                    Text("${character.name} (${character.status} / ${character.species})")
                }
                Spacer(modifier = Modifier.height(8.dp))
                MetricGrid(metric)
            }
        }
    }
}

@Composable
private fun HistoryRow(metric: BenchmarkMetric) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = metric.technology.label,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = formatTimestamp(metric.timestamp),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Text(metric.endpointTag, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(6.dp))
            MetricGrid(metric)
        }
    }
}

@Composable
private fun MetricGrid(metric: BenchmarkMetric) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(String.format(Locale.US, "Latency: %.2f ms", metric.latencyMs))
        Text("Payload: ${metric.payloadSizeBytes} bytes")
        Text("Memory Δ: ${metric.memoryUsageBytes} bytes")
        Text(String.format(Locale.US, "CPU: %.1f%%", metric.cpuUsagePercent))
    }
}

private fun formatTimestamp(timestamp: Long): String =
    DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM).format(Date(timestamp))
