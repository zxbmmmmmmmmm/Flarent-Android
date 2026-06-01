package com.bettafish.flarent.ui.pages.notification

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.bettafish.flarent.models.Post
import com.bettafish.flarent.ui.widgets.BackNavigationIcon
import com.bettafish.flarent.ui.widgets.Card
import com.bettafish.flarent.ui.widgets.NotificationItem
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.DiscussionDetailPageDestination
import com.ramcosta.composedestinations.generated.destinations.UserProfilePageDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel

private const val DefaultNotificationGroupKey = "__default__"

private data class NotificationGroup(
    val key: String,
    val title: String,
    val showHeader: Boolean,
    val notifications: MutableList<IndexedValue<com.bettafish.flarent.models.Notification>>,
)

private fun buildNotificationGroups(
    notifications: List<com.bettafish.flarent.models.Notification>
): List<NotificationGroup> {
    val groupedNotifications = mutableListOf<NotificationGroup>()
    val discussionGroupIndices = LinkedHashMap<String, Int>()
    var defaultGroupCounter = 0
    var activeDefaultGroupIndex: Int? = null

    notifications.forEachIndexed { index, notification ->
        val discussion = (notification.subject as? Post)?.discussion

        if (discussion == null) {
            val groupIndex =
                activeDefaultGroupIndex ?: groupedNotifications.size.also {
                    groupedNotifications +=
                        NotificationGroup(
                            key = "$DefaultNotificationGroupKey-${defaultGroupCounter++}",
                            title = "",
                            showHeader = false,
                            notifications = mutableListOf(),
                        )
                }

            groupedNotifications[groupIndex].notifications += IndexedValue(index, notification)
            activeDefaultGroupIndex = groupIndex
            return@forEachIndexed
        }

        activeDefaultGroupIndex = null
        val groupIndex =
            discussionGroupIndices[discussion.id] ?: groupedNotifications.size.also {
                groupedNotifications +=
                    NotificationGroup(
                        key = discussion.id,
                            title = discussion.title?.takeIf { it.isNotBlank() } ?: "未命名讨论",
                        showHeader = true,
                        notifications = mutableListOf(),
                    )
                discussionGroupIndices[discussion.id] = it
            }

        groupedNotifications[groupIndex].notifications += IndexedValue(index, notification)
    }

    return groupedNotifications
}

@Composable
private fun NotificationGroupHeader(title: String, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(12.dp, 12.dp, 2.dp, 2.dp),
        modifier = modifier) {
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("通知") },
                navigationIcon = {
                    BackNavigationIcon { navigator.navigateUp() }
                },
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            groupedNotifications.forEach { group ->
                if (group.showHeader) {
                    item(key = "header-${group.key}") {
                        NotificationGroupHeader(
                            group.title,
                            modifier = Modifier.padding(
                                start = 16.dp,
                                end = 16.dp,
                                top = 0.dp,
                                bottom = 1.dp
                            )
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
                                group.showHeader && isLast -> {
                                    RoundedCornerShape(2.dp, 2.dp, 12.dp, 12.dp)
                                }

                                group.showHeader -> RoundedCornerShape(2.dp)
                                isFirst && isLast -> RoundedCornerShape(12.dp)
                                isFirst -> RoundedCornerShape(12.dp, 12.dp, 2.dp, 2.dp)
                                isLast -> RoundedCornerShape(2.dp, 2.dp, 12.dp, 12.dp)
                                else -> RoundedCornerShape(2.dp)
                            },
                        modifier =
                            when {
                                group.showHeader && isLast -> {
                                    Modifier.padding(16.dp, 1.dp, 16.dp, 16.dp)
                                }

                                group.showHeader -> {
                                    Modifier.padding(horizontal = 16.dp, vertical = 1.dp)
                                }

                                isFirst && isLast -> Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                                isFirst -> {
                                    Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 1.dp)
                                }

                                isLast -> {
                                    Modifier.padding(start = 16.dp, end = 16.dp, top = 1.dp, bottom = 16.dp)
                                }

                                else -> Modifier.padding(horizontal = 16.dp, vertical = 1.dp)
                            },
                        onClick = {
                            when (notification.contentType) {
                                "postMentioned" -> {
                                    val map = notification.content as? Map<*, *>
                                    val replyNumber = map?.get("replyNumber").toString()
                                    val post = notification.subject as Post
                                    navigator.navigate(
                                        DiscussionDetailPageDestination(
                                            discussionId = post.discussion!!.id,
                                            targetPosition = replyNumber.toIntOrNull()
                                                ?: post.number ?: 0,
                                        )
                                    )
                                }

                                "vote" -> {
                                    val post = notification.subject as Post
                                    navigator.navigate(
                                        DiscussionDetailPageDestination(
                                            post.discussion!!.id,
                                            post.number ?: 0,
                                        )
                                    )
                                }

                                "postReacted" -> {
                                    val post = notification.subject as Post
                                    navigator.navigate(
                                        DiscussionDetailPageDestination(
                                            post.discussion!!.id,
                                            post.number ?: 0,
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
                                DiscussionDetailPageDestination(
                                    discussionId = post.discussion!!.id,
                                    targetPosition = post.number ?: 0,
                                )
                            )
                        }
                    )
                }
            }
            if (notifications.loadState.refresh is LoadState.Loading) {
                item {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
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