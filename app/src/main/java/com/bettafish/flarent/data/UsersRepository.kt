package com.bettafish.flarent.data

import com.bettafish.flarent.models.User
import com.bettafish.flarent.models.navigation.LoginResult

interface UsersRepository {
    suspend fun fetchUser(id: String) : User

    suspend fun login(identification: String, password: String): LoginResult
}