package com.bettafish.flarent.ui.pages.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bettafish.flarent.App
import com.bettafish.flarent.data.UsersRepository
import com.bettafish.flarent.firebaseAnalytics
import com.bettafish.flarent.models.User
import com.bettafish.flarent.utils.appSettings
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AccountViewModel(
    val usersRepository : UsersRepository
): ViewModel() {
    private val _user = MutableStateFlow(App.Companion.INSTANCE.appSettings.user)
    val user: StateFlow<User?> = _user.asStateFlow()

    fun refreshUser(id: String, isLogin: Boolean = false){
        viewModelScope.launch {
            try {
                val data = usersRepository.fetchUser(id)
                _user.value = data
                App.Companion.INSTANCE.appSettings.user = data
                if(isLogin){
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN) {
                        param("userId", data.id)
                        data.username?.let { param("userName", it) }
                        param(FirebaseAnalytics.Param.METHOD, "accountPage")
                    }
                }
            } catch (e: Exception) {
            }
        }
    }

    fun logout(){
        _user.value = null
        App.Companion.INSTANCE.appSettings.user = null
        App.Companion.INSTANCE.appSettings.token = null
        App.Companion.INSTANCE.appSettings.userId = null
    }
}