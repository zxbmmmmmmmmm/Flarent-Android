package com.bettafish.flarent.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.ref.WeakReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

open class AppPreferencesUtils private constructor(ctx: Context) {
    companion object {
        private var instance: AppPreferencesUtils? = null

        fun getInstance(context: Context): AppPreferencesUtils {
            return instance ?: AppPreferencesUtils(context).also {
                instance = it
            }
        }
    }

    private val contextWeakReference: WeakReference<Context> = WeakReference(ctx)

    private val context: Context
        get() = contextWeakReference.get()!!

    private val preferencesDataStore: DataStore<Preferences>
        get() = context.dataStore

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    var token by DataStoreDelegates.string(key = "token")

    private object DataStoreDelegates {
        fun int(
            defaultValue: Int = 0,
            key: String? = null
        ) = object : ReadWriteProperty<AppPreferencesUtils, Int> {
            private var prefValue = defaultValue
            private var initialized = false

            override fun getValue(thisRef: AppPreferencesUtils, property: KProperty<*>): Int {
                val finalKey = key ?: property.name
                if (!initialized) {
                    initialized = true
                    prefValue = thisRef.preferencesDataStore.getInt(finalKey, defaultValue)
                    thisRef.coroutineScope.launch {
                        thisRef.preferencesDataStore.data
                            .map { it[intPreferencesKey(finalKey)] }
                            .distinctUntilChanged()
                            .collect {
                                prefValue = it ?: defaultValue
                            }
                    }
                }
                return prefValue
            }

            override fun setValue(
                thisRef: AppPreferencesUtils,
                property: KProperty<*>,
                value: Int
            ) {
                prefValue = value
                thisRef.coroutineScope.launch {
                    thisRef.preferencesDataStore.edit {
                        it[intPreferencesKey(key ?: property.name)] = value
                    }
                }
            }
        }

        fun string(
            defaultValue: String? = null,
            key: String? = null
        ) = object : ReadWriteProperty<AppPreferencesUtils, String?> {
            private var prefValue = defaultValue
            private var initialized = false

            override fun getValue(thisRef: AppPreferencesUtils, property: KProperty<*>): String? {
                val finalKey = key ?: property.name
                if (!initialized) {
                    initialized = true
                    prefValue = thisRef.preferencesDataStore.getString(finalKey)
                        ?: defaultValue
                    thisRef.coroutineScope.launch {
                        thisRef.preferencesDataStore.data
                            .map { it[stringPreferencesKey(finalKey)] }
                            .distinctUntilChanged()
                            .collect {
                                prefValue = it ?: defaultValue
                            }
                    }
                }
                return prefValue
            }

            override fun setValue(
                thisRef: AppPreferencesUtils,
                property: KProperty<*>,
                value: String?
            ) {
                prefValue = value
                thisRef.coroutineScope.launch {
                    thisRef.preferencesDataStore.edit {
                        if (value == null) {
                            it.remove(stringPreferencesKey(key ?: property.name))
                        } else {
                            it[stringPreferencesKey(key ?: property.name)] = value
                        }
                    }
                }
            }
        }

        fun float(
            defaultValue: Float = 0F,
            key: String? = null
        ) = object : ReadWriteProperty<AppPreferencesUtils, Float> {
            private var prefValue = defaultValue
            private var initialized = false

            override fun getValue(thisRef: AppPreferencesUtils, property: KProperty<*>): Float {
                val finalKey = key ?: property.name
                if (!initialized) {
                    initialized = true
                    prefValue =
                        thisRef.preferencesDataStore.getFloat(finalKey, defaultValue)
                    thisRef.coroutineScope.launch {
                        thisRef.preferencesDataStore.data
                            .map { it[floatPreferencesKey(finalKey)] }
                            .distinctUntilChanged()
                            .collect {
                                prefValue = it ?: defaultValue
                            }
                    }
                }
                return prefValue
            }

            override fun setValue(
                thisRef: AppPreferencesUtils,
                property: KProperty<*>,
                value: Float
            ) {
                prefValue = value
                thisRef.coroutineScope.launch {
                    thisRef.preferencesDataStore.edit {
                        it[floatPreferencesKey(key ?: property.name)] = value
                    }
                }
            }
        }

        fun long(
            defaultValue: Long = 0L,
            key: String? = null
        ) = object : ReadWriteProperty<AppPreferencesUtils, Long> {
            private var prefValue = defaultValue
            private var initialized = false

            override fun getValue(thisRef: AppPreferencesUtils, property: KProperty<*>): Long {
                val finalKey = key ?: property.name
                if (!initialized) {
                    initialized = true
                    prefValue =
                        thisRef.preferencesDataStore.getLong(finalKey, defaultValue)
                    thisRef.coroutineScope.launch {
                        thisRef.preferencesDataStore.data
                            .map { it[longPreferencesKey(finalKey)] }
                            .distinctUntilChanged()
                            .collect {
                                prefValue = it ?: defaultValue
                            }
                    }
                }
                return prefValue
            }

            override fun setValue(
                thisRef: AppPreferencesUtils,
                property: KProperty<*>,
                value: Long
            ) {
                prefValue = value
                thisRef.coroutineScope.launch {
                    thisRef.preferencesDataStore.edit {
                        it[longPreferencesKey(key ?: property.name)] = value
                    }
                }
            }
        }

        fun boolean(
            defaultValue: Boolean = false,
            key: String? = null
        ) = object : ReadWriteProperty<AppPreferencesUtils, Boolean> {
            private var prefValue = defaultValue
            private var initialized = false

            override fun getValue(thisRef: AppPreferencesUtils, property: KProperty<*>): Boolean {
                val finalKey = key ?: property.name
                if (!initialized) {
                    initialized = true
                    prefValue =
                        thisRef.preferencesDataStore.getBoolean(finalKey, defaultValue)
                    thisRef.coroutineScope.launch {
                        thisRef.preferencesDataStore.data
                            .map { it[booleanPreferencesKey(finalKey)] }
                            .distinctUntilChanged()
                            .collect {
                                prefValue = it ?: defaultValue
                            }
                    }
                }
                return prefValue
            }

            override fun setValue(
                thisRef: AppPreferencesUtils,
                property: KProperty<*>,
                value: Boolean
            ) {
                prefValue = value
                thisRef.coroutineScope.launch {
                    thisRef.preferencesDataStore.edit {
                        it[booleanPreferencesKey(key ?: property.name)] = value
                    }
                }
            }
        }


        inline fun <reified T> any(
                defaultValue: T? = null,
                key: String? = null
        ) = object : ReadWriteProperty<AppPreferencesUtils, T?> {
            private var prefValue = defaultValue
            private var initialized = false

            override fun getValue(thisRef: AppPreferencesUtils, property: KProperty<*>): T? {
                val finalKey = key ?: property.name
                if (!initialized) {
                    initialized = true
                    prefValue =
                        Json.decodeFromString( thisRef.preferencesDataStore.getString(finalKey, Json.encodeToString(defaultValue)))
                    thisRef.coroutineScope.launch {
                        thisRef.preferencesDataStore.data
                            .map { it[stringPreferencesKey(finalKey)] }
                            .distinctUntilChanged()
                            .collect {
                                prefValue = it?.let { str -> Json.decodeFromString(str) } ?: defaultValue
                            }
                    }
                }
                return prefValue
            }

            override fun setValue(
                thisRef: AppPreferencesUtils,
                property: KProperty<*>,
                value: T?
            ) {
                prefValue = value
                thisRef.coroutineScope.launch {
                    thisRef.preferencesDataStore.edit {
                        it[stringPreferencesKey(key ?: property.name)] = Json.encodeToString(defaultValue)
                    }
                }
            }
        }
    }
}

val Context.appPreferences: AppPreferencesUtils
    get() = AppPreferencesUtils.getInstance(this)