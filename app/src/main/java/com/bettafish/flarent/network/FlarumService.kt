package com.bettafish.flarent.network

import com.bettafish.flarent.models.Discussion
import com.bettafish.flarent.models.File
import com.bettafish.flarent.models.LoginRequest
import com.bettafish.flarent.models.LoginResponse
import com.bettafish.flarent.models.Post
import com.bettafish.flarent.models.Tag
import com.bettafish.flarent.models.User
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface FlarumService {
    // GET /api/discussions
    @GET("api/discussions")
    suspend fun getDiscussionList(
        @QueryMap options: Map<String, String>
    ): List<Discussion>

    // GET /api/discussions/{id}
    @GET("api/discussions/{id}")
    suspend fun getDiscussion(
        @Path("id") id: String,
        @QueryMap options: Map<String, String>
    ): Discussion

    @GET("api/tags")
    suspend fun getTags(
        @Query("include") include: String? = "children,lastPostedDiscussion,parent"
    ): List<Tag>

    @GET("api/posts")
    suspend fun getPosts(
        @QueryMap options: Map<String, String>
        ):List<Post>

    @GET("api/users/{id}")
    suspend fun getUser(
        @Path("id") id: String
    ): User

    @POST("api/token")
    suspend fun getToken(
        @Body body: LoginRequest
    ): LoginResponse

    @POST("api/posts")
    suspend fun sendPost(
        @Body post: Post
    ) : Post

    @Multipart
    @POST("api/fof/upload")
    suspend fun uploadFile(
        @Part file: MultipartBody.Part
    ): List<File>
}