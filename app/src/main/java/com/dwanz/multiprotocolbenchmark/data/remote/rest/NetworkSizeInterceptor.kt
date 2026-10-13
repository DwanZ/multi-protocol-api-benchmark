package com.dwanz.multiprotocolbenchmark.data.remote.rest

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class NetworkSizeInterceptor : Interceptor {
    @Volatile
    var lastPayloadSize: Long = 0
        private set

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        val responseBody = response.body
        val bytes = responseBody?.bytes() ?: byteArrayOf()
        lastPayloadSize = bytes.size.toLong()
        val wrappedBody = bytes.toResponseBody(responseBody?.contentType())
        return response.newBuilder().body(wrappedBody).build()
    }
}
