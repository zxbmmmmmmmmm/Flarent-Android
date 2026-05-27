package com.bettafish.flarent.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.RemoteMediator
import com.bettafish.flarent.data.local.DiscussionCacheDao
import com.bettafish.flarent.models.Discussion
import com.bettafish.flarent.models.request.DiscussionListRequest
import com.bettafish.flarent.models.request.DiscussionRequest
import com.bettafish.flarent.network.FlarumService
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class DiscussionsRepositoryImpl(
    private val service: FlarumService,
    private val discussionCacheDao: DiscussionCacheDao,
    private val objectMapper: ObjectMapper,
    private val memoryCache: DiscussionListMemoryCache
) : DiscussionsRepository {
    private val backgroundScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override suspend fun fetchDiscussionList(request: DiscussionListRequest): List<Discussion> =
        service.getDiscussionList(request.toQueryMap())

    override fun discussionListPagingSource(
        request: DiscussionListRequest
    ): PagingSource<Int, Discussion> =
        DiscussionListPagingSource(
            request = request,
            discussionCacheDao = discussionCacheDao,
            memoryCache = memoryCache,
            objectMapper = objectMapper
        )

    @OptIn(ExperimentalPagingApi::class)
    override fun discussionListRemoteMediator(
        request: DiscussionListRequest,
        pageSize: Int
    ): RemoteMediator<Int, Discussion> =
        DiscussionListRemoteMediator(
            request = request,
            pageSize = pageSize,
            service = service,
            discussionCacheDao = discussionCacheDao,
            memoryCache = memoryCache,
            objectMapper = objectMapper
        )

    override suspend fun fetchDiscussion(request: DiscussionRequest) =
        service.getDiscussion(request.id, request.toQueryMap())

    override suspend fun readDiscussion(discussionId: String, lastReadPostNumber: Int) {
        updateCachedDiscussion(discussionId) {
            this.lastReadPostNumber = lastReadPostNumber
        }
        val changedMemoryCacheKeys = memoryCache.updateDiscussion(discussionId) {
            this.lastReadPostNumber = lastReadPostNumber
        }
        changedMemoryCacheKeys.forEach { memoryCache.invalidate(it) }

        backgroundScope.launch {
            runCatching {
                patchDiscussion(discussionId = discussionId) {
                    this.lastReadPostNumber = lastReadPostNumber
                }
            }
        }
    }

    private suspend fun updateCachedDiscussion(discussionId: String, block: Discussion.() -> Unit) {
        val rows = discussionCacheDao.getCachedDiscussionRows(discussionId)
        if (rows.isEmpty()) return

        val updatedRows = rows.mapNotNull { row ->
            val discussion = runCatching {
                objectMapper.readValue(row.payload, Discussion::class.java)
            }.getOrNull() ?: return@mapNotNull null

            block(discussion)
            row.copy(
                payload = objectMapper.writeValueAsString(discussion),
                cachedAt = System.currentTimeMillis()
            )
        }
        discussionCacheDao.updateAll(updatedRows)
        rows.map { it.cacheKey }.distinct().forEach { memoryCache.invalidate(it) }
    }

    suspend fun patchDiscussion(discussionId: String, block: Discussion.() -> Unit): Discussion? {
        val discussion = Discussion().apply { id = discussionId }
        block(discussion)
        return service.patchDiscussion(discussionId, discussion)
    }
}
