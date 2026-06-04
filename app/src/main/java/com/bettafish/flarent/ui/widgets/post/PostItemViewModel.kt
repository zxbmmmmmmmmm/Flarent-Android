package com.bettafish.flarent.ui.widgets.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bettafish.flarent.data.PostsRepository
import com.bettafish.flarent.models.Post
import com.bettafish.flarent.models.request.PostsRequest
import com.bettafish.flarent.utils.HtmlConverter
import com.bettafish.flarent.utils.SuspendCommand2
import com.bettafish.flarent.utils.SuspendCommand3
import com.bettafish.flarent.utils.SuspendCommand4
import com.mikepenz.markdown.model.Input
import com.mikepenz.markdown.model.MarkdownState
import com.mikepenz.markdown.model.State
import com.mikepenz.markdown.model.parseMarkdownFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PostItemViewModel(
    val id: String,
    initPost: Post? = null,
    private val repository: PostsRepository
) : ViewModel() {
    private val _post = MutableStateFlow(initPost)
    val post: StateFlow<Post?> = _post



    var parsedMarkdown: StateFlow<State> = MutableStateFlow(State.Loading())
        private set

    init {
        if (initPost == null) {
            load()
        }
        else{
            updatePost(initPost)
        }
    }

    private fun load() {
        viewModelScope.launch {
            try {
                val data = repository.fetchPosts(PostsRequest(listOf(id)))[0]
                updatePost(data)
            } catch (e: Exception) {
            }
        }
    }

    fun updatePost(updatedPost: Post) {
        if (updatedPost.contentHtml != null) {
            val markdown = HtmlConverter.convert(updatedPost.contentHtml)
            viewModelScope.launch {
                val state = parseMarkdownFlow(markdown).stateIn(viewModelScope, SharingStarted.Eagerly, State.Loading())
                parsedMarkdown = state
            }

        }
        _post.value = updatedPost
    }

    private suspend fun vote(postId: String, isUpvoted: Boolean, isDownvoted: Boolean, useVote: Boolean) {
        val data = if(useVote){
            repository.votePost(postId, isUpvoted, isDownvoted)
        }
        else{
            repository.likePost(postId, isUpvoted)
        }
        updatePost(data)
    }

    private suspend fun react(postId: String, reactionId: String) {
        val data = repository.reactPost(postId, reactionId)
        updatePost(data)
    }

    val reactCommand = SuspendCommand2(::react, viewModelScope)

    val voteCommand = SuspendCommand4(::vote, viewModelScope)
}