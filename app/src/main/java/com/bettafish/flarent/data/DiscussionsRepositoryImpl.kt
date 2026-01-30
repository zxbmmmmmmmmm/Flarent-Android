package com.bettafish.flarent.data

import com.bettafish.flarent.network.FlarumService

class DiscussionsRepositoryImpl(
    private val service: FlarumService
) : DiscussionsRepository {

    override suspend fun fetchDiscussions(pageOffset: Int, tag:String?) = service.getDiscussions(pageOffset, tag)



    override suspend fun fetchDiscussionById(id: Int, near: Int) = service.getDiscussion(id)

}
