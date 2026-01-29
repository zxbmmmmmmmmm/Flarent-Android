package com.bettafish.flarent.data

import com.bettafish.flarent.models.Discussion

interface DiscussionsRepository {
    suspend fun fetchDiscussions(pageOffset: Int = 0, tag: String? = null): List<Discussion>
    suspend fun fetchDiscussionById(id: String): Discussion
}
