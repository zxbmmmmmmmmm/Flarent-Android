package com.bettafish.flarent.ui.pages.vote

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import com.bettafish.flarent.models.User
import com.bettafish.flarent.ui.widgets.Avatar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.bottomsheet.spec.DestinationStyleBottomSheet
import com.ramcosta.composedestinations.generated.destinations.UserProfilePageDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Destination<RootGraph>(style = DestinationStyleBottomSheet::class)
fun VotesBottomSheet(postId: String, navigator: DestinationsNavigator){
    val viewModel: VotesViewModel = getViewModel{ parametersOf(postId) }
    val upvotersState = viewModel.upvoters.collectAsState()
    val downvotersState = viewModel.downvoters.collectAsState()
    val screenHeight = LocalWindowInfo.current.containerDpSize.height
    val canRefresh = viewModel.refreshCommand.canExecute.collectAsState()



    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxWidth()
        .defaultMinSize(minHeight = screenHeight / 2)
        .windowInsetsPadding(WindowInsets.systemBars)){
        if(!canRefresh.value){
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        else{
            val pagerState = rememberPagerState(
                if((upvotersState.value?.size ?: 0) >= (downvotersState.value?.size ?: 0))
                    0
                else
                    1
            ) { 2 }
            val tabs = listOf("赞同 ${upvotersState.value?.size ?: 0}", "反对 ${downvotersState.value?.size ?: 0}")
            Column(modifier = Modifier.fillMaxSize()) {
                TabRow(selectedTabIndex = pagerState.currentPage) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = { Text(title) }
                        )
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) { page ->
                    val users = if (page == 0) upvotersState.value else downvotersState.value
                    if (users.isNullOrEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("暂无数据", style = MaterialTheme.typography.bodyLarge)
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(count = users.size) { index ->
                                val padding =
                                    when (index) {
                                        0 if users.size == 1 -> PaddingValues(horizontal = 16.dp, vertical = 20.dp)
                                        0 -> {
                                            PaddingValues(16.dp, 20.dp, 16.dp, 16.dp)
                                        }
                                        users.size - 1 -> {
                                            PaddingValues(16.dp, 16.dp, 16.dp, 20.dp)
                                        }
                                        else -> {
                                            PaddingValues(horizontal = 16.dp, vertical = 16.dp)
                                        }
                                    }
                                UserItem(user = users[index], contentPadding = padding) {
                                    navigator.navigate(UserProfilePageDestination(users[index].username ?: ""))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun UserItem(user: User, contentPadding: PaddingValues = PaddingValues(16.dp), onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Avatar(
            avatarUrl = user.avatarUrl,
            name = user.displayName ?: user.username,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )
        Text(
            text = user.displayName ?: user.username ?: "Unknown",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}