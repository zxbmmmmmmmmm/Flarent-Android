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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
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
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.bettafish.flarent.ui.widgets.BackNavigationIcon
import com.bettafish.flarent.ui.widgets.LocalImagePreviewer
import com.bettafish.flarent.ui.widgets.post.PostItem
import com.bettafish.flarent.ui.widgets.post.PostItemPlaceholder
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.ReplyBottomSheetDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
@Destination<RootGraph>
@OptIn(ExperimentalMaterial3Api::class,ExperimentalCoroutinesApi::class)
fun DiscussionDetailPage(discussionId: String, targetPosition: Int = 0, navigator: DestinationsNavigator, modifier: Modifier = Modifier){
    val viewModel: DiscussionDetailViewModel = getViewModel() { parametersOf(discussionId, targetPosition) }
    val discussion by viewModel.discussion.collectAsState()
    val posts = viewModel.posts.collectAsLazyPagingItems()
    val scrollTarget by viewModel.scrollTarget.collectAsState()

    var showMoreMenu by remember { mutableStateOf(false) }
    var showJumpDialog by remember { mutableStateOf(false) }
    var jumpInput by remember { mutableStateOf("") }

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
                    Box {
                        IconButton(onClick = { showMoreMenu = true }) {
                            Icon(Icons.Default.MoreHoriz, "更多")
                        }
                        DropdownMenu(
                            expanded = showMoreMenu,
                            onDismissRequest = { showMoreMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("跳转到楼层") },
                                onClick = {
                                    jumpInput = ""
                                    showMoreMenu = false
                                    showJumpDialog = true
                                }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            val target = scrollTarget
            if (target != null) {
                val listState = rememberLazyListState(initialFirstVisibleItemIndex = target)

                LaunchedEffect(target) {
                    listState.scrollToItem(target)
                }

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
                            if (post != null) {
                                PostItem(post,
                                    null,
                                    navigator,
                                    modifier = Modifier.padding(16.dp),
                                    isOp = post.user?.id == discussion?.user?.id,
                                )
                            } else {
                                PostItemPlaceholder(modifier = Modifier.padding(16.dp))
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

    if (showJumpDialog) {
        AlertDialog(
            onDismissRequest = { showJumpDialog = false },
            title = { Text("跳转到楼层") },
            text = {
                val totalCount = discussion?.lastPostNumber ?: discussion?.posts?.size
                TextField(
                    value = jumpInput,
                    onValueChange = { jumpInput = it.filter { char -> char.isDigit() } },
                    label = {
                        Text(if (totalCount != null) "楼层号 (1–$totalCount)" else "楼层号")
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val floor = jumpInput.toIntOrNull()
                        if (floor != null && floor >= 1) {
                            viewModel.jumpToPosition(floor - 1)
                        }
                        showJumpDialog = false
                    }
                ) { Text("确认") }
            },
            dismissButton = {
                TextButton(onClick = { showJumpDialog = false }) { Text("取消") }
            }
        )
    }
}
