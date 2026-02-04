package com.bettafish.flarent.di

import com.bettafish.flarent.App
import com.bettafish.flarent.data.DiscussionsRepository
import com.bettafish.flarent.data.DiscussionsRepositoryImpl
import com.bettafish.flarent.data.PostsRepository
import com.bettafish.flarent.data.PostsRepositoryImpl
import com.bettafish.flarent.data.TagsRepository
import com.bettafish.flarent.data.TagsRepositoryImpl
import com.bettafish.flarent.data.UsersRepository
import com.bettafish.flarent.data.UsersRepositoryImpl
import com.bettafish.flarent.models.Discussion
import com.bettafish.flarent.models.LoginRequest
import com.bettafish.flarent.models.LoginResponse
import com.bettafish.flarent.models.Post
import com.bettafish.flarent.models.Tag
import com.bettafish.flarent.models.User
import com.bettafish.flarent.models.navigation.LoginResult
import com.bettafish.flarent.models.navigation.TagNavArgs
import com.bettafish.flarent.network.FlarumService
import com.bettafish.flarent.utils.appSettings
import com.bettafish.flarent.viewModels.AccountViewModel
import com.bettafish.flarent.viewModels.DiscussionDetailViewModel
import com.bettafish.flarent.viewModels.DiscussionsViewModel
import com.bettafish.flarent.viewModels.LoginViewModel
import com.bettafish.flarent.viewModels.TagsViewModel
import com.bettafish.flarent.viewModels.UserProfileViewModel
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.jasminb.jsonapi.retrofit.JSONAPIConverterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit

val networkModule = module {
    single {
        val logging = HttpLoggingInterceptor().apply {
            level = if (com.bettafish.flarent.BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(AuthInterceptor())
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    single {
        jacksonObjectMapper().apply {
            // register modules if needed
        }
    }

    single {
        val objectMapper = ObjectMapper()
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        objectMapper.registerModule(JavaTimeModule())
        val resourceConverter = com.github.jasminb.jsonapi.ResourceConverter(
            objectMapper,
            Discussion::class.java,
            Post::class.java,
            Tag::class.java,
            User::class.java
        )
        resourceConverter.enableDeserializationOption(com.github.jasminb.jsonapi.DeserializationFeature.ALLOW_UNKNOWN_TYPE_IN_RELATIONSHIP)
        resourceConverter.enableDeserializationOption(com.github.jasminb.jsonapi.DeserializationFeature.ALLOW_UNKNOWN_INCLUSIONS)
        resourceConverter.disableDeserializationOption(com.github.jasminb.jsonapi.DeserializationFeature.REQUIRE_RESOURCE_ID)
        Retrofit.Builder()
            .client(get())
            .baseUrl(com.bettafish.flarent.BuildConfig.FLARUM_BASE_URL)
            .addConverterFactory(
                JSONAPIConverterFactory(resourceConverter)
            )
            .addConverterFactory(
                JacksonConverterFactory.create()
            )
            .build()
    }

    single { get<Retrofit>().create(FlarumService::class.java) }
}

val repositoryModule = module {
    single<DiscussionsRepository> { DiscussionsRepositoryImpl(get()) }
    single<TagsRepository> { TagsRepositoryImpl(get()) }
    single<PostsRepository> { PostsRepositoryImpl(get()) }
    single<UsersRepository> { UsersRepositoryImpl(get()) }
}

val viewModelModule = module {
    viewModel { (navArgs: TagNavArgs?) ->
        DiscussionsViewModel(get(), navArgs) }
    viewModel { TagsViewModel(get()) }
    viewModel { AccountViewModel(get()) }
    viewModel { (id : String, targetPosition:Int) -> DiscussionDetailViewModel(get(),get(),id, targetPosition) }
    viewModel { (userName : String) -> UserProfileViewModel(userName, get(), get()) }
    viewModel { LoginViewModel(get()) }
}

class AuthInterceptor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val token = App.INSTANCE.appSettings.token

        val requestWithToken = token?.let {
            originalRequest.newBuilder()
                .header("Authorization", "Token $it").build()
        }  ?: originalRequest

        return chain.proceed(requestWithToken)
    }
}
