package com.bettafish.flarent.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map


class LocalUpdatedValueStore<T> {
    companion object{
        val DiscussionLastReadPostNumberStore = LocalUpdatedValueStore<Int>()
        val NotificationIsReadStore = LocalUpdatedValueStore<Boolean>()
    }
    private val lock = Any()
    private val values = MutableStateFlow<Map<String, T>>(emptyMap())

    operator fun get(key: String): T? = values.value[key]

    fun observe(key: String): Flow<T?> =
        values.map { it[key] }.distinctUntilChanged()

    fun update(key: String, value: T) {
        synchronized(lock) {
            val currentValue = values.value[key]
            if (currentValue != value) {
                values.value += (key to value)
            }
        }
    }
}