package com.bettafish.flarent.data

import com.bettafish.flarent.models.Discussion
import com.bettafish.flarent.models.Post

interface PostsRepository {
    suspend fun fetchPostsById(id: List<String>): List<Post>
}