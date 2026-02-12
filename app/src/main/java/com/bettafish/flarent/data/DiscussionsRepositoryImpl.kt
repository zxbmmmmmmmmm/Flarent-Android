package com.bettafish.flarent.data

import com.bettafish.flarent.models.request.DiscussionListRequest
import com.bettafish.flarent.models.request.DiscussionRequest
import com.bettafish.flarent.network.FlarumService

class DiscussionsRepositoryImpl(
    private val service: FlarumService
) : DiscussionsRepository {

    override suspend fun fetchDiscussionList(request: DiscussionListRequest) = service.getDiscussionList(request.toQueryMap())

    override suspend fun fetchDiscussion(request: DiscussionRequest) = service.getDiscussion(request.toQueryMap())
}
