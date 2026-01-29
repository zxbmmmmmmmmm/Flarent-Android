package com.bettafish.flarent.network

import com.bettafish.flarent.models.Discussion
import com.bettafish.flarent.models.Tag
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FlarumService {
    // GET /api/discussions
    @GET("api/discussions")
    suspend fun getDiscussions(
        @Query("page[offset]") page: Int = 0,
        @Query("filter[tag]") tag: String? = null,
        @Query("include") include: String? = "user,lastPostedUser,tags"
    ): List<Discussion>

    // GET /api/discussions/{id}
    @GET("api/discussions/{id}")
    suspend fun getDiscussion(
        @Path("id") id: String,
        @Query("include") include: String? = "user,lastPostedUser,tags"
    ): Discussion

    @GET("api/tags")
    suspend fun getTags(
        @Query("include") include: String? = "children,lastPostedDiscussion,parent"
    ): List<Tag>
}
