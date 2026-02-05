package com.bettafish.flarent.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bettafish.flarent.data.PostsRepository
import com.bettafish.flarent.models.Post
import com.bettafish.flarent.utils.HtmlConverter
import com.bettafish.flarent.viewModels.DiscussionDetailViewModel.Companion.POST_PAGE_SIZE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.max

class PostViewModel(
    private val id: String,
    private val repository: PostsRepository): ViewModel() {
    private val _post = MutableStateFlow<Post?>(null)
    val post: StateFlow<Post?> = _post

    init {
        loadPost()
    }

    private fun loadPost() {
        viewModelScope.launch {
            try {
                val data = repository.fetchPosts(listOf(id))[0]
                if(data.contentHtml!=null){
                    data.contentMarkdown = HtmlConverter.convert(data.contentHtml)
                }
                _post.value = data
            } catch (e: Exception) {
            }
        }
    }
}