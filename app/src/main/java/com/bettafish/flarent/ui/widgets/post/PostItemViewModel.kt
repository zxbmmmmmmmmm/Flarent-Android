package com.bettafish.flarent.ui.widgets.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bettafish.flarent.data.PostsRepository
import com.bettafish.flarent.models.Post
import com.bettafish.flarent.models.request.PostsRequest
import com.bettafish.flarent.utils.HtmlConverter
import com.bettafish.flarent.utils.SuspendCommand3
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PostItemViewModel(
    private val id: String,
    initPost: Post? = null,
    private val repository: PostsRepository
): ViewModel() {
    private val _post = MutableStateFlow(initPost)
    val post: StateFlow<Post?> = _post

    init {
        if(initPost == null){
            load()
        }
    }

    private fun load() {
        viewModelScope.launch {
            try {
                val data = repository.fetchPosts(PostsRequest(listOf(id)))[0]
                if(data.contentHtml!=null){
                    data.contentMarkdown = HtmlConverter.convert(data.contentHtml)
                }
                _post.value = data
            } catch (e: Exception) {
            }
        }
    }

    private suspend fun vote(postId: String, isUpvoted: Boolean, isDownvoted: Boolean) {
        val data = repository.votePost(postId, isUpvoted, isDownvoted)
        if(data.contentHtml!=null){
            data.contentMarkdown = HtmlConverter.convert(data.contentHtml)
        }
        _post.value = data
    }

    val voteCommand = SuspendCommand3(::vote, viewModelScope)
}