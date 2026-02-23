package com.bettafish.flarent.models

import com.fasterxml.jackson.annotation.JsonProperty
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

    internal var contentMarkdown: String? = null

    @JsonProperty("contentHtml")
    val contentHtml: String? = null

    @JsonProperty("number")
    var number: Int? = null

    @JsonProperty("votes")
    var votes: Int? = null

    @JsonProperty("hasUpvoted")
    var hasUpvoted: Boolean? = null

    @JsonProperty("hasDownvoted")
    var hasDownvoted: Boolean? = null

    @JsonProperty("canVote")
    var canVote: Boolean? = null

    @JsonProperty("seeVoters")
    var seeVoters: Boolean? = null

    @JsonProperty("canEdit")
    var canEdit: Boolean? = null

    @JsonProperty("canReact")
    var canReact: Boolean? = null

    @JsonProperty("reactionCounts")
    var reactionCounts: Map<String, Int>? = null

    @JsonProperty("userReactionIdentifier")
    var userReactionIdentifier: String? = null

    @JsonProperty("createdAt")
    @Contextual
    var createdAt: ZonedDateTime? = null

    @JsonProperty("editedAt")
    @Contextual
    var editedAt: ZonedDateTime? = null

    @JsonProperty("isHidden")
    var isHidden: Boolean? = null

    @Relationship("upvotes")
    var upvotes: List<User>? = null

    @Relationship("downvotes")
    var downvotes: List<User>? = null

    @Relationship("user")
    var user: User? = null

    @Relationship("discussion")
    var discussion: Discussion? = null
}
