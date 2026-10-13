package com.dwanz.multiprotocolbenchmark.domain.model

enum class ProtocolTechnology(val storageKey: String, val label: String) {
    REST("REST", "REST"),
    GRPC("GRPC", "gRPC"),
    GRAPHQL("GRAPHQL", "GraphQL");

    companion object {
        const val ENDPOINT_GET_CHARACTER = "GetCharacter"
        const val CHARACTER_ID = 1

        fun fromStorageKey(key: String): ProtocolTechnology? =
            entries.firstOrNull { it.storageKey == key }
    }
}
