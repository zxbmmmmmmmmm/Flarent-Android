package com.bettafish.flarent.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bettafish.flarent.data.local.DiscussionCacheDao
import com.bettafish.flarent.models.Discussion
import com.bettafish.flarent.models.request.DiscussionListRequest
import com.fasterxml.jackson.databind.ObjectMapper

class DiscussionListPagingSource(
    private val request: DiscussionListRequest,
    private val discussionCacheDao: DiscussionCacheDao,
    private val memoryCache: DiscussionListMemoryCache,
    private val objectMapper: ObjectMapper
) : PagingSource<Int, Discussion>() {
    private val cacheKey = request.cacheKey()

    init {
        memoryCache.register(cacheKey, this)
    }

    override fun getRefreshKey(state: PagingState<Int, Discussion>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Discussion> =
        try {
            val diskItems = discussionCacheDao.getCachedDiscussions(cacheKey)
                .mapNotNull { entity ->
                    runCatching {
                        objectMapper.readValue(entity.payload, Discussion::class.java)
                    }.getOrNull()
                }
            val memoryItems = memoryCache.loadedDiscussions(cacheKey)

            LoadResult.Page(
                data = diskItems + memoryItems,
                prevKey = null,
                nextKey = null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
}
