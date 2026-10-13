package com.dwanz.multiprotocolbenchmark.data.repository

import com.dwanz.multiprotocolbenchmark.data.local.BenchmarkDao
import com.dwanz.multiprotocolbenchmark.data.local.BenchmarkMetricEntity
import com.dwanz.multiprotocolbenchmark.data.metrics.BenchmarkExecutor
import com.dwanz.multiprotocolbenchmark.data.remote.graphql.GraphQLClientInstance
import com.dwanz.multiprotocolbenchmark.data.remote.grpc.GrpcClientManager
import com.dwanz.multiprotocolbenchmark.data.remote.rest.RestClient
import com.dwanz.multiprotocolbenchmark.domain.model.BenchmarkMetric
import com.dwanz.multiprotocolbenchmark.domain.model.CharacterSnapshot
import com.dwanz.multiprotocolbenchmark.domain.model.ProtocolTechnology
import com.dwanz.multiprotocolbenchmark.domain.repository.BenchmarkRepository
import com.dwanz.multiprotocolbenchmark.domain.repository.BenchmarkRunResult
import com.dwanz.multiprotocolbenchmark.graphql.GetCharacterQuery
import com.dwanz.multiprotocolbenchmark.grpc.GetCharacterRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class BenchmarkRepositoryImpl(
    private val dao: BenchmarkDao,
    private val executor: BenchmarkExecutor
) : BenchmarkRepository {

    override fun observeAllMetrics(): Flow<List<BenchmarkMetric>> =
        dao.getAllMetrics().map { list -> list.mapNotNull { it.toDomainOrNull() } }

    override fun observeMetricsByTechnology(technology: ProtocolTechnology): Flow<List<BenchmarkMetric>> =
        dao.getMetricsByTechnology(technology.storageKey)
            .map { list -> list.mapNotNull { it.toDomainOrNull() } }

    override suspend fun runBenchmark(technology: ProtocolTechnology): BenchmarkRunResult =
        withContext(Dispatchers.IO) {
            when (technology) {
                ProtocolTechnology.REST -> runRest()
                ProtocolTechnology.GRAPHQL -> runGraphQl()
                ProtocolTechnology.GRPC -> runGrpc()
            }
        }

    private suspend fun runRest(): BenchmarkRunResult {
        val result = executor.measure(
            technology = ProtocolTechnology.REST.storageKey,
            endpointTag = ProtocolTechnology.ENDPOINT_GET_CHARACTER
        ) {
            val dto = RestClient.api.getCharacter(ProtocolTechnology.CHARACTER_ID)
            val snapshot = CharacterSnapshot(
                id = dto.id,
                name = dto.name,
                status = dto.status,
                species = dto.species
            )
            snapshot to RestClient.getLastPayloadSize()
        }
        return BenchmarkRunResult(result.data, result.metric.toDomain())
    }

    private suspend fun runGraphQl(): BenchmarkRunResult {
        val result = executor.measure(
            technology = ProtocolTechnology.GRAPHQL.storageKey,
            endpointTag = ProtocolTechnology.ENDPOINT_GET_CHARACTER
        ) {
            val response = GraphQLClientInstance.apolloClient
                .query(GetCharacterQuery(id = ProtocolTechnology.CHARACTER_ID.toString()))
                .execute()

            if (response.hasErrors()) {
                val message = response.errors?.joinToString { it.message } ?: "Unknown GraphQL error"
                throw IllegalStateException(message)
            }

            val character = response.data?.character
                ?: throw IllegalStateException("GraphQL character payload was null")

            val snapshot = CharacterSnapshot(
                id = character.id?.toIntOrNull() ?: ProtocolTechnology.CHARACTER_ID,
                name = character.name.orEmpty(),
                status = character.status.orEmpty(),
                species = character.species.orEmpty()
            )

            snapshot to GraphQLClientInstance.getLastPayloadSize()
        }
        return BenchmarkRunResult(result.data, result.metric.toDomain())
    }

    private suspend fun runGrpc(): BenchmarkRunResult {
        val result = executor.measure(
            technology = ProtocolTechnology.GRPC.storageKey,
            endpointTag = ProtocolTechnology.ENDPOINT_GET_CHARACTER
        ) {
            val request = GetCharacterRequest.newBuilder()
                .setId(ProtocolTechnology.CHARACTER_ID)
                .build()
            val character = GrpcClientManager.stub.getCharacter(request)
            val snapshot = CharacterSnapshot(
                id = character.id,
                name = character.name,
                status = character.status,
                species = character.species
            )
            val payloadSize = character.toByteArray().size.toLong()
            snapshot to payloadSize
        }
        return BenchmarkRunResult(result.data, result.metric.toDomain())
    }

    private fun BenchmarkMetricEntity.toDomainOrNull(): BenchmarkMetric? {
        val tech = ProtocolTechnology.fromStorageKey(technology) ?: return null
        return BenchmarkMetric(
            id = id,
            technology = tech,
            timestamp = timestamp,
            latencyMs = latencyMs,
            payloadSizeBytes = payloadSizeBytes,
            memoryUsageBytes = memoryUsageBytes,
            cpuUsagePercent = cpuUsagePercent,
            endpointTag = endpointTag
        )
    }

    private fun BenchmarkMetricEntity.toDomain(): BenchmarkMetric =
        toDomainOrNull()
            ?: error("Unknown technology key: $technology")
}
