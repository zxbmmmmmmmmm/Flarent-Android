package com.bettafish.flarent.models.request

data class DiscussionListRequest(
    val offset: Int? = 0,
    val tag: String? = null,
    val author: String? = null,
    val sort: String? = null,
    val include: String? = "user,lastPostedUser,tags"
) {
    fun toQueryMap(): Map<String, String> = buildMap {
        offset?.let { put("page[offset]", it.toString()) }
        tag?.let { put("filter[tag]", it) }
        author?.let { put("filter[author]", it) }
        sort?.let { put("sort", it) }
        include?.let { put("include", it) }
    }
}