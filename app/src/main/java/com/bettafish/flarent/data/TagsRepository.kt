package com.bettafish.flarent.data

import com.bettafish.flarent.models.Tag

interface TagsRepository {
    suspend fun fetchTags(): List<Tag>
}
