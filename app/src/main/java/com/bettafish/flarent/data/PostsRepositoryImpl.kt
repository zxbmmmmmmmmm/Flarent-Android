package com.bettafish.flarent.data

import com.bettafish.flarent.models.Discussion
import com.bettafish.flarent.models.Post
import com.bettafish.flarent.models.request.PostsRequest
import com.bettafish.flarent.network.FlarumService

class PostsRepositoryImpl(private val service: FlarumService): PostsRepository {
    override suspend fun fetchPosts(request: PostsRequest)
            = service.getPosts(request.toQueryMap())

    override suspend fun sendPost(discussionId: String, content: String) : Post {
        val post = Post().apply {
            id = String()
            this.content = content
            discussion = Discussion().apply { id = discussionId } }
        return service.sendPost(post)
    }

    override suspend fun votePost(postId: String, isUpvoted: Boolean, isDownvoted: Boolean) : Post {
        val request = mapOf("data" to mapOf(
            "type" to "posts",
            "attributes" to listOf(isUpvoted, isDownvoted, "vote"),
            "id" to postId,
            ),
        )
        return service.votePost(postId,request)
    }
}
