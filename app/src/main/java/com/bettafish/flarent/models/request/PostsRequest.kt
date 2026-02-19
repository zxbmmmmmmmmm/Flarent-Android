package com.bettafish.flarent.models.request

data class PostsRequest(
    val ids: List<String>? = null,
    val author: String? = null,
    val type: String? = null,
    val limit: Int? = null,
    val offset: Int? = null,
    val sort: String? = null,
    val include: List<String>? = null
) {
    fun toQueryMap(): Map<String, String> {
        val map = mutableMapOf<String, String>()
        ids?.let { map["filter[id]"] = it.joinToString(",") }
        author?.let { map["filter[author]"] = it }
        type?.let { map["filter[type]"] = it }
        limit?.let { map["page[limit]"] = it.toString() }
        offset?.let { map["page[offset]"] = it.toString() }
        sort?.let { map["sort"] = it }
        include?.let { map["include"] = it.joinToString(",") }
        return map
    }
}