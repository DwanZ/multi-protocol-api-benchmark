package com.dwanz.multiprotocolbenchmark.data.remote.graphql

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.network.okHttpClient
import com.dwanz.multiprotocolbenchmark.data.remote.rest.NetworkSizeInterceptor
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object GraphQLClientInstance {
    private const val SERVER_URL = "https://rickandmortyapi.com/graphql"

    private val sizeInterceptor = NetworkSizeInterceptor()

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(sizeInterceptor)
            .build()
    }

    val apolloClient: ApolloClient by lazy {
        ApolloClient.Builder()
            .serverUrl(SERVER_URL)
            .okHttpClient(okHttpClient)
            .build()
    }

    fun getLastPayloadSize(): Long = sizeInterceptor.lastPayloadSize
}
