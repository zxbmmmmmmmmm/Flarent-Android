package com.bettafish.flarent.data

import com.bettafish.flarent.models.Forum
import com.bettafish.flarent.network.FlarumService

class ForumRepositoryImpl(private val service: FlarumService): ForumRepository {
    override suspend fun fetchForum() : Forum = service.getForum()
}