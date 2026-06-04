package com.bettafish.flarent.ui.pages.like

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bettafish.flarent.data.UsersRepository
import com.bettafish.flarent.models.User
import kotlinx.coroutines.flow.Flow

class LikesViewModel(repository: UsersRepository, postId: String) : ViewModel() {
    companion object{
        private const val LOAD_COUNT = 20
    }
    val likers: Flow<PagingData<User>> = Pager(
        config = PagingConfig(pageSize = 20, enablePlaceholders = false),
        pagingSourceFactory = {
            LikesDataSource(
                repository,
                postId,
                LOAD_COUNT,
            )
        }
    ).flow.cachedIn(viewModelScope)
}