package com.bettafish.flarent.data

import com.bettafish.flarent.network.FlarumService

class TagsRepositoryImpl(
    private val service: FlarumService
) : TagsRepository {

    override suspend fun fetchTags() = service.getTags()
}