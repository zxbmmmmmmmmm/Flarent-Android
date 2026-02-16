package com.bettafish.flarent.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.jasminb.jsonapi.annotations.Type
import kotlinx.serialization.Serializable

@Serializable
@Type("reactions")
class Reaction {
    @JsonProperty("identifier")
    val identifier:String? = null

    @JsonProperty("display")
    val display: String? = null

    @JsonProperty("type")
    val type: String? = null

    @JsonProperty("enabled")
    val enabled: Boolean? = null
}