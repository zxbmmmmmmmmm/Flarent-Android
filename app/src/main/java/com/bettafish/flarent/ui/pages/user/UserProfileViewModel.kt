package com.bettafish.flarent.ui.pages.user

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
import com.bettafish.flarent.models.User
import com.bettafish.flarent.ui.pages.detail.PostsDataSource
import com.bettafish.flarent.ui.pages.discusison.DiscussionsDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UserProfileViewModel(
    val userName : String,
    val postsRepository: PostsRepository,
    val discussionsRepository: DiscussionsRepository
): ViewModel() {
    companion object{
        private const val LOAD_COUNT = 20;
    }
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    val discussions: Flow<PagingData<Discussion>> = Pager(
        config = PagingConfig(pageSize = LOAD_COUNT, enablePlaceholders = false),
        pagingSourceFactory = {
            DiscussionsDataSource(
                discussionsRepository,
                LOAD_COUNT,
                null,
                userName,
                "-createdAt"
            )
        }
    ).flow.cachedIn(viewModelScope)


    val posts: Flow<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = LOAD_COUNT, enablePlaceholders = false),
        pagingSourceFactory = {
            PostsDataSource(
                postsRepository,
                LOAD_COUNT,
                userName,
                onFirstLoad = { post ->
                    if (_user.value == null) {
                        _user.value = post.user
                    }
                }
            )
        }
    ).flow.cachedIn(viewModelScope)
}