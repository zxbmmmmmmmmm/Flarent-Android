package com.bettafish.flarent.data

import com.bettafish.flarent.network.FlarumService

class DiscussionsRepositoryImpl(
    private val service: FlarumService
) : DiscussionsRepository {

    override suspend fun fetchDiscussions(pageOffset: Int) = service.getDiscussions(pageOffset)



    override suspend fun fetchDiscussionById(id: String) = service.getDiscussion(id)

}
