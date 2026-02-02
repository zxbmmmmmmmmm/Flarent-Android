package com.bettafish.flarent.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonSetter
import com.fasterxml.jackson.databind.JsonNode
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.Type
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime


@Serializable
@Type("posts")
class Post
{
    @Id
    lateinit var id: String

    @JsonProperty("contentType")
    var contentType: String? = null

    @JsonProperty("content")
    @Contextual
    var content: Any? = null

    @JsonProperty("contentHtml")
    val contentHtml: String? = null

    @JsonProperty("number")
    var number: Int? = null

    @JsonProperty("createdAt")
    @Contextual
    var createdAt: ZonedDateTime? = null

    @Relationship("user")
    var user: User? = null

    @Relationship("discussion")
    var discussion: Discussion? = null
}
