package com.bettafish.flarent.data

import com.bettafish.flarent.network.FlarumService

class PostsRepositoryImpl(private val service: FlarumService): PostsRepository {
    override suspend fun fetchPosts(id: List<String>?, author:String?, type: String?, limit: Int?, offset: Int?, sort: String?)
            = service.getPosts(id,author,type,limit,offset,sort)
}
