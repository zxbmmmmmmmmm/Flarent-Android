package com.bettafish.flarent.ui.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.bettafish.flarent.ui.widgets.DiscussionItem
import com.bettafish.flarent.viewModels.DiscussionsViewModel
import org.koin.androidx.compose.getViewModel

@Composable
@Preview
@ExperimentalMaterial3Api
fun MainPage(modifier: Modifier = Modifier) {
    val viewModel: DiscussionsViewModel = getViewModel()
    val pagingItems = viewModel.discussions.collectAsLazyPagingItems()
    val isRefreshing = pagingItems.loadState.refresh is LoadState.Loading

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { pagingItems.refresh() },
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(count = pagingItems.itemCount) { index ->
                val discussion = pagingItems[index]
                discussion?.let {
                    DiscussionItem(it)
                }
            }

            // 底部加载更多状态
            item {
                if (pagingItems.loadState.append is LoadState.Loading) {
                    LinearProgressIndicator(
                        color = colorScheme.secondary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}


