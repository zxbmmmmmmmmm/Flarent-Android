package com.bettafish.flarent.models.request

data class NotificationRequest(
    val offset: Int? = 0,
    val limit: Int? = 20
) {
    fun toQueryMap(): Map<String, String> = buildMap {
        offset?.let { put("page[offset]", it.toString()) }
        limit?.let { put("page[limit]", it.toString()) }
    }
}