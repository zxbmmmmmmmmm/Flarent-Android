package com.bettafish.flarent.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Type
import kotlinx.serialization.Serializable

@Serializable
@Type("groups")
class Group {
    @Id
    lateinit var id: String

    @JsonProperty("nameSingular")
    var nameSingular: String? = null

    @JsonProperty("namePlural")
    var namePlural: String? = null

    @JsonProperty("color")
    var color: String? = null

    @JsonProperty("icon")
    var icon: String? = null

    @JsonProperty("isHidden")
    var isHidden: Int? = null
}