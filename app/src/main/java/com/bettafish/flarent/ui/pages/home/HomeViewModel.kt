package com.bettafish.flarent.ui.pages.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bettafish.flarent.App
import com.bettafish.flarent.data.ForumRepository
import com.bettafish.flarent.utils.appSettings
import kotlinx.coroutines.launch

class HomeViewModel(val forumRepository: ForumRepository): ViewModel() {
    init{
        updateUser()
    }
    fun updateUser(){
        viewModelScope.launch {
            val forum = forumRepository.fetchForum()
            App.INSTANCE.appSettings.forum = forum
            App.INSTANCE.appSettings.user = forum.actor
        }
    }
}