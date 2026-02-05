package com.bettafish.flarent.ui.pages

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.bettafish.flarent.ui.widgets.BackNavigationIcon
import com.bettafish.flarent.ui.widgets.PostItem
import com.bettafish.flarent.viewModels.DiscussionDetailViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.DiscussionDetailPageDestination
import com.ramcosta.composedestinations.generated.destinations.PostBottomSheetDestination
import com.ramcosta.composedestinations.generated.destinations.UserProfilePageDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import kotlin.text.append
import kotlin.text.get

@Composable
@Destination<RootGraph>
@OptIn(ExperimentalMaterial3Api::class,ExperimentalCoroutinesApi::class)
fun DiscussionDetailPage(discussionId: String, targetPosition: Int = 0, navigator: DestinationsNavigator, modifier: Modifier = Modifier){
    val viewModel: DiscussionDetailViewModel = getViewModel() { parametersOf(discussionId, targetPosition) }
    val discussion by viewModel.discussion.collectAsState()
    val posts = viewModel.posts.collectAsLazyPagingItems()
    val initialIndex by viewModel.initialScrollIndex.collectAsState()

    PullToRefreshBox(
        isRefreshing = posts.loadState.refresh is LoadState.Loading,
        onRefresh = { posts.refresh() },
        modifier = modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = discussion?.title ?: "帖子") },

                    navigationIcon = {
                        BackNavigationIcon { navigator.navigateUp() }
                    },
                )
            }
        ) { innerPadding ->
            if (initialIndex != null ) {
                val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex!!)
                Box(modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()){
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize()
                    ){
                        items(
                            count = posts.itemCount,
                            key = posts.itemKey { it.id }
                        ) { index ->
                            val post = posts[index]
                            post?.let { item ->
                                PostItem(item,
                                    modifier = Modifier.padding(16.dp),
                                    userClick = { navigator.navigate(UserProfilePageDestination(it)) },
                                    discussionClick = { id,number-> navigator.navigate(DiscussionDetailPageDestination(id,number ?: 0)) },
                                    postClick = { navigator.navigate(PostBottomSheetDestination(it)) })
                            }
                        }
                    }
                    if (posts.loadState.prepend is LoadState.Loading) {
                        LinearProgressIndicator(
                            color = colorScheme.secondary,
                            modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth().zIndex(1f)
                        )
                    }
                    if (posts.loadState.append is LoadState.Loading) {
                        LinearProgressIndicator(
                            color = colorScheme.secondary,
                            modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().zIndex(1f)
                        )
                    }
                }

            }
        }
    }
}