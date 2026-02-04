package com.bettafish.flarent.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.bettafish.flarent.models.Tag
import com.bettafish.flarent.models.navigation.TagNavArgs
import com.bettafish.flarent.ui.widgets.BackNavigationIcon
import com.bettafish.flarent.ui.widgets.DiscussionItem
import com.bettafish.flarent.ui.widgets.Toolbar
import com.bettafish.flarent.utils.toFaIcon
import com.bettafish.flarent.viewModels.DiscussionsViewModel
import com.guru.fontawesomecomposelib.FaIcon
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.DiscussionDetailPageDestination
import com.ramcosta.composedestinations.generated.destinations.DiscussionsPageDestination
import com.ramcosta.composedestinations.generated.destinations.UserProfilePageDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
@Destination<RootGraph>(
    navArgs = TagNavArgs::class
)
@ExperimentalMaterial3Api
fun DiscussionsPage(
    modifier: Modifier = Modifier,
    tag: TagNavArgs? = null,
    navigator: DestinationsNavigator,
) {
    val viewModel: DiscussionsViewModel = getViewModel() { parametersOf(tag) }
    val pagingItems = viewModel.discussions.collectAsLazyPagingItems()

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
                    title = { Text(text = tag?.name ?: "帖子") },
                    navigationIcon = {
                        if (tag != null) {
                            BackNavigationIcon { navigator.navigateUp() }
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                items(count = pagingItems.itemCount) { index ->
                    val discussion = pagingItems[index]
                    discussion?.let { item ->
                        DiscussionItem(item, click = {
                            navigator.navigate(DiscussionDetailPageDestination(it.id, it.lastReadPostNumber?:0))
                        }, tagClick = {
                            navigator.navigate(DiscussionsPageDestination(TagNavArgs.from(it)))
                        }, userClick = {
                            navigator.navigate(UserProfilePageDestination(it.username!!))
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

}

@Composable
fun TagHeader(tag: TagNavArgs){
    Surface(color = colorScheme.secondaryContainer, modifier = Modifier.fillMaxWidth()) {
        Column() {
            Row() {
                intArrayOf()
                val textStyle = MaterialTheme.typography.displayMedium
                val density = LocalDensity.current
                val textHeightDp = with(density) { textStyle.lineHeight.toDp() }
                val icon = tag.icon?.toFaIcon()
                if (icon != null)
                    FaIcon(
                        faIcon = icon,
                        size = textHeightDp,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .align(Alignment.CenterVertically),
                        tint = LocalContentColor.current
                    )
                Text(
                    text = tag.name ?: "",
                    style = textStyle,
                )
            }
            tag.description?.let{
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

    }
}

@Composable
@Preview
fun TagHeaderPreview(){
    TagHeader(TagNavArgs.from(Tag().apply {
        name = "Windows"
        slug = "ai"
        icon = "fas fa-windows"
        description = "Microsoft, your potential, our passion."
        color = "#0077C8"
    }))
}


