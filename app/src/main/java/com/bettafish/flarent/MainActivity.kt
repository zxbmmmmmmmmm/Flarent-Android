package com.bettafish.flarent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.navigation.ModalBottomSheetLayout
import androidx.compose.material.navigation.rememberBottomSheetNavigator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.IntOffset
import androidx.navigation.NavBackStackEntry
import androidx.navigation.compose.rememberNavController
import androidx.navigation.plusAssign
import com.bettafish.flarent.ui.theme.FlarentTheme

import com.bettafish.flarent.ui.widgets.GlobalImagePreviewerProvider
import com.bettafish.flarent.utils.appSettings
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.NavHostAnimatedDestinationStyle
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.DiscussionDetailPageDestination
import com.ramcosta.composedestinations.generated.destinations.PostBottomSheetDestination
import com.ramcosta.composedestinations.generated.destinations.UserProfilePageDestination
import com.ramcosta.composedestinations.generated.destinations.WelcomePageDestination
import com.ramcosta.composedestinations.spec.Direction
import com.ramcosta.composedestinations.utils.toDestinationsNavigator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import okhttp3.HttpUrl.Companion.toHttpUrl

class MainActivity : ComponentActivity() {
    @ExperimentalMaterial3Api
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlarentTheme {
                FlarentApp()
            }
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
@ExperimentalMaterial3Api
fun FlarentApp() {
    val navController = rememberNavController()
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    navController.navigatorProvider += bottomSheetNavigator
    val navigator = navController.toDestinationsNavigator()
    val defaultUriHandler = LocalUriHandler.current


    val uriHandler = object : UriHandler {
        override fun openUri(uri: String) {
            if (uri.contains(BuildConfig.FLARUM_BASE_URL)) {
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
                        if(post != null){
                            navigator.navigate(PostBottomSheetDestination(post))
                        }
                        else if (discussion != null){
                            navigator.navigate(DiscussionDetailPageDestination(discussion,number?.toIntOrNull() ?: 0))
                        }
                    }
                    "u" -> {
                        val user = segments.getOrNull(1)
                        user?.let {
                            navigator.navigate(UserProfilePageDestination(it))
                        }
                    }
                    else ->{
                        defaultUriHandler.openUri(uri)
                    }
                }
            } else {
                defaultUriHandler.openUri(uri)
            }
        }
    }


    GlobalImagePreviewerProvider {
        CompositionLocalProvider(LocalUriHandler provides uriHandler) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background, // 背景色
                contentColor = MaterialTheme.colorScheme.onBackground // 自动传递给子组件的文字颜色
            ){
                ModalBottomSheetLayout(
                    bottomSheetNavigator = bottomSheetNavigator,
                    sheetBackgroundColor = MaterialTheme.colorScheme.surface,
                    sheetContentColor = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxSize()
                ){
                    val start: Direction = if(App.INSTANCE.appSettings.forum == null){
                        WelcomePageDestination
                    }
                    else{
                        NavGraphs.root.defaultStartDirection
                    }
                    DestinationsNavHost(
                        navController = navController,
                        modifier = Modifier.fillMaxSize(),
                        navGraph = NavGraphs.root,
                        start = start,
                        defaultTransitions = SlideTransitions
                    )
                }
            }

        }
    }
}
object SlideTransitions : NavHostAnimatedDestinationStyle() {
    private val AnimationSpec = tween<IntOffset>(durationMillis = 300, easing = FastOutSlowInEasing)

    override val enterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.Start,
            animationSpec = AnimationSpec,
            initialOffset = { it }
        )
    }

    override val exitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.End,
            animationSpec = AnimationSpec,
            targetOffset = { -it }
        )
    }

    override val popEnterTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
        slideIntoContainer(
            AnimatedContentTransitionScope.SlideDirection.Start,
            animationSpec = AnimationSpec,
            initialOffset = { -it }
        )
    }

    override val popExitTransition: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
        slideOutOfContainer(
            AnimatedContentTransitionScope.SlideDirection.End,
            animationSpec = AnimationSpec,
            targetOffset = { it }
        )
    }

}

