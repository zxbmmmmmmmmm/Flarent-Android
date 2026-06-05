package com.bettafish.flarent.ui.pages.detail
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.twotone.MoveUp
import androidx.compose.material.icons.twotone.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.NavigationEventTransitionState
import androidx.navigationevent.compose.NavigationBackHandler
import androidx.navigationevent.compose.rememberNavigationEventState
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.bettafish.flarent.R
import com.bettafish.flarent.ui.theme.defaultTypography
import com.bettafish.flarent.ui.widgets.BackNavigationIcon
import com.bettafish.flarent.ui.widgets.KnowledgeTopAppBar
import com.bettafish.flarent.ui.widgets.TagList
import com.bettafish.flarent.ui.widgets.post.PostItem
import com.bettafish.flarent.ui.widgets.post.PostItemPlaceholder
import com.bettafish.flarent.ui.widgets.post.PostItemViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.DiscussionDetailPageDestination
import com.ramcosta.composedestinations.generated.destinations.ReplyBottomSheetDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.math.roundToInt

@Composable
@Destination<RootGraph>
@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoroutinesApi::class)
fun DiscussionDetailPage(
    discussionId: String,
    targetPosition: Int = 0,
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: DiscussionDetailViewModel = koinViewModel {
        parametersOf(
            discussionId,
            targetPosition
        )
    }
) {
    val discussion by viewModel.discussion.collectAsState()
    val posts = viewModel.posts.collectAsLazyPagingItems()
    val scrollTarget by viewModel.scrollTarget.collectAsState()

    var showSheet by remember { mutableStateOf(false) }
    var showJumpDialog by remember { mutableStateOf(false) }
    var jumpInput by remember { mutableStateOf("") }
    var initialScrollConsumed by rememberSaveable(discussionId) { mutableStateOf(false) }
    var pendingJumpTarget by remember { mutableStateOf<Int?>(null) }
    val canLoadDiscussionCommandExec = viewModel.loadDiscussionCommand.canExecute.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val target = scrollTarget
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = if (initialScrollConsumed) 0 else target ?: 0
    )
    val lastVisibleItem by remember {
        derivedStateOf {
            val visibleItems = listState.layoutInfo.visibleItemsInfo
            if (visibleItems.isNotEmpty()) {
                val lastIndex = visibleItems.last().index
                posts[lastIndex]
            } else {
                null
            }
        }
    }
    PullToRefreshBox(
        isRefreshing = posts.loadState.refresh is LoadState.Loading || !canLoadDiscussionCommandExec.value,
        onRefresh = { viewModel.loadDiscussionCommand.execute() },
        modifier = modifier.fillMaxSize()
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                KnowledgeTopAppBar(
                    topLayout = {
                        Text(
                            text = discussion?.title ?: stringResource(R.string.posts),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 20.sp
                        )
                    },
                    bottomLayout = {
                        Column(
                            modifier = Modifier.padding(
                                start = 16.dp,
                                end = 16.dp,
                                bottom = 24.dp
                            ), verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = discussion?.title ?: stringResource(R.string.posts),
                                style = defaultTypography.titleLarge
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                discussion?.tags?.let {
                                    TagList(it)
                                }
                                discussion?.commentCount?.let {
                                    Text(
                                        pluralStringResource(R.plurals.reply_count, it, it),
                                        color = colorScheme.outline,
                                        style = defaultTypography.bodyMedium
                                    )
                                }
                            }

                        }
                    },
                    navigationIcon = {
                        BackNavigationIcon { navigator.navigateUp() }
                    },
                    actions = {
                        IconButton(
                            onClick = { showSheet = true },
                            modifier = Modifier.align(Alignment.CenterVertically)
                        ) {
                            Icon(Icons.Default.MoreVert, stringResource(R.string.more), tint = colorScheme.outline)
                        }
                    },
                    scrollBehavior = scrollBehavior
                )
            },
            bottomBar = {
                Row(
                    modifier = Modifier
                        .background(colorScheme.surfaceContainer)
                        .padding(horizontal = 8.dp)
                        .navigationBarsPadding()
                        .height(56.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1f)
                            .padding(start = 8.dp, top = 8.dp, bottom = 8.dp)
                            .clip(RoundedCornerShape(64.dp))
                            .background(colorScheme.surfaceContainerHigh)
                            .clickable {
                                navigator.navigate(
                                    ReplyBottomSheetDestination(
                                        discussionId = discussionId,
                                        postId = null,
                                        discussionTitle = discussion?.title,
                                        content = null
                                    )
                                )
                            }) {
                        Text(
                            text = stringResource(R.string.say_something),
                            color = colorScheme.outline,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .padding(start = 20.dp)
                                .align(Alignment.CenterStart)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(start = 8.dp, top = 8.dp, bottom = 8.dp, end = 4.dp)
                            .clip(RoundedCornerShape(64.dp))
                            .clickable {
                                showJumpDialog = true
                            }) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(horizontal = 8.dp)) {

                            lastVisibleItem?.number?.let {
                                Text(
                                    text = it.toString(),
                                )
                            }
                            Text(
                                text = "/",
                                color = colorScheme.outline

                            )
                            discussion?.lastPostNumber?.let {
                                Text(
                                    text = it.toString(),
                                    color = colorScheme.outline

                                )
                            }
                        }

                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {

                if (target != null && canLoadDiscussionCommandExec.value) {

                    LaunchedEffect(target, pendingJumpTarget) {
                        val jumpTarget = pendingJumpTarget
                        if (!initialScrollConsumed) {
                            listState.scrollToItem(target)
                            initialScrollConsumed = true
                        } else if (jumpTarget == target) {
                            listState.scrollToItem(target)
                            pendingJumpTarget = null
                        }
                    }
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            count = posts.itemCount,
                            key = posts.itemKey { it.id }
                        ) { index ->
                            val post = posts[index]

                            if (post != null) {
                                val parentEntry =
                                    remember(navController.currentBackStackEntry) {
                                        navController.getBackStackEntry(
                                            DiscussionDetailPageDestination.route
                                        )
                                    }
                                val viewModel: PostItemViewModel =
                                    koinViewModel<PostItemViewModel>(
                                        key = post.id,
                                        viewModelStoreOwner = parentEntry
                                    ) { parametersOf(post.id, post) }
                                PostItem(
                                    viewModel,
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
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .fillMaxWidth()
                                .zIndex(1f)
                        )
                    }
                    if (posts.loadState.append is LoadState.Loading) {
                        LinearProgressIndicator(
                            color = colorScheme.secondary,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .zIndex(1f)
                        )
                    }

                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.SpaceAround
                    ) {
                        PostItemPlaceholder(modifier = Modifier.padding(16.dp))
                        PostItemPlaceholder(modifier = Modifier.padding(16.dp))
                        PostItemPlaceholder(modifier = Modifier.padding(16.dp))
                        PostItemPlaceholder(modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
    val sheetState = rememberModalBottomSheetState()

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            dragHandle = { BottomSheetDefaults.DragHandle() }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
                    .navigationBarsPadding(),
            ) {
                BottomSheetMenuItem(
                    icon = Icons.TwoTone.MoveUp,
                    text = stringResource(R.string.jump_to_floor),
                    onClick = {
                        jumpInput = ""
                        showJumpDialog = true
                        showSheet = false
                    }
                )

                BottomSheetMenuItem(
                    icon = Icons.TwoTone.Refresh,
                    text = stringResource(R.string.refresh),
                    onClick = {
                        viewModel.loadDiscussionCommand.execute()
                        showSheet = false
                    }
                )
            }
        }
    }



    if (showJumpDialog) {
        AlertDialog(
            onDismissRequest = { showJumpDialog = false },
            title = { Text(stringResource(R.string.jump_to_floor)) },
            text = {
                val totalCount = discussion?.lastPostNumber ?: discussion?.posts?.size
                TextField(
                    value = jumpInput,
                    onValueChange = { jumpInput = it.filter { char -> char.isDigit() } },
                    label = {
                        Text(
                            if (totalCount != null) {
                                stringResource(R.string.floor_number_with_range, totalCount)
                            } else {
                                stringResource(R.string.floor_number)
                            }
                        )
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
                            val position = floor - 1
                            pendingJumpTarget = position
                            viewModel.jumpToPosition(position)
                        }
                        showJumpDialog = false
                    }
                ) { Text(stringResource(R.string.confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showJumpDialog = false }) { Text(stringResource(R.string.cancel)) }
            }
        )
    }
}

@Composable
fun BottomSheetMenuItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp)) {
        Icon(icon, contentDescription = text, tint = colorScheme.secondary)
        Text(text = text)
    }
}
