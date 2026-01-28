package com.bettafish.flarent.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Type
import java.time.ZonedDateTime

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
    var createdAt: ZonedDateTime? = null
}
