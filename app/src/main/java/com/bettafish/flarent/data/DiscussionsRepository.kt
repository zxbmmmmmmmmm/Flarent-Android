package com.bettafish.flarent.data

import com.bettafish.flarent.models.Discussion

interface DiscussionsRepository {
    suspend fun fetchDiscussions(page: Int = 1): List<Discussion>
    suspend fun fetchDiscussionById(id: String): Discussion
}
