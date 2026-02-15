package com.bettafish.flarent.ui.pages.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.bettafish.flarent.data.DiscussionsRepository
import com.bettafish.flarent.data.PostsRepository
import com.bettafish.flarent.models.Discussion
import com.bettafish.flarent.models.Post
import com.bettafish.flarent.models.request.DiscussionRequest
import com.bettafish.flarent.utils.HtmlConverter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class DiscussionDetailViewModel(
    private val postsRepository: PostsRepository,
    private val discussionsRepository: DiscussionsRepository,
    val discussionId: String,
    val targetPosition: Int = 0
) : ViewModel() {
    companion object{
        const val POST_PAGE_SIZE = 20
    }
    private val _discussion = MutableStateFlow<Discussion?>(null)
    val discussion: StateFlow<Discussion?> = _discussion.asStateFlow()

    private val _initialScrollIndex = MutableStateFlow<Int?>(null)
    val initialScrollIndex = _initialScrollIndex.asStateFlow()

    init {
        loadDiscussion()
    }

    private fun loadDiscussion() {
        viewModelScope.launch {
            try {
                val fetchPos = max(0, targetPosition)
                val result = discussionsRepository.fetchDiscussion(
                    DiscussionRequest(
                        discussionId,
                        fetchPos,
                        POST_PAGE_SIZE
                    )
                )
                _discussion.value = result
            } catch (e: Exception) {
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    var posts: Flow<PagingData<Post>> = _discussion
        .filterNotNull()
        .filter { !it.posts.isNullOrEmpty() }
        .flatMapLatest { discussion ->
            val posts = discussion.posts!!
            // Find the index of the target post (startingPosition)
            // Because post.number varies and entries might be deleted, we can't assume index == number.
            // We search for the exact number, or the closest number among loaded posts.
            val targetIndex = posts.indexOfFirst { it.number == targetPosition }
                .takeIf { it != -1 }
                ?: posts.filter { it.number != null }
                    .minByOrNull { abs(it.number!! - targetPosition) }
                    ?.let { posts.indexOf(it) }
                ?: max(0, min(posts.size, targetPosition))

            var startKey = targetIndex
            while (startKey > 0 && posts[startKey - 1].createdAt != null) {
                startKey--
            }

            _initialScrollIndex.value = targetIndex - startKey

            Pager(
                config = PagingConfig(
                    pageSize = POST_PAGE_SIZE,
                    enablePlaceholders = false,
                    initialLoadSize = POST_PAGE_SIZE,
                    prefetchDistance = 1,
                ),
                initialKey = startKey,
                pagingSourceFactory = {
                    DiscussionDetailPostListDataSource(
                        postsRepository = postsRepository,
                        posts = discussion.posts!!
                    )
                }
            ).flow
        }
        .cachedIn(viewModelScope)

    val modifiedItems = MutableStateFlow<Map<String, Post>>(emptyMap())

    val combinedPosts = combine(posts, modifiedItems) { pagingData, modifications ->
        pagingData.map { user ->
            modifications[user.id] ?: user // 如果有修改记录就用修改后的，否则用原始的
        }
    }.cachedIn(viewModelScope)
}