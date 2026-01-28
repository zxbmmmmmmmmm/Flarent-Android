package com.bettafish.flarent.ui.pages

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.bettafish.flarent.ui.widgets.DiscussionItem
import com.bettafish.flarent.viewModels.DiscussionsViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import org.koin.androidx.compose.getViewModel

@Composable
@Destination<RootGraph>(start = true)
@ExperimentalMaterial3Api
fun DiscussionsPage(modifier: Modifier = Modifier, navController: NavController) {
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


