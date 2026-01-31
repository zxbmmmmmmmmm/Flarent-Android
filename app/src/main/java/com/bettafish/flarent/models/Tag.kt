package com.bettafish.flarent.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.Type
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
@Type("tags")
class Tag {
    @Id
    lateinit var id: String

    @JsonProperty("name")
    var name: String? = null

    @JsonProperty("icon")
    var icon: String? = null

    @JsonProperty("slug")
    var slug: String? = null

    @JsonProperty("description")
    var description: String? = null

    @JsonProperty("color")
    var color: String? = null

    @JsonProperty("lastPostedAt")
    @Contextual
    var lastPostedAt: ZonedDateTime? = null

    @JsonProperty("discussionCount")
    var discussionCount: Int? = null

    @Relationship("lastPostedDiscussion")
    var lastPostedDiscussion: Discussion? = null

    @Relationship("children")
    var children: List<Tag>? = null

    @JsonProperty("isChild")
    var isChild : Boolean? = null
}