package com.bettafish.flarent.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Type
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
@Type("posts")
class Post
{
    @Id
    var id: String? = null

    @JsonProperty("contentType")
    var contentType: String? = null

    @JsonProperty("number")
    var number: Int? = null

    @JsonProperty("createdAt")
    @Contextual
    var createdAt: ZonedDateTime? = null
}
