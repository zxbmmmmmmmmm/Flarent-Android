package com.bettafish.flarent.ui.pages.account.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bettafish.flarent.data.UsersRepository
import com.bettafish.flarent.models.navigation.LoginResult
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: UsersRepository
): ViewModel() {
    var isLoading by mutableStateOf(false)
        private set

    var errorMsg by mutableStateOf<String?>(null)
        private set

    private val _loginSuccessEvent = Channel<LoginResult>()
    val loginSuccessEvent = _loginSuccessEvent.receiveAsFlow()

    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            errorMsg = "用户名或密码不能为空"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMsg = null
            try {
                val result = repository.login(username, password)
                _loginSuccessEvent.send(result)
            } catch (e: Exception) {
                e.printStackTrace()
                errorMsg = "登录失败: ${e.message ?: "请检查网络或密码"}"
            } finally {
                isLoading = false
            }
        }
    }
}