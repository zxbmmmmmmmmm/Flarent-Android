package com.bettafish.flarent

import android.app.Application
import com.bettafish.flarent.di.networkModule
import com.bettafish.flarent.di.repositoryModule
import com.bettafish.flarent.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(listOf(networkModule, repositoryModule, viewModelModule))
        }
    }
}
