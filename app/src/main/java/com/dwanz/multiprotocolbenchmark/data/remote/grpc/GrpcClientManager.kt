package com.dwanz.multiprotocolbenchmark.data.remote.grpc

import com.dwanz.multiprotocolbenchmark.grpc.CharacterServiceGrpcKt
import io.grpc.ManagedChannel
import io.grpc.Server
import io.grpc.inprocess.InProcessChannelBuilder
import io.grpc.inprocess.InProcessServerBuilder
import java.util.concurrent.TimeUnit

object GrpcClientManager {
    private const val SERVER_NAME = "local-character-service"

    @Volatile
    private var server: Server? = null

    @Volatile
    private var channel: ManagedChannel? = null

    val stub: CharacterServiceGrpcKt.CharacterServiceCoroutineStub
        get() {
            ensureStarted()
            return CharacterServiceGrpcKt.CharacterServiceCoroutineStub(requireNotNull(channel))
        }

    @Synchronized
    private fun ensureStarted() {
        if (server != null && channel != null) return

        server = InProcessServerBuilder
            .forName(SERVER_NAME)
            .directExecutor()
            .addService(LocalCharacterService())
            .build()
            .start()

        channel = InProcessChannelBuilder
            .forName(SERVER_NAME)
            .directExecutor()
            .build()
    }

    @Synchronized
    fun shutdown() {
        channel?.shutdownNow()
        server?.shutdownNow()
        channel?.awaitTermination(1, TimeUnit.SECONDS)
        server?.awaitTermination(1, TimeUnit.SECONDS)
        channel = null
        server = null
    }
}
