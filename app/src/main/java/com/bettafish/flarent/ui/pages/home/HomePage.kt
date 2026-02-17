package com.bettafish.flarent.ui.pages.home

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.twotone.AccountCircle
import androidx.compose.material.icons.twotone.Category
import androidx.compose.material.icons.twotone.Home
import androidx.compose.material.icons.twotone.QuestionAnswer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.bettafish.flarent.models.navigation.LoginResult
import com.bettafish.flarent.ui.pages.account.AccountPage
import com.bettafish.flarent.ui.pages.discussionList.DiscussionListPage
import com.bettafish.flarent.ui.pages.tagList.TagListPage
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import com.ramcosta.composedestinations.generated.destinations.LoginPageDestination
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@Composable
@Destination<RootGraph>(start=true)
@ExperimentalMaterial3Api
@OptIn(ExperimentalCoroutinesApi::class)
fun HomePage(
    navigator: DestinationsNavigator,
    resultRecipient: ResultRecipient<LoginPageDestination, LoginResult>
){
    val tabs = listOf(
        HomeTab.DiscussionList,
        HomeTab.Tags,
        HomeTab.Account
    )
    val pagerState = rememberPagerState { tabs.size }
    val scope = rememberCoroutineScope()

    Scaffold(
        bottomBar = {
            NavigationBar(
                tonalElevation = 8.dp,
                modifier = Modifier.height(56.dp)) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                        icon = { if(pagerState.currentPage == index)
                            Icon(tab.selectedIcon, contentDescription = tab.label)
                        else
                            Icon(tab.icon, contentDescription = tab.label) },
                        alwaysShowLabel = false,
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent,
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) { page ->
            when (tabs[page]) {
                HomeTab.DiscussionList -> DiscussionListPage(navigator = navigator)
                HomeTab.Tags -> TagListPage(navigator = navigator)
                HomeTab.Account -> AccountPage(navigator = navigator, resultRecipient = resultRecipient)
            }
        }
    }
}

sealed class HomeTab(val label: String, val icon: ImageVector, val selectedIcon: ImageVector = icon) {
    data object DiscussionList : HomeTab("帖子", Icons.TwoTone.QuestionAnswer, Icons.Default.QuestionAnswer)
    data object Tags : HomeTab("分类", Icons.TwoTone.Category, Icons.Default.Category)
    data object Account : HomeTab("我的", Icons.TwoTone.AccountCircle, Icons.Default.AccountCircle)
}