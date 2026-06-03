package com.bettafish.flarent.utils

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.platform.UriHandler
import com.bettafish.flarent.config.ForumConfig
import com.ramcosta.composedestinations.generated.destinations.DiscussionDetailPageDestination
import com.ramcosta.composedestinations.generated.destinations.PostBottomSheetDestination
import com.ramcosta.composedestinations.generated.destinations.UserProfilePageDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import okhttp3.HttpUrl.Companion.toHttpUrl

class AppUriHandler(
    val navigator: DestinationsNavigator,
    val defaultUriHandler: UriHandler
) : UriHandler {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun openUri(uri: String) {
        if (uri.contains(ForumConfig.baseUrl)) {
            val httpUrl = uri.toHttpUrl()
            val segments = httpUrl.pathSegments
            val queryMap = httpUrl.query?.split("&")?.associate {
                val (key, value) = it.split("=")
                key to value
            } ?: emptyMap()
            when (segments.getOrNull(0)) {
                "d" -> {
                    val discussion = segments.getOrNull(1)
                    val number = segments.getOrNull(2)
                    val post = queryMap["post"]
                    if (post != null) {
                        navigator.navigate(PostBottomSheetDestination(post))
                    } else if (discussion != null) {
                        navigator.navigate(
                            DiscussionDetailPageDestination(
                                discussion,
                                number?.toIntOrNull() ?: 0
                            )
                        )
                    }
                }

                "u" -> {
                    val user = segments.getOrNull(1)
                    user?.let {
                        navigator.navigate(UserProfilePageDestination(it))
                    }
                }

                else -> {
                    defaultUriHandler.openUri(uri)
                }
            }
        } else {
            defaultUriHandler.openUri(uri)
        }
    }
}
