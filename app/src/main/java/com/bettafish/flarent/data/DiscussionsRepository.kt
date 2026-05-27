package com.bettafish.flarent.data

import androidx.paging.PagingSource
import androidx.paging.ExperimentalPagingApi
import androidx.paging.RemoteMediator
import com.bettafish.flarent.models.Discussion
import com.bettafish.flarent.models.request.DiscussionListRequest
import com.bettafish.flarent.models.request.DiscussionRequest

interface DiscussionsRepository {
    suspend fun fetchDiscussionList(request: DiscussionListRequest): List<Discussion>
    fun discussionListPagingSource(request: DiscussionListRequest): PagingSource<Int, Discussion>
    @OptIn(ExperimentalPagingApi::class)
    fun discussionListRemoteMediator(
        request: DiscussionListRequest,
        pageSize: Int
    ): RemoteMediator<Int, Discussion>
    suspend fun fetchDiscussion(request: DiscussionRequest): Discussion

    suspend fun readDiscussion(discussionId:String, lastReadPostNumber: Int)
}
