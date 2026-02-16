package com.bettafish.flarent.data

import com.bettafish.flarent.models.Forum

interface ForumRepository{
    suspend fun fetchForum() : Forum
}