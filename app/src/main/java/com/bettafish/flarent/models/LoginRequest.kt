package com.bettafish.flarent.models

import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val identification: String,
    val password: String,
    val remember: Boolean = true
)
