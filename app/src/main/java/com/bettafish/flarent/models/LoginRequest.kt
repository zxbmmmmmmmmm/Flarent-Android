package com.bettafish.flarent.models

import com.github.jasminb.jsonapi.annotations.Type
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    val identification: String,
    val password: String,
    val remember: Boolean = true
)
