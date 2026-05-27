package com.bettafish.flarent.data

import com.bettafish.flarent.models.request.DiscussionListRequest

fun DiscussionListRequest.cacheKey(): String =
    listOf(
        "tag=${tag.orEmpty()}",
        "author=${author.orEmpty()}",
        "sort=${sort.orEmpty()}",
        "include=${include.orEmpty()}"
    ).joinToString("&")
