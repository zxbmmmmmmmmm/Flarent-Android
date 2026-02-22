package com.bettafish.flarent.data

import com.bettafish.flarent.models.Post
import com.bettafish.flarent.models.PostReactions
import com.bettafish.flarent.models.request.PostsRequest

interface PostsRepository {
    suspend fun fetchPosts(request: PostsRequest): List<Post>

    suspend fun sendPost(discussionId : String, content : String) : Post

    suspend fun votePost(postId: String, isUpvoted: Boolean, isDownvoted: Boolean): Post

    suspend fun reactPost(postId: String, reactionId: String): Post

    suspend fun fetchReactions(postId: String): List<PostReactions>

    suspend fun editPost(postId: String, content: String): Post
}