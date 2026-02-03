package com.bettafish.flarent

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.bettafish.flarent.di.networkModule
import com.bettafish.flarent.di.repositoryModule
import com.bettafish.flarent.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        INSTANCE = this
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(listOf(networkModule, repositoryModule, viewModelModule))
        }
    }
    companion object{
        lateinit var INSTANCE:App
    }
}
