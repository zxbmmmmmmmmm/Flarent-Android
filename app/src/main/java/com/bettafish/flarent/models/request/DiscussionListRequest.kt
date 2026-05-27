package com.bettafish.flarent.models.request

data class DiscussionListRequest(
    val offset: Int? = 0,
    val tag: String? = null,
    val author: String? = null,
    val sort: String? = null,
    val include: String? = "user,lastPostedUser,tags",
    val limit: Int? = null
) {
    fun toQueryMap(): Map<String, String> = buildMap {
        offset?.let { put("page[offset]", it.toString()) }
        limit?.let { put("page[limit]", it.toString()) }
        tag?.let { put("filter[tag]", it) }
        author?.let { put("filter[author]", it) }
        sort?.let { put("sort", it) }
        include?.let { put("include", it) }
    }
}
