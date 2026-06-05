package com.bettafish.flarent.models.request

import kotlin.collections.set

data class DiscussionListRequest(
    val offset: Int? = 0,
    val sort: String? = null,
    val filter: Map<String,String>? = null,
    val include: List<String>? = listOf("user","lastPostedUser","tags")
) {
    fun toQueryMap(): Map<String, String> = buildMap {
        offset?.let { put("page[offset]", it.toString()) }
        filter?.forEach { (k, v) -> put("filter[$k]", v) }
        sort?.let { put("sort", it) }
        include?.let { put("include", it.joinToString(",")) }
    }
}