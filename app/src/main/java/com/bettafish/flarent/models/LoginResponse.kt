package com.bettafish.flarent.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import kotlinx.serialization.Serializable

@Serializable
@JsonSerialize
class LoginResponse{
    @JsonProperty("token")
    lateinit var token: String
    @JsonProperty("userId")
    lateinit var userId: String
}