package com.dwanz.multiprotocolbenchmark.data.remote.grpc

import com.dwanz.multiprotocolbenchmark.grpc.Character
import com.dwanz.multiprotocolbenchmark.grpc.CharacterServiceGrpcKt
import com.dwanz.multiprotocolbenchmark.grpc.GetCharacterRequest

/**
 * In-process CharacterService implementation used as the local gRPC bridge/mock stub.
 * Returns a fixed Rick Sanchez payload matching the public REST/GraphQL character id=1 shape.
 */
class LocalCharacterService : CharacterServiceGrpcKt.CharacterServiceCoroutineImplBase() {
    override suspend fun getCharacter(request: GetCharacterRequest): Character {
        return Character.newBuilder()
            .setId(request.id)
            .setName("Rick Sanchez")
            .setStatus("Alive")
            .setSpecies("Human")
            .build()
    }
}
