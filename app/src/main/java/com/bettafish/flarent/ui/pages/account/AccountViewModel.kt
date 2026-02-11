package com.bettafish.flarent.ui.pages.account

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bettafish.flarent.App
import com.bettafish.flarent.data.UsersRepository
import com.bettafish.flarent.models.User
import com.bettafish.flarent.utils.appSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AccountViewModel(
    val usersRepository : UsersRepository
): ViewModel() {
    private val _user = MutableStateFlow(App.Companion.INSTANCE.appSettings.user)
    val user: StateFlow<User?> = _user.asStateFlow()

    fun refreshUser(id: String){
        viewModelScope.launch {
            try {
                val data = usersRepository.fetchUser(id)
                _user.value = data
                App.Companion.INSTANCE.appSettings.user = data
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