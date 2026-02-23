package com.bettafish.flarent.ui.pages.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bettafish.flarent.App
import com.bettafish.flarent.data.ForumRepository
import com.bettafish.flarent.data.UsersRepository
import com.bettafish.flarent.firebaseAnalytics
import com.bettafish.flarent.models.Forum
import com.bettafish.flarent.utils.SuspendCommand1
import com.bettafish.flarent.utils.appSettings
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class WelcomeViewModel(val forumRepository: ForumRepository,
                       val usersRepository : UsersRepository
): ViewModel() {
    private val _forum: MutableStateFlow<Forum?> = MutableStateFlow(null)
    val forum: StateFlow<Forum?> = _forum

    init {
        getForum()
    }

    private fun getForum(){
        viewModelScope.launch {
            val forum = forumRepository.fetchForum()
            _forum.value = forum
        }
    }

    suspend fun refreshUser(id: String){
        try {
            val data = usersRepository.fetchUser(id)
            App.INSTANCE.appSettings.user = data
            firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN) {
                param("userId", data.id)
                data.username?.let { param("userName", it) }
                param(FirebaseAnalytics.Param.METHOD, "welcomePage")
            }
        } catch (e: Exception) {
        }
    }

    val refreshUserCommand = SuspendCommand1(::refreshUser, viewModelScope)

    fun saveForum(){
        App.INSTANCE.appSettings.forum = _forum.value
    }
}