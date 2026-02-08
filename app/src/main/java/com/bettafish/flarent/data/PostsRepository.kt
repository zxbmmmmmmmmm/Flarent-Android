package com.bettafish.flarent.data

import com.bettafish.flarent.models.Post

interface PostsRepository {
    suspend fun fetchPosts(id: List<String>? = null, author: String? = null, type: String? = null, limit: Int? = null, offset: Int? = null, sort: String? = null): List<Post>

    suspend fun sendPost(discussionId : String, content : String) : Post
}