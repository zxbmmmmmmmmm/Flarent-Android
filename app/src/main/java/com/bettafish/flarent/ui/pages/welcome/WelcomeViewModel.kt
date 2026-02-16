package com.bettafish.flarent.ui.pages.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bettafish.flarent.App
import com.bettafish.flarent.data.ForumRepository
import com.bettafish.flarent.models.Forum
import com.bettafish.flarent.utils.appSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WelcomeViewModel(val repository: ForumRepository): ViewModel() {
    private val _forum: MutableStateFlow<Forum?> = MutableStateFlow(null)
    val forum: StateFlow<Forum?> = _forum

    init {
        getForum()
    }

    private fun getForum(){
        viewModelScope.launch {
            val forum = repository.fetchForum()
            _forum.value = forum
        }
    }

    fun saveForum(){
        App.INSTANCE.appSettings.forum = _forum.value
    }
}