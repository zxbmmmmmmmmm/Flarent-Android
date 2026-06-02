package com.bettafish.flarent.ui.pages.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bettafish.flarent.data.DiscussionsRepository
import com.bettafish.flarent.data.PostsRepository
import com.bettafish.flarent.models.request.PostsRequest
import kotlinx.coroutines.launch

class PostBottomSheetViewModel(
    private val postRepository: PostsRepository
) : ViewModel() {
    suspend fun getPostId(discussionId: String, postNumber: String): String? {
        return try {
            val posts = postRepository.fetchPosts(
                PostsRequest(
                    discussionId = discussionId,
                    number = postNumber
                )
            )
            posts.firstOrNull()?.id
        } catch (e: Exception) {
            null
        }
    }
}