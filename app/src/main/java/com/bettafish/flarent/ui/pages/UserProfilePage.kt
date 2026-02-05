package com.bettafish.flarent.ui.pages

import android.R.attr.maxHeight
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.LocalOverscrollFactory
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.bettafish.flarent.models.navigation.TagNavArgs
import com.bettafish.flarent.ui.widgets.BackNavigationIcon
import com.bettafish.flarent.ui.widgets.DiscussionItem
import com.bettafish.flarent.ui.widgets.PostItem
import com.bettafish.flarent.ui.widgets.ProfileHeader
import com.bettafish.flarent.viewModels.UserProfileViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.DiscussionDetailPageDestination
import com.ramcosta.composedestinations.generated.destinations.DiscussionsPageDestination
import com.ramcosta.composedestinations.generated.destinations.PostBottomSheetDestination
import com.ramcosta.composedestinations.generated.destinations.TagsPageDestination
import com.ramcosta.composedestinations.generated.destinations.UserProfilePageDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
@Destination<RootGraph>
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
fun UserProfilePage(userName: String, navigator: DestinationsNavigator, modifier: Modifier = Modifier){
    val viewModel: UserProfileViewModel = getViewModel{ parametersOf(userName) }
    val user by viewModel.user.collectAsState()
    val posts = viewModel.posts.collectAsLazyPagingItems()
    val discussions = viewModel.discussions.collectAsLazyPagingItems()
    val headerColor: Color = MaterialTheme.colorScheme.surfaceContainer
    val scrollState = rememberScrollState()
    var headerHeight by remember { mutableIntStateOf(0) }

    val showTitle by remember {
        derivedStateOf {
            scrollState.value > headerHeight - 10 || headerHeight == 0
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    AnimatedVisibility(
                        visible = showTitle,
                        enter = fadeIn(),
                        exit = fadeOut()
                    )  {
                        Text(user?.displayName ?: userName)
                    }
                },
                navigationIcon = { BackNavigationIcon { navigator.popBackStack() } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = headerColor)
            )

        }
    ){  padding ->
        BoxWithConstraints(
            modifier = Modifier.padding(padding)){
            val screenHeight = maxHeight
            Column(modifier = modifier
                .fillMaxSize()
                .verticalScroll(state = scrollState)){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned { coordinates ->
                            headerHeight = coordinates.size.height
                        }
                ) {
                    user?.let {
                        ProfileHeader(it, modifier = Modifier.background(headerColor).padding(16.dp))
                    }
                }
                Column(modifier = Modifier.height(screenHeight)){
                    val tabs = listOf(
                        "回复 ${user?.commentCount ?: ""}",
                        "主题 ${user?.discussionCount ?: ""}"
                    )
                    val coroutineScope = rememberCoroutineScope()
                    val pagerState = rememberPagerState { tabs.size }

                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                            )
                        },
                        containerColor = headerColor,
                        divider = {}
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    coroutineScope.launch { pagerState.animateScrollToPage(index) }
                                },
                                text = { Text(title, fontSize = 14.sp) }
                            )
                        }
                    }
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxHeight()
                            .nestedScroll(remember {
                                object : NestedScrollConnection {
                                    override fun onPreScroll(
                                        available: Offset,
                                        source: NestedScrollSource
                                    ): Offset {
                                        return if (available.y > 0) Offset.Zero else Offset(
                                            x = 0f,
                                            y = -scrollState.dispatchRawDelta(-available.y)
                                        )
                                    }

                                }
                            }),
                    ) { page ->
                        when (page) {
                            0 -> PagingDataList(posts) { post ->
                                post.discussion?.title?.let {
                                    Text(it,
                                        color = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)
                                            .clickable{
                                                navigator.navigate(DiscussionDetailPageDestination(post.discussion!!.id, post.discussion?.lastReadPostNumber?:0))
                                            })
                                }
                                PostItem(post,
                                    modifier = modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 16.dp),
                                    userClick = {
                                        if(it != user?.username){
                                            navigator.navigate(UserProfilePageDestination(it))
                                        }
                                    },
                                    postClick = {
                                        navigator.navigate(PostBottomSheetDestination(it))
                                    },
                                    discussionClick = { id,number-> navigator.navigate(DiscussionDetailPageDestination(id,number ?: 0)) }
                                )
                            }
                            1 -> PagingDataList(discussions) { discussion ->
                                DiscussionItem(discussion,
                                    userClick = {
                                        if(it.id != user?.id){
                                            navigator.navigate(UserProfilePageDestination(it.username!!))
                                        }
                                    },
                                    tagClick = {
                                        navigator.navigate(DiscussionsPageDestination(TagNavArgs.from(it)))
                                    },
                                    click = {
                                        navigator.navigate(DiscussionDetailPageDestination(it.id))
                                    })
                            }
                        }
                    }
                }

            }
        }
    }
}

@Composable
fun <T : Any> PagingDataList(
    items: LazyPagingItems<T>,
    content: @Composable (T) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(count = items.itemCount) { index ->
            items[index]?.let { content(it) }
        }

        items.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    item { Box(Modifier.fillParentMaxSize()) { CircularProgressIndicator(Modifier.align(Alignment.Center)) } }
                }
                loadState.append is LoadState.Loading -> {
                    item { LinearProgressIndicator(Modifier.fillMaxWidth()) }
                }
            }
        }
    }
}