package com.bettafish.flarent.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

object DiscussionLastReadPostNumberStore {
    private val lock = Any()
    private val values = MutableStateFlow<Map<String, Int>>(emptyMap())

    fun get(discussionId: String): Int? = values.value[discussionId]

    fun observe(discussionId: String): Flow<Int?> =
        values.map { it[discussionId] }.distinctUntilChanged()

    fun update(discussionId: String, lastReadPostNumber: Int): Int {
        synchronized(lock) {
            val currentValue = values.value[discussionId]
            val resolvedValue = if (currentValue == null || lastReadPostNumber > currentValue) {
                lastReadPostNumber
            } else {
                currentValue
            }

            if (currentValue != resolvedValue) {
                values.value = values.value + (discussionId to resolvedValue)
            }

            return resolvedValue
        }
    }
}


