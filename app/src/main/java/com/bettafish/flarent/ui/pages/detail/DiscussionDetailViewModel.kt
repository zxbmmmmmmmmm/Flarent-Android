package com.bettafish.flarent.ui.pages.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bettafish.flarent.App
import com.bettafish.flarent.data.DiscussionsRepository
import com.bettafish.flarent.data.PostsRepository
import com.bettafish.flarent.firebaseAnalytics
import com.bettafish.flarent.models.Discussion
import com.bettafish.flarent.models.Post
import com.bettafish.flarent.models.request.DiscussionRequest
import com.bettafish.flarent.utils.Analytics
import com.bettafish.flarent.utils.SuspendCommand
import com.bettafish.flarent.utils.appSettings
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

data class ScrollTarget(val index: Int, val version: Int)

class DiscussionDetailViewModel(
    private val postsRepository: PostsRepository,
    private val discussionsRepository: DiscussionsRepository,
    val discussionId: String,
    private var targetPosition: Int = 0
) : ViewModel() {
    companion object{
        const val POST_PAGE_SIZE = 20
    }
    private val _discussion = MutableStateFlow<Discussion?>(null)
    val discussion: StateFlow<Discussion?> = _discussion.asStateFlow()


    private val _scrollTarget = MutableStateFlow<Int?>(null)
    val scrollTarget: StateFlow<Int?> = _scrollTarget.asStateFlow()

    var currentPagingSource : DiscussionDetailPostListDataSource? = null

    val loadDiscussionCommand = SuspendCommand(::loadDiscussion, viewModelScope)

    init {
        loadDiscussionCommand.execute()
    }

    private suspend fun loadDiscussion() {
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
            firebaseAnalytics.logEvent(Analytics.Event.VIEW_DISCUSSION) {
                result.title?.let{ param(Analytics.Param.DISCUSSION_TITLE, it )}
                param(FirebaseAnalytics.Param.METHOD, "welcomePage")
            }
        } catch (e: Exception) {
        }
    }



    fun jumpToPosition(position: Int) {
        targetPosition = position
        _scrollTarget.value = position
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    var posts: Flow<PagingData<Post>> = _discussion
        .filterNotNull()
        .filter { !it.posts.isNullOrEmpty() }
        .flatMapLatest { discussion ->
            val posts = discussion.posts!!
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

            _scrollTarget.value = targetIndex

            Pager(
                config = PagingConfig(
                    pageSize = POST_PAGE_SIZE,
                    enablePlaceholders = true,
                    initialLoadSize = POST_PAGE_SIZE,
                    prefetchDistance = 2,
                    jumpThreshold = POST_PAGE_SIZE
                ),
                initialKey = startKey,
                pagingSourceFactory = {
                    DiscussionDetailPostListDataSource(
                        postsRepository = postsRepository,
                        posts = discussion.posts!!
                    ).also { currentPagingSource = it }
                }
            ).flow
        }
        .cachedIn(viewModelScope)

}