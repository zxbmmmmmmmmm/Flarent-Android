package com.bettafish.flarent.ui.pages.notification

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
private const val DefaultNotificationGroupTitle = "其他通知"

private data class NotificationGroup(
    val key: String,
    val title: String,
    val notifications: List<IndexedValue<com.bettafish.flarent.models.Notification>>,
)

private fun buildNotificationGroups(
    notifications: List<com.bettafish.flarent.models.Notification>
): List<NotificationGroup> {
    val groupedNotifications = LinkedHashMap<String, MutableList<IndexedValue<com.bettafish.flarent.models.Notification>>>()
    val titles = LinkedHashMap<String, String>()

    notifications.forEachIndexed { index, notification ->
        val discussion = (notification.subject as? Post)?.discussion
        val key = discussion?.id ?: DefaultNotificationGroupKey
        val title = discussion?.title?.takeIf { it.isNotBlank() } ?: DefaultNotificationGroupTitle

        groupedNotifications.getOrPut(key) { mutableListOf() }.add(IndexedValue(index, notification))
        titles.putIfAbsent(key, title)
    }

    return groupedNotifications.map { (key, items) ->
        NotificationGroup(
            key = key,
            title = titles[key] ?: DefaultNotificationGroupTitle,
            notifications = items,
        )
    }
}

@Composable
private fun NotificationGroupHeader(title: String) {
    Card(
        shape = RoundedCornerShape(4.dp,4.dp,0.dp,0.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination<RootGraph>
fun NotificationsPage(
    viewModel: NotificationsViewModel = koinViewModel(),
    navigator: DestinationsNavigator) {
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
    )  {innerPadding->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)) {
            groupedNotifications.forEach { group ->
                item(key = "header-${group.key}") {
                    NotificationGroupHeader(group.title)
                }

                items(
                    count = group.notifications.size,
                    key = { position -> group.notifications[position].value.id },
                ) { position ->
                    val indexedNotification = group.notifications[position]
                    val notification = notifications[indexedNotification.index] ?: indexedNotification.value

                    NotificationItem(
                        notification = notification,
                        modifier = Modifier
                            .clickable {
                                when (notification.contentType) {
                                    "postMentioned" -> {
                                        val map = notification.content as? Map<*, *>
                                        val replyNumber = map?.get("replyNumber").toString()
                                        val post = notification.subject as Post
                                        navigator.navigate(
                                            DiscussionDetailPageDestination(
                                                discussionId = post.discussion!!.id,
                                                targetPosition = replyNumber.toIntOrNull() ?: post.number ?: 0,
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
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
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