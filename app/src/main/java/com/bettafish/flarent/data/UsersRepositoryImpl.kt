package com.bettafish.flarent.data

import com.bettafish.flarent.models.request.LoginRequest
import com.bettafish.flarent.models.navigation.LoginResult
import com.bettafish.flarent.network.FlarumService

class UsersRepositoryImpl(
    private val service: FlarumService
) : UsersRepository {

    override suspend fun fetchUser(id: String) = service.getUser(id)

    override suspend fun login(identification: String, password: String): LoginResult {
        val response = service.getToken(LoginRequest(identification, password))
        return LoginResult(token = response.token, id = response.userId)
    }
}