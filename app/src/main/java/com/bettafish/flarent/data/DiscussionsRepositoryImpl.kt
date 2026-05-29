package com.bettafish.flarent.data

import com.bettafish.flarent.models.Discussion
import com.bettafish.flarent.models.request.DiscussionListRequest
import com.bettafish.flarent.models.request.DiscussionRequest
import com.bettafish.flarent.network.FlarumService

class DiscussionsRepositoryImpl(
    private val service: FlarumService
) : DiscussionsRepository {

    override suspend fun fetchDiscussionList(request: DiscussionListRequest) =
        service.getDiscussionList(request.toQueryMap())

    override suspend fun fetchDiscussion(request: DiscussionRequest) =
        service.getDiscussion(request.id, request.toQueryMap())

    override suspend fun updateLastReadPostNumber(id: String, lastReadPostNumber: Int) {
        patchDiscussion(id) {
            this.lastReadPostNumber = lastReadPostNumber
        }
    }
    suspend fun patchDiscussion(discussionId :String, block: Discussion.() -> Unit): Discussion?{
        val discussion = Discussion().apply { id = discussionId }
        block(discussion)
        return service.patchDiscussion(discussionId, discussion)
    }
}
