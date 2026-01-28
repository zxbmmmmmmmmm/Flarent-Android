package com.bettafish.flarent

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bettafish.flarent.ui.pages.DiscussionsPage
import com.bettafish.flarent.ui.pages.TagsPage
import com.bettafish.flarent.ui.theme.FlarentTheme
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.DiscussionsPageDestination
import com.ramcosta.composedestinations.generated.destinations.TagsPageDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.spec.DestinationSpec
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec
import com.ramcosta.composedestinations.utils.currentDestinationAsState
import com.ramcosta.composedestinations.utils.startDestination
import com.ramcosta.composedestinations.utils.toDestinationsNavigator

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

    Scaffold(bottomBar = { BottomBar(navController) }) { innerPadding ->
        DestinationsNavHost(
            navController = navController,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            navGraph = NavGraphs.root)
    }
}

@Composable
@ExperimentalMaterial3Api
fun BottomBar(
    navController: NavController
) {
    val currentDestination: DestinationSpec = navController.currentDestinationAsState().value
        ?: NavGraphs.root.startDestination
    val destinationsNavigator = navController.toDestinationsNavigator()
    NavigationBar () {
        BottomBarDestination.entries.forEach { destination ->
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

@ExperimentalMaterial3Api
enum class BottomBarDestination(
    val direction: DirectionDestinationSpec,
    val icon: ImageVector,
    val label: String,
) {
    Home(DiscussionsPageDestination, Icons.Default.Home, "Home"),
    Tags(TagsPageDestination, Icons.Default.Category, "Tags"),
}

@Composable
fun ProfileScreen(modifier: Modifier = Modifier) {
    Text(text = "Profile Screen", modifier = modifier.padding(16.dp))
}
