package com.bettafish.flarent.ui.pages.discussionList

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreProvider
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.bettafish.flarent.App
import com.bettafish.flarent.models.Tag
import com.bettafish.flarent.models.navigation.DiscussionListNavArgs
import com.bettafish.flarent.ui.widgets.BackNavigationIcon
import com.bettafish.flarent.ui.widgets.DiscussionItem
import com.bettafish.flarent.ui.widgets.DiscussionItemViewModel
import com.bettafish.flarent.utils.appSettings
import com.bettafish.flarent.utils.toFaIcon
import com.guru.fontawesomecomposelib.FaIcon
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.NavGraph
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.DiscussionDetailPageDestination
import com.ramcosta.composedestinations.generated.destinations.DiscussionListPageDestination
import com.ramcosta.composedestinations.generated.destinations.NotificationsPageDestination
import com.ramcosta.composedestinations.generated.destinations.UserProfilePageDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
@Destination<RootGraph>(
    navArgs = DiscussionListNavArgs::class
)
@Composable
@ExperimentalMaterial3Api
fun DiscussionListPage(
    modifier: Modifier = Modifier,
    args: DiscussionListNavArgs? = null,
    navigator: DestinationsNavigator,
    vm: DiscussionListViewModel? = null
) {
    var filter : MutableMap<String,String>? = null;
    val filterArray = args?.filter
    if(filterArray != null) {
        filter = mutableMapOf()
        for (i in filterArray.indices step 2) {
            if (i + 1 < filterArray.size) {
                filter[filterArray[i]] = filterArray[i + 1]
            } else {
                filter[filterArray[i]] = ""
            }
        }
    }
    val viewModel = vm ?: koinViewModel(parameters = { parametersOf(filter) })

    val pagingItems = viewModel.discussions.collectAsLazyPagingItems()
    val storeProvider = rememberViewModelStoreProvider()

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    PullToRefreshBox(
        isRefreshing = pagingItems.loadState.refresh is LoadState.Loading,
        onRefresh = { pagingItems.refresh() },
        modifier = modifier.fillMaxSize()
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopAppBar(
                    title = { Text(text = args?.title ?: "帖子") },
                    navigationIcon = {
                        if (args?.title != null) {
                            BackNavigationIcon { navigator.navigateUp() }
                        }
                    },
                    scrollBehavior = scrollBehavior,
                    actions = {
                        if (args?.title == null) {
                            val user = App.INSTANCE.appSettings.user
                            user?.newNotificationCount?.let {
                                IconButton(onClick = {
                                    navigator.navigate(NotificationsPageDestination())
                                    App.INSTANCE.appSettings.user!!.newNotificationCount = 0
                                }) {
                                    if (it > 0) {
                                        BadgedBox(badge = {
                                            Badge { Text(it.toString()) }
                                        }) {
                                            Icon(
                                                Icons.Filled.Notifications,
                                                "通知",
                                                tint = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    } else {
                                        Icon(Icons.Outlined.Notifications, "通知")
                                    }
                                }

                            }


                        }
                    }
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                items(
                    count = pagingItems.itemCount,
                    key = { index -> pagingItems[index]?.id ?: index }
                ) { index ->
                    val discussion = pagingItems[index]
                    discussion?.let { item ->
                        val owner = rememberViewModelStoreOwner(
                            provider = storeProvider,
                            key = item.id
                        )
                        CompositionLocalProvider(LocalViewModelStoreOwner provides owner) {
                            val itemViewModel: DiscussionItemViewModel =
                                koinViewModel<DiscussionItemViewModel>(key = item.id) {
                                    parametersOf(item.id, item)
                                }
                            DiscussionItem(itemViewModel, click = {
                                navigator.navigate(
                                    DiscussionDetailPageDestination(
                                        it.id,
                                        it.lastReadPostNumber ?: 0
                                    )
                                )
                            }, tagClick = {
                                navigator.navigate(
                                    DiscussionListPageDestination(
                                        DiscussionListNavArgs(
                                            filter = arrayOf("tag", it.slug!!),
                                            title = it.name
                                        )
                                    )
                                )
                            }, userClick = {
                                navigator.navigate(UserProfilePageDestination(it.username!!))
                            })
                        }
                    }
                }
                item {
                    if (pagingItems.loadState.append is LoadState.Loading) {
                        LinearProgressIndicator(
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

}


