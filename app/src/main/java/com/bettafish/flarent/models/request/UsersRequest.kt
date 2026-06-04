package com.bettafish.flarent.models.request

data class UsersRequest(
    val filter: Map<String,String>? = null,
    val limit: Int? = null,
    val offset: Int? = null,
) {
    fun toQueryMap(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        filter?.forEach { (k, v) -> map["filter[$k]"] = v }
        limit?.let { map["page[limit]"] = it.toString() }
        offset?.let { map["page[offset]"] = it.toString() }
        return map
    }
}