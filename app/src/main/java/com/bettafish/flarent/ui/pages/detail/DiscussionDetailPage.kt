package com.bettafish.flarent.ui.pages.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.bettafish.flarent.ui.widgets.BackNavigationIcon
import com.bettafish.flarent.ui.widgets.LocalImagePreviewer
import com.bettafish.flarent.ui.widgets.post.PostItem
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.ReplyBottomSheetDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
@Destination<RootGraph>
@OptIn(ExperimentalMaterial3Api::class,ExperimentalCoroutinesApi::class)
fun DiscussionDetailPage(discussionId: String, targetPosition: Int = 0, navigator: DestinationsNavigator, modifier: Modifier = Modifier){
    val viewModel: DiscussionDetailViewModel = getViewModel() { parametersOf(discussionId, targetPosition) }
    val discussion by viewModel.discussion.collectAsState()
    val posts = viewModel.combinedPosts.collectAsLazyPagingItems()
    val initialIndex by viewModel.initialScrollIndex.collectAsState()

    PullToRefreshBox(
        isRefreshing = posts.loadState.refresh is LoadState.Loading,
        onRefresh = { posts.refresh() },
        modifier = modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = discussion?.title ?: "帖子", maxLines = 1, overflow = TextOverflow.Ellipsis ) },
                    navigationIcon = {
                        BackNavigationIcon { navigator.navigateUp() }
                    },
                    colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = colorScheme.surfaceContainer)
                )
            },
            bottomBar = {
                Row(modifier = Modifier.background(colorScheme.surfaceContainer).padding(bottom = 8.dp).height(48.dp)) {
                    Box(modifier = Modifier.fillMaxHeight().weight(1f).clickable(){
                        navigator.navigate(ReplyBottomSheetDestination(discussionId, discussion?.title))
                    }) {
                        Text(text = "回复" , color = colorScheme.outline, modifier = Modifier.padding(start = 16.dp).align(Alignment.CenterStart))
                    }
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.MoreHoriz, "更多")
                    }
                }
            }
        ) { innerPadding ->
            if (initialIndex != null ) {
                val listState = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex!!)
                Box(modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()){
                    val imagePreviewer = LocalImagePreviewer.current
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
                                    null,
                                    navigator,
                                    modifier = Modifier.padding(16.dp),
                                    isOp = post.user?.id == discussion?.user?.id,
                                )
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