package com.bettafish.flarent.data

import com.bettafish.flarent.models.Discussion
import com.bettafish.flarent.network.FlarumService

class DiscussionsRepositoryImpl(
    private val service: FlarumService
) : DiscussionsRepository {

    override suspend fun fetchDiscussions(page: Int) = service.getDiscussions(page)



    override suspend fun fetchDiscussionById(id: String) = service.getDiscussion(id)

}
