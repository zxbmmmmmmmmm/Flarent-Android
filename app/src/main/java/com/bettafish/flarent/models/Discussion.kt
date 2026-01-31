package com.bettafish.flarent.models

import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Type
import com.github.jasminb.jsonapi.annotations.Relationship
import com.fasterxml.jackson.annotation.JsonProperty;
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
@Type("discussions")
class Discussion {
    @Id
    lateinit var id: String

    @JsonProperty("title")
    var title: String? = null

    @JsonProperty("slug")
    var slug: String? = null

    @JsonProperty("commentCount")
    var commentCount: Int? = null

    @JsonProperty("participantCount")
    var participantCount: Int? = null

    @JsonProperty("createdAt")
    @Contextual
    var createdAt: ZonedDateTime? = null

    @JsonProperty("lastPostedAt")
    @Contextual
    var lastPostedAt: ZonedDateTime? = null

    @Relationship("user")
    var user: User? = null

    @Relationship("lastPostedUser")
    var lastPostedUser: User? = null

    @Relationship("tags")
    var tags: List<Tag>? = null

    @Relationship("posts")
    var posts: List<Post>? = null
}