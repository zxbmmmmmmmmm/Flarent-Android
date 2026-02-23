package com.bettafish.flarent.utils

import com.bettafish.flarent.models.Post
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow

object GlobalPostUpdateManager {
    private  val _events = MutableSharedFlow<Post>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()
    suspend fun emitPost(post: Post){
        _events.emit(post)
    }
}