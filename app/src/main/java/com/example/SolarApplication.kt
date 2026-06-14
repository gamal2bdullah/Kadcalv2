package com.example

import android.app.Application
import com.example.di.AppContainer
import com.example.di.AppContainerImpl
import com.example.core.logging.SolarLogger

class SolarApplication : Application() {
    lateinit var appContainer: AppContainer

    override fun onCreate() {
        super.onCreate()
        SolarLogger.i("SolarApplication", "Initializing Solar Application and DI container...")
        appContainer = AppContainerImpl(this)
    }
}
