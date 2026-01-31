package com.bettafish.flarent.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bettafish.flarent.data.DiscussionsRepository
import com.bettafish.flarent.data.PostsRepository
import com.bettafish.flarent.models.Discussion
import com.bettafish.flarent.models.Post
import com.bettafish.flarent.models.navigation.TagNavArgs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

const val POST_PAGE_SIZE = 20

class DiscussionDetailViewModel(
    private val postsRepository: PostsRepository,
    private val discussionsRepository: DiscussionsRepository,
    val discussionId: String
) : ViewModel() {
    private val _discussion = MutableStateFlow<Discussion?>(null)
    val discussion: StateFlow<Discussion?> = _discussion.asStateFlow()
    val startingPosition = 0

    init {
        loadDiscussion()
    }

    private fun loadDiscussion() {
        viewModelScope.launch {
            try {
                val result = discussionsRepository.fetchDiscussionById(discussionId, startingPosition ,POST_PAGE_SIZE)
                _discussion.value = result
            } catch (e: Exception) {
            }
        }
    }

    @ExperimentalCoroutinesApi
    val posts: Flow<PagingData<Post>> = _discussion
        .filterNotNull()
        .filter { !it.posts.isNullOrEmpty() }
        .flatMapLatest { discussion ->
            Pager(
                config = PagingConfig(pageSize = POST_PAGE_SIZE, enablePlaceholders = false),
                pagingSourceFactory = {
                    PostsDataSource(
                        postsRepository = postsRepository,
                        posts = discussion.posts!!,
                        startingPosition = startingPosition,
                        pageSize = POST_PAGE_SIZE
                    )
                }
            ).flow
        }
        .cachedIn(viewModelScope)
}