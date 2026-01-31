package com.bettafish.flarent.data

import com.bettafish.flarent.network.FlarumService

class PostsRepositoryImpl(private val service: FlarumService): PostsRepository {
    override suspend fun fetchPostsById(id: List<String>) = service.getPostsById(id)
}