package com.bettafish.flarent.data

import com.bettafish.flarent.models.User
import com.bettafish.flarent.models.navigation.LoginResult
import com.bettafish.flarent.models.request.UsersRequest

interface UsersRepository {
    suspend fun fetchUser(id: String) : User
    suspend fun fetchUsers(request: UsersRequest) : List<User>

    suspend fun login(identification: String, password: String): LoginResult
}