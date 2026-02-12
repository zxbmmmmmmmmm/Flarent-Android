package com.bettafish.flarent.models.request

data class DiscussionRequest(
    val id: String,
    val near: Int? = 0,
    val limit: Int? = 20
) {
    fun toQueryMap(): Map<String, String> = buildMap {
        put("id", id)
        near?.let { put("page[near]", it.toString()) }
        limit?.let { put("page[limit]", it.toString()) }
    }
}