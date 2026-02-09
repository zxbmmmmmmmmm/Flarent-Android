package com.bettafish.flarent.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.jasminb.jsonapi.annotations.Id
import com.github.jasminb.jsonapi.annotations.Type
import kotlinx.serialization.Serializable

@Serializable
@Type("files")
class File {
    @Id
    lateinit var id: String

    @JsonProperty("baseName")
    var baseName: String? = null

    @JsonProperty("path")
    var path: String? = null

    @JsonProperty("url")
    var url: String? = null

    @JsonProperty("type")
    var fileType: String? = null

    @JsonProperty("size")
    var size: Long? = null

    @JsonProperty("humanSize")
    var humanSize: String? = null

    @JsonProperty("createdAt")
    var createdAt: String? = null

    @JsonProperty("uuid")
    var uuid: String? = null

    @JsonProperty("tag")
    var tag: String? = null

    @JsonProperty("hidden")
    var isHidden: Boolean? = null

    @JsonProperty("bbcode")
    var bbcode: String? = null

    @JsonProperty("shared")
    var isShared: Boolean? = null

    @JsonProperty("canViewInfo")
    var canViewInfo: Boolean? = null

    @JsonProperty("canHide")
    var canHide: Boolean? = null

    @JsonProperty("canDelete")
    var canDelete: Boolean? = null
}
