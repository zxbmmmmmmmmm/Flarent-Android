package com.bettafish.flarent.data

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.bettafish.flarent.data.local.DiscussionCacheDao
import com.bettafish.flarent.data.local.DiscussionCacheEntity
import com.bettafish.flarent.models.Discussion
import com.bettafish.flarent.models.request.DiscussionListRequest
import com.bettafish.flarent.network.FlarumService
import com.fasterxml.jackson.databind.ObjectMapper

@OptIn(ExperimentalPagingApi::class)
class DiscussionListRemoteMediator(
    private val request: DiscussionListRequest,
    private val pageSize: Int,
    private val service: FlarumService,
    private val discussionCacheDao: DiscussionCacheDao,
    private val memoryCache: DiscussionListMemoryCache,
    private val objectMapper: ObjectMapper
) : RemoteMediator<Int, Discussion>() {
    private val cacheKey = request.cacheKey()

    override suspend fun initialize(): InitializeAction =
        InitializeAction.LAUNCH_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Discussion>
    ): MediatorResult =
        try {
            when (loadType) {
                LoadType.REFRESH -> refresh()
                LoadType.PREPEND -> MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> append()
            }
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }

    private suspend fun refresh(): MediatorResult.Success {
        val networkItems = service.getDiscussionList(
            request.copy(offset = 0, limit = pageSize).toQueryMap()
        )
        val endReached = networkItems.size < pageSize
        val now = System.currentTimeMillis()
        val entities = networkItems.take(pageSize).mapIndexed { index, discussion ->
            DiscussionCacheEntity(
                cacheKey = cacheKey,
                discussionId = discussion.id,
                position = index,
                payload = objectMapper.writeValueAsString(discussion),
                cachedAt = now
            )
        }

        discussionCacheDao.replaceCache(cacheKey, entities)
        memoryCache.replaceAfterRefresh(cacheKey, pageSize, endReached)
        memoryCache.invalidate(cacheKey)
        return MediatorResult.Success(endOfPaginationReached = endReached)
    }

    private suspend fun append(): MediatorResult.Success {
        val offset = memoryCache.nextOffset(cacheKey)
            ?: return MediatorResult.Success(endOfPaginationReached = true)
        val networkItems = service.getDiscussionList(
            request.copy(offset = offset, limit = pageSize).toQueryMap()
        )
        val endReached = networkItems.size < pageSize

        memoryCache.appendPage(cacheKey, offset, networkItems, pageSize)
        memoryCache.invalidate(cacheKey)
        return MediatorResult.Success(endOfPaginationReached = endReached)
    }
}
