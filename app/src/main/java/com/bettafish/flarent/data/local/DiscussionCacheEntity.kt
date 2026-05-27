package com.bettafish.flarent.data.local

import androidx.room.Entity

@Entity(
    tableName = "discussion_list_cache",
    primaryKeys = ["cacheKey", "discussionId"]
)
data class DiscussionCacheEntity(
    val cacheKey: String,
    val discussionId: String,
    val position: Int,
    val payload: String,
    val cachedAt: Long
)
