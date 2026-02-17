package com.bettafish.flarent.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Type
import kotlinx.serialization.Serializable

@Serializable
@Type("reactions")
class Reaction {
    @Id
    lateinit var id: String

    @JsonProperty("identifier")
    var identifier: String? = null

    @JsonProperty("display")
    var display: String? = null

    @JsonProperty("type")
    var type: String? = null

    @JsonProperty("enabled")
    var enabled: Boolean? = null
}