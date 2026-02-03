package com.bettafish.flarent.data

import com.bettafish.flarent.network.FlarumService

class DiscussionsRepositoryImpl(
    private val service: FlarumService
) : DiscussionsRepository {

    override suspend fun fetchDiscussions(pageOffset: Int, tag: String?, author: String?, sort: String?) = service.getDiscussions(pageOffset, tag, author, sort)



    override suspend fun fetchDiscussionById(id: String, near: Int, limit:Int) = service.getDiscussion(id,near,limit)

}
