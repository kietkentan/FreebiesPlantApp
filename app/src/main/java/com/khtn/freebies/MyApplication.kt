package com.khtn.freebies

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.lifecycle.LifecycleObserver
import androidx.multidex.MultiDexApplication
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class MyApplication: MultiDexApplication(), LifecycleObserver, Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration =
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}