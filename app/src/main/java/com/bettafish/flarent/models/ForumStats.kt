package com.bettafish.flarent.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Type
import kotlinx.serialization.Serializable

@Serializable
data class ForumStats (
    @param:JsonProperty("discussionCount")
    val discussionCount: StatsItem,
    @param:JsonProperty("userCount")
    val userCount: StatsItem,
    @param:JsonProperty("commentPostCount")
    val commentPostCount: StatsItem,
)
@Serializable
data class StatsItem (
    @param:JsonProperty("label")
    val label: String,
    @param:JsonProperty("icon")
    val icon: String?,
    @param:JsonProperty("value")
    val value: Int,
    @param:JsonProperty("prettyValue")
    val prettyValue: String
)