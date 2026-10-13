package com.dwanz.multiprotocolbenchmark

import android.app.Application
import com.dwanz.multiprotocolbenchmark.data.remote.grpc.GrpcClientManager
import com.dwanz.multiprotocolbenchmark.di.AppContainer

class BenchmarkApp : Application() {
    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }

    override fun onTerminate() {
        GrpcClientManager.shutdown()
        super.onTerminate()
    }
}
