package com.bettafish.flarent.di

import com.bettafish.flarent.data.DiscussionsRepository
import com.bettafish.flarent.data.DiscussionsRepositoryImpl
import com.bettafish.flarent.data.PostsRepository
import com.bettafish.flarent.data.PostsRepositoryImpl
import com.bettafish.flarent.data.TagsRepository
import com.bettafish.flarent.data.TagsRepositoryImpl
import com.bettafish.flarent.models.Discussion
import com.bettafish.flarent.models.Post
import com.bettafish.flarent.models.Tag
import com.bettafish.flarent.models.User
import com.bettafish.flarent.models.navigation.TagNavArgs
import com.bettafish.flarent.network.FlarumService
import com.bettafish.flarent.viewModels.DiscussionDetailViewModel
import com.bettafish.flarent.viewModels.DiscussionsViewModel
import com.bettafish.flarent.viewModels.TagsViewModel
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.jasminb.jsonapi.retrofit.JSONAPIConverterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

val networkModule = module {
    single {
        val logging = HttpLoggingInterceptor().apply {
            level = if (com.bettafish.flarent.BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }
        OkHttpClient.Builder()
            .addInterceptor(logging)
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
        Retrofit.Builder()
            .baseUrl(com.bettafish.flarent.BuildConfig.FLARUM_BASE_URL)
            .addConverterFactory(
                JSONAPIConverterFactory(
                    objectMapper,
                    Discussion::class.java,
                    Post::class.java,
                    Tag::class.java,
                    User::class.java
                )
            )
            .build()
    }

    single { get<Retrofit>().create(FlarumService::class.java) }
}

val repositoryModule = module {
    single<DiscussionsRepository> { DiscussionsRepositoryImpl(get()) }
    single<TagsRepository> { TagsRepositoryImpl(get()) }
    single<PostsRepository> { PostsRepositoryImpl(get()) }
}

val viewModelModule = module {
    viewModel { (navArgs: TagNavArgs?) ->
        DiscussionsViewModel(get(), navArgs) }
    viewModel { TagsViewModel(get()) }
    viewModel { (id : String) -> DiscussionDetailViewModel(get(),get(),id) }
}
