package com.bettafish.flarent.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Relationship
import com.github.jasminb.jsonapi.annotations.Type
import kotlinx.serialization.Serializable

@Serializable
@Type("forums")
class Forum {
    @Id
    lateinit var id: String

    @JsonProperty("title")
    var title: String? = null

    @JsonProperty("description")
    var description: String? = null

    @JsonProperty("baseUrl")
    var baseUrl: String? = null

    @JsonProperty("guidelinesUrl")
    var guidelinesUrl: String? = null

    @JsonProperty("welcomeTitle")
    var welcomeTitle: String? = null

    @JsonProperty("welcomeMessage")
    var welcomeMessage: String? = null

    @JsonProperty("themePrimaryColor")
    var themePrimaryColor: String? = null

    @JsonProperty("themeSecondaryColor")
    var themeSecondaryColor: String? = null

    @JsonProperty("logoUrl")
    var logoUrl: String? = null

    @JsonProperty("faviconUrl")
    var faviconUrl: String? = null

    @JsonProperty("minPrimaryTags")
    var minPrimaryTags: Int? = null

    @JsonProperty("maxPrimaryTags")
    var maxPrimaryTags: Int? = null

    @JsonProperty("minSecondaryTags")
    var minSecondaryTags: Int? = null

    @JsonProperty("maxSecondaryTags")
    var maxSecondaryTags: Int? = null

    @Relationship("actor")
    var actor: User? = null

    @Relationship("reactions")
    var reactions: List<Tag>? = null
}