package com.bettafish.flarent.ui.pages.like

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.bettafish.flarent.ui.pages.vote.UserItem
import com.bettafish.flarent.ui.pages.vote.VotesViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.bottomsheet.spec.DestinationStyleBottomSheet
import com.ramcosta.composedestinations.generated.destinations.UserProfilePageDestination
import com.ramcosta.composedestinations.generated.destinations.UserProfilePageDestination.invoke
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Destination<RootGraph>(style = DestinationStyleBottomSheet::class)
fun LikesBottomSheet(
    postId: String,
    navigator: DestinationsNavigator,
    viewModel: LikesViewModel = koinViewModel { parametersOf(postId) }
) {

    val screenHeight = LocalWindowInfo.current.containerDpSize.height
    val likers = viewModel.likers.collectAsLazyPagingItems()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = screenHeight / 2)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        LazyColumn() {
            items(likers.itemCount) { index ->
                val user = likers[index]!!
                UserItem(user = user) {
                    navigator.navigate(UserProfilePageDestination(user.username!!))
                }
            }
            item {
                if (likers.loadState.append is LoadState.Loading || likers.loadState.refresh is LoadState.Loading) {
                    LinearProgressIndicator(
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

        }
    }
}