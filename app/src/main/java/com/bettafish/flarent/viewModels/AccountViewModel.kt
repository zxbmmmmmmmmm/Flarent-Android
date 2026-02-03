package com.bettafish.flarent.viewModels

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
    private val _user = MutableStateFlow(App.INSTANCE.appSettings.user)
    val user: StateFlow<User?> = _user.asStateFlow()

    fun refreshUser(id: String){
        viewModelScope.launch {
            try {
                val data = usersRepository.fetchUser(id)
                _user.value = data
                App.INSTANCE.appSettings.user = data
            } catch (e: Exception) {
            }
        }
    }
}