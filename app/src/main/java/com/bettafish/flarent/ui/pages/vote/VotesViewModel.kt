package com.bettafish.flarent.ui.pages.vote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bettafish.flarent.data.PostsRepository
import com.bettafish.flarent.models.User
import com.bettafish.flarent.models.request.PostsRequest
import com.bettafish.flarent.utils.SuspendCommand1
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class VotesViewModel(val repository: PostsRepository,
                     val postId: String) : ViewModel() {
    private val _upvoters = MutableStateFlow<List<User>?>(null)
    val upvoters = _upvoters.asStateFlow()

    private val _downvoters = MutableStateFlow<List<User>?>(null)
    val downvoters = _downvoters.asStateFlow()

    val refreshCommand = SuspendCommand1(::refresh, viewModelScope)

    init {
        refreshCommand.execute(postId)
    }

    suspend fun refresh(postId: String){
        val request = PostsRequest(listOf(postId), include = listOf("upvotes", "downvotes"))
        val data = repository.fetchPosts(request)
        _upvoters.value = data.firstOrNull()?.upvotes
        _downvoters.value = data.firstOrNull()?.downvotes
    }
}