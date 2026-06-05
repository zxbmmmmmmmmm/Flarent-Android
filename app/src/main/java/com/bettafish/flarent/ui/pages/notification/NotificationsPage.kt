package com.bettafish.flarent.ui.pages.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.bettafish.flarent.R
import com.bettafish.flarent.models.Discussion
import com.bettafish.flarent.models.Post
import com.bettafish.flarent.ui.widgets.BackNavigationIcon
import com.bettafish.flarent.ui.widgets.Card
import com.bettafish.flarent.ui.widgets.NotificationItem
import com.bettafish.flarent.utils.LocalUpdatedValueStore.Companion.NotificationIsReadStore
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.DiscussionDetailPageDestination
import com.ramcosta.composedestinations.generated.destinations.PostBottomSheetDestination
import com.ramcosta.composedestinations.generated.destinations.UserProfilePageDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

private const val DefaultNotificationGroupKey = "__default__"

private data class NotificationGroup(
    val key: String,
    val discussion: Discussion?,
    val notifications: MutableList<IndexedValue<com.bettafish.flarent.models.Notification>>,
)

private fun buildNotificationGroups(
    notifications: List<com.bettafish.flarent.models.Notification>
): List<NotificationGroup> {
    val groupedNotifications = mutableListOf<NotificationGroup>()

    notifications.forEachIndexed { index, notification ->
        val discussion = (notification.subject as? Post)?.discussion
        val previousGroup = groupedNotifications.lastOrNull()
        val canAppendToPreviousGroup =
            when {
                previousGroup == null -> false
                discussion == null -> previousGroup.discussion == null
                else -> previousGroup.discussion?.id == discussion.id
            }

        if (canAppendToPreviousGroup) {
            previousGroup?.notifications += IndexedValue(index, notification)
            return@forEachIndexed
        }

        groupedNotifications +=
            NotificationGroup(
                key = discussion?.id?.let { "$it-$index" } ?: "$DefaultNotificationGroupKey-$index",
                discussion = discussion,
                notifications = mutableListOf(IndexedValue(index, notification)),
            )
    }

    return groupedNotifications
}

@Composable
private fun NotificationGroupHeader(
    title: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    Card(
        shape = RoundedCornerShape(12.dp, 12.dp, 2.dp, 2.dp),
        modifier = modifier,
        onClick = onClick
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination<RootGraph>
fun NotificationsPage(
    viewModel: NotificationsViewModel = koinViewModel(),
    navigator: DestinationsNavigator
) {
    val notifications = viewModel.notifications.collectAsLazyPagingItems()
    val groupedNotifications = buildNotificationGroups(notifications.itemSnapshotList.items)

    PullToRefreshBox(
        isRefreshing = notifications.loadState.refresh is LoadState.Loading,
        onRefresh = { notifications.refresh() },
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(stringResource(R.string.notifications)) },
                    navigationIcon = {
                        BackNavigationIcon { navigator.navigateUp() }
                    },
                    actions = {
                        val canMarkAsAll =
                            viewModel.markAllAsReadCommand.canExecute.collectAsState()
                        IconButton(onClick = {
                            viewModel.markAllAsReadCommand.execute(notifications.itemSnapshotList.items)
                        }, enabled = canMarkAsAll.value) {
                            if (canMarkAsAll.value)
                                Icon(Icons.Default.Check, stringResource(R.string.mark_all_as_read))
                            else
                                CircularProgressIndicator(Modifier.padding(8.dp))
                        }
                    }
                )
            }
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                groupedNotifications.forEach { group ->
                    if (group.discussion != null) {
                        item(key = "header-${group.key}") {
                            NotificationGroupHeader(
                                title = group.discussion.title?.takeIf { it.isNotBlank() }
                                    ?: stringResource(R.string.untitled_discussion),
                                modifier = Modifier.padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = 0.dp,
                                    bottom = 1.dp
                                ),
                                onClick = {
                                    navigator.navigate(
                                        DiscussionDetailPageDestination(group.discussion.id)
                                    )
                                },
                            )
                        }
                    }
                    items(
                        count = group.notifications.size,
                        key = { position -> group.notifications[position].value.id },
                    ) { position ->
                        val indexedNotification = group.notifications[position]
                        val notification =
                            notifications[indexedNotification.index] ?: indexedNotification.value
                        val isFirst = position == 0
                        val isLast = position == group.notifications.size - 1
                        NotificationItem(
                            notification = notification,
                            shape =
                                when {
                                    group.discussion != null && isLast -> {
                                        RoundedCornerShape(2.dp, 2.dp, 12.dp, 12.dp)
                                    }

                                    group.discussion != null -> RoundedCornerShape(2.dp)
                                    isFirst && isLast -> RoundedCornerShape(12.dp)
                                    isFirst -> RoundedCornerShape(12.dp, 12.dp, 2.dp, 2.dp)
                                    isLast -> RoundedCornerShape(2.dp, 2.dp, 12.dp, 12.dp)
                                    else -> RoundedCornerShape(2.dp)
                                },
                            modifier =
                                when {
                                    group.discussion != null && isLast -> {
                                        Modifier.padding(16.dp, 1.dp, 16.dp, 16.dp)
                                    }

                                    group.discussion != null -> {
                                        Modifier.padding(horizontal = 16.dp, vertical = 1.dp)
                                    }

                                    isFirst && isLast -> Modifier.padding(
                                        start = 16.dp,
                                        end = 16.dp,
                                        bottom = 16.dp
                                    )

                                    isFirst -> {
                                        Modifier.padding(
                                            start = 16.dp,
                                            end = 16.dp,
                                            top = 16.dp,
                                            bottom = 1.dp
                                        )
                                    }

                                    isLast -> {
                                        Modifier.padding(
                                            start = 16.dp,
                                            end = 16.dp,
                                            top = 1.dp,
                                            bottom = 16.dp
                                        )
                                    }

                                    else -> Modifier.padding(horizontal = 16.dp, vertical = 1.dp)
                                },
                            onClick = {
                                viewModel.markAsRead(notification.id)
                                when (notification.contentType) {
                                    "postMentioned" -> {
                                        val map = notification.content as? Map<*, *>
                                        val replyNumber = map?.get("replyNumber").toString()
                                        val post = notification.subject as Post
                                        navigator.navigate(
                                            PostBottomSheetDestination(
                                                discussionId = post.discussion!!.id,
                                                postNumber = replyNumber,
                                                discussionTitle = post.discussion?.title
                                            )
                                        )
                                    }

                                    "vote" -> {
                                        val post = notification.subject as Post
                                        navigator.navigate(
                                            PostBottomSheetDestination(
                                                postId = post.id,
                                                discussionId = post.discussion!!.id,
                                                discussionTitle = post.discussion?.title
                                            )
                                        )
                                    }

                                    "postReacted" -> {
                                        val post = notification.subject as Post
                                        navigator.navigate(
                                            PostBottomSheetDestination(
                                                postId = post.id,
                                                discussionId = post.discussion!!.id,
                                                discussionTitle = post.discussion?.title
                                            )
                                        )
                                    }

                                    "newFollower" -> {
                                        navigator.navigate(
                                            UserProfilePageDestination(
                                                notification.fromUser!!.username!!,
                                            )
                                        )
                                    }

                                    "postLiked" ->{
                                        val post = notification.subject as Post
                                        navigator.navigate(
                                            PostBottomSheetDestination(
                                                postId = post.id,
                                                discussionId = post.discussion!!.id,
                                                discussionTitle = post.discussion?.title
                                            )
                                        )
                                    }
                                }
                            },
                            userClick = { user ->
                                navigator.navigate(
                                    UserProfilePageDestination(
                                        userName = user.username!!,
                                    )
                                )
                            },
                            postClick = { post ->
                                navigator.navigate(
                                    PostBottomSheetDestination(
                                        postId = post.id,
                                        discussionId = post.discussion!!.id,
                                        discussionTitle = post.discussion?.title
                                    )
                                )
                            }
                        )
                    }
                }
                if (notifications.loadState.append is LoadState.Loading) {
                    item {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}
