package com.bettafish.flarent.data

import androidx.paging.PagingSource
import com.bettafish.flarent.models.Discussion
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class DiscussionListMemoryCache {
    private data class Entry(
        val pages: MutableMap<Int, List<Discussion>> = linkedMapOf(),
        var nextOffset: Int? = null
    )

    private val mutex = Mutex()
    private val entries = mutableMapOf<String, Entry>()
    private val invalidatorsLock = Any()
    private val invalidators = mutableMapOf<String, MutableSet<PagingSource<*, *>>>()

    fun register(cacheKey: String, pagingSource: PagingSource<*, *>) {
        synchronized(invalidatorsLock) {
            invalidators.getOrPut(cacheKey) { mutableSetOf() }.add(pagingSource)
        }
        pagingSource.registerInvalidatedCallback {
            synchronized(invalidatorsLock) {
                invalidators[cacheKey]?.remove(pagingSource)
            }
        }
    }

    fun invalidate(cacheKey: String) {
        val sources = synchronized(invalidatorsLock) {
            invalidators[cacheKey]?.toList().orEmpty()
        }
        sources.forEach { it.invalidate() }
    }

    suspend fun replaceAfterRefresh(cacheKey: String, pageSize: Int, endReached: Boolean) {
        mutex.withLock {
            entries[cacheKey] = Entry(
                nextOffset = if (endReached) null else pageSize
            )
        }
    }

    suspend fun appendPage(
        cacheKey: String,
        offset: Int,
        items: List<Discussion>,
        pageSize: Int
    ) {
        mutex.withLock {
            val entry = entries.getOrPut(cacheKey) { Entry() }
            entry.pages[offset] = items
            entry.nextOffset = if (items.size < pageSize) null else offset + pageSize
        }
    }

    suspend fun nextOffset(cacheKey: String): Int? =
        mutex.withLock { entries[cacheKey]?.nextOffset }

    suspend fun loadedDiscussions(cacheKey: String): List<Discussion> =
        mutex.withLock {
            entries[cacheKey]
                ?.pages
                ?.toSortedMap()
                ?.values
                ?.flatten()
                .orEmpty()
        }

    suspend fun updateDiscussion(
        discussionId: String,
        block: Discussion.() -> Unit
    ): Set<String> =
        mutex.withLock {
            buildSet {
                entries.forEach { (cacheKey, entry) ->
                    entry.pages.forEach { (_, discussions) ->
                        discussions.forEach { discussion ->
                            if (discussion.id == discussionId) {
                                block(discussion)
                                add(cacheKey)
                            }
                        }
                    }
                }
            }
        }
}
