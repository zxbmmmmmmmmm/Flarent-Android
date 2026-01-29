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
import com.bettafish.flarent.models.Tag
import com.bettafish.flarent.models.navigation.TagNavArgs
import com.bettafish.flarent.ui.widgets.DiscussionItem
import com.bettafish.flarent.viewModels.DiscussionsViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.DiscussionsPageDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
@Destination<RootGraph>(
    navArgs = TagNavArgs::class
)
@ExperimentalMaterial3Api
fun DiscussionsPage(modifier: Modifier = Modifier,tag: TagNavArgs ?= null, navigator: DestinationsNavigator) {
    val viewModel: DiscussionsViewModel = getViewModel(){ parametersOf(tag) }
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
                discussion?.let { tag ->
                    DiscussionItem(tag, tagClick = {
                        navigator.navigate(DiscussionsPageDestination(TagNavArgs.from(it)))
                    })
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


