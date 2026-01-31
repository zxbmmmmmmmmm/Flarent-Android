package com.bettafish.flarent.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.Type
import kotlinx.serialization.Serializable

@Serializable
@Type("users")
class User {
    @Id
    lateinit var id: String

    @JsonProperty("username")
    var username: String? = null

    @JsonProperty("displayName")
    var displayName: String? = null

    @JsonProperty("avatarUrl")
    var avatarUrl: String? = null

    @JsonProperty("slug")
    var slug: String? = null

    @Relationship("groups")
    var groups: List<Group>? = null
}