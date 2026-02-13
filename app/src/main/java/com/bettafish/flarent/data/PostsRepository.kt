package com.bettafish.flarent.data

import com.bettafish.flarent.models.Post
import com.bettafish.flarent.models.request.PostsRequest

interface PostsRepository {
    suspend fun fetchPosts(request: PostsRequest): List<Post>

    suspend fun sendPost(discussionId : String, content : String) : Post

    suspend fun votePost(postId: String, isUpvoted: Boolean, isDownvoted: Boolean): Post
}