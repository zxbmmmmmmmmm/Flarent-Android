package com.bettafish.flarent.models

import com.bettafish.flarent.utils.ZonedDateTimeSerializer
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.Type
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Type("users")
@Serializable
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

    @JsonProperty("followerCount")
    var followerCount: Int? = null

    @JsonProperty("followingCount")
    var followingCount: Int? = null

    @JsonProperty("discussionCount")
    var discussionCount: Int? = null

    @JsonProperty("commentCount")
    var commentCount: Int? = null

    @JsonProperty("points")
    var points: Int? = null

    @JsonProperty("joinTime")
    @Contextual
    @Serializable(with = ZonedDateTimeSerializer::class)
    var joinTime: ZonedDateTime? = null

    @JsonProperty("lastSeenAt")
    @Contextual
    @Serializable(with = ZonedDateTimeSerializer::class)
    var lastSeenAt: ZonedDateTime? = null

    @JsonProperty("bio")
    var bio: String? = null

    @Relationship("groups")
    var groups: List<Group>? = null
}