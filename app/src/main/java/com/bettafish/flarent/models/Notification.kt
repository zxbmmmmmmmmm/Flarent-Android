package com.bettafish.flarent.models

import com.bettafish.flarent.utils.ZonedDateTimeSerializer
import com.fasterxml.jackson.annotation.JsonProperty
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.Type
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.time.ZonedDateTime

@Serializable
@Type("notifications")
class Notification{
    @Id
    lateinit var id: String

    @JsonProperty("contentType")
    var contentType: String? = null

    @JsonProperty("content")
    @Contextual
    var content: Any? = null

    @JsonProperty("isRead")
    var isRead: Boolean? = null

    @JsonProperty("createdAt")
    @Contextual
    @Serializable(with = ZonedDateTimeSerializer::class)
    var createdAt: ZonedDateTime? = null

    @Relationship("fromUser")
    var fromUser: User? = null

    @Relationship("subject")
    @Contextual
    var subject: Section? = null
}
