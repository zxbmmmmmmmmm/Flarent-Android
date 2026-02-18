package com.bettafish.flarent.ui.pages.reaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bettafish.flarent.data.PostsRepository
import com.bettafish.flarent.models.Reaction
import com.bettafish.flarent.models.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PostReactionsViewModel(val postRepository: PostsRepository, val postId: String): ViewModel() {
    private val _reactions: MutableStateFlow<List<Pair<Reaction, List<User>>>?> = MutableStateFlow(null)

    val reactions: StateFlow<List<Pair<Reaction, List<User>>>?> = _reactions

    init {
        refresh()
    }

    fun refresh() {
        try {
            viewModelScope.launch {
                val data = postRepository.fetchReactions(postId)
                _reactions.value = data
                    .groupBy {  it.reaction!! }
                    .map { (reaction, users) ->
                        reaction to users.map { it.user!! }
                    }
                    .sortedByDescending { it.second.size }

            }
        }
        catch (e: Exception){

        }
    }
}