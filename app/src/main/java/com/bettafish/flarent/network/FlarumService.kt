package com.bettafish.flarent.network

import com.bettafish.flarent.models.Discussion
import com.bettafish.flarent.models.LoginRequest
import com.bettafish.flarent.models.LoginResponse
import com.bettafish.flarent.models.Post
import com.bettafish.flarent.models.Tag
import com.bettafish.flarent.models.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FlarumService {
    // GET /api/discussions
    @GET("api/discussions")
    suspend fun getDiscussions(
        @Query("page[offset]") offset: Int = 0,
        @Query("filter[tag]") tag: String? = null,
        @Query("filter[author]") author: String? = null,
        @Query("sort") string: String? = null,
        @Query("include") include: String? = "user,lastPostedUser,tags"
    ): List<Discussion>

    // GET /api/discussions/{id}
    @GET("api/discussions/{id}")
    suspend fun getDiscussion(
        @Path("id") id: String,
        @Query("page[near]") near:Int = 0,
        @Query("page[limit]") limit:Int = 20,
    ): Discussion

    @GET("api/tags")
    suspend fun getTags(
        @Query("include") include: String? = "children,lastPostedDiscussion,parent"
    ): List<Tag>

    @GET("api/posts")
    suspend fun getPosts (
        @Query("filter[id][]") id: List<String>? = null,
        @Query("filter[author]") author:String? = null,
        @Query("filter[type]") type: String? = null,
        @Query("page[limit]") limit:Int? = null,
        @Query("page[offset]") offset: Int? = null,
        @Query("sort") string: String? = null,
        ):List<Post>

    @GET("api/users/{id}")
    suspend fun getUser(
        @Path("id") id: String
    ): User

    @POST("api/token")
    suspend fun getToken(
        @Body body: LoginRequest
    ): LoginResponse
}