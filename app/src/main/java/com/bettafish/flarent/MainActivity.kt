package com.bettafish.flarent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.navigation.ModalBottomSheetLayout
import androidx.compose.material.navigation.rememberBottomSheetNavigator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.navigation.plusAssign
import com.bettafish.flarent.ui.theme.FlarentTheme
import com.bettafish.flarent.ui.widgets.GlobalImagePreviewerProvider
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.NavHostAnimatedDestinationStyle
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.AccountPageDestination
import com.ramcosta.composedestinations.generated.destinations.DiscussionDetailPageDestination
import com.ramcosta.composedestinations.generated.destinations.MainPageDestination
import com.ramcosta.composedestinations.generated.destinations.PostBottomSheetDestination
import com.ramcosta.composedestinations.generated.destinations.TagListPageDestination
import com.ramcosta.composedestinations.generated.destinations.UserProfilePageDestination
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import com.ramcosta.composedestinations.utils.currentDestinationAsState
import com.ramcosta.composedestinations.utils.startDestination
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

@Composable
@ExperimentalMaterial3Api
fun FlarentApp() {
    val navController = rememberNavController()
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    navController.navigatorProvider += bottomSheetNavigator
    val navigator = navController.toDestinationsNavigator()
    val defaultUriHandler = LocalUriHandler.current



    val uriHandler = object : UriHandler {
        override fun openUri(url: String) {
            if (url.contains(BuildConfig.FLARUM_BASE_URL)) {
                val httpUrl = url.toHttpUrl()
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
                }
            } else {
                defaultUriHandler.openUri(url)
            }
        }
    }


    Scaffold(bottomBar = { BottomBar(navController) }) { innerPadding ->
        GlobalImagePreviewerProvider {
            CompositionLocalProvider(LocalUriHandler provides uriHandler) {
                ModalBottomSheetLayout(
                    bottomSheetNavigator = bottomSheetNavigator,
                    sheetBackgroundColor = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxSize()
                ){
                    DestinationsNavHost(
                        navController = navController,
                        modifier = Modifier.fillMaxSize().padding(bottom = innerPadding.calculateBottomPadding()),
                        navGraph = NavGraphs.root,
                        defaultTransitions = SlideTransitions)
                }}

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


@OptIn(ExperimentalCoroutinesApi::class)
@Composable
@ExperimentalMaterial3Api
fun BottomBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val currentDestination: DestinationSpec = navController.currentDestinationAsState().value
        ?: NavGraphs.root.startDestination
    val destinationsNavigator = navController.toDestinationsNavigator()
    val shouldShowBottomBar = currentDestination.route == MainPageDestination.route ||
            currentDestination.route == TagListPageDestination.route ||
            currentDestination.route == AccountPageDestination.route

    AnimatedVisibility(
        modifier = modifier.fillMaxWidth(),
        visible = shouldShowBottomBar,
        enter = expandIn() + expandVertically(expandFrom = Alignment.CenterVertically),
        exit = shrinkOut() + shrinkVertically(shrinkTowards = Alignment.CenterVertically)
    ) {
        NavigationBar () {
            BottomBarDestination.entries.fastForEach { destination ->
                NavigationBarItem(
                    selected = currentDestination == destination.direction,
                    onClick = {
                        destinationsNavigator.navigate(destination.direction) {
                            launchSingleTop = true
                            restoreState = true
                            popUpTo(NavGraphs.root.startDestination) {
                                saveState = true
                            }
                        }
                    },
                    icon = { Icon(destination.icon, contentDescription = destination.label)},
                    label = { destination.label },
                )
            }
        }
    }

}

@ExperimentalMaterial3Api
enum class BottomBarDestination(
    val direction: DirectionDestinationSpec,
    val icon: ImageVector,
    val label: String,
) {
    Home(MainPageDestination, Icons.Default.Home, "Home"),
    @OptIn(ExperimentalCoroutinesApi::class)
    Tags(TagListPageDestination, Icons.Default.Category, "Tags"),
    Account(AccountPageDestination, Icons.Default.AccountCircle, "Account"),
}

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    Text(text = "Profile Screen", modifier = modifier.padding(16.dp))
}
