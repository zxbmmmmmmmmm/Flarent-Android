package com.bettafish.flarent.data

import com.bettafish.flarent.models.Discussion
import com.bettafish.flarent.models.Post
import com.bettafish.flarent.network.FlarumService

class PostsRepositoryImpl(private val service: FlarumService): PostsRepository {
    override suspend fun fetchPosts(id: List<String>?, author:String?, type: String?, limit: Int?, offset: Int?, sort: String?)
            = service.getPosts(id,author,type,limit,offset,sort)

    override suspend fun sendPost(discussionId: String, content: String) : Post {
        val post = Post().apply {
            id = String()
            this.content = content
            discussion = Discussion().apply { id = discussionId } }
        return service.sendPost(post)
    }
}
