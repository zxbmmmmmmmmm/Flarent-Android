package com.bettafish.flarent.ui.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Grid
import androidx.compose.foundation.layout.GridTrackSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.AddReaction
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbsUpDown
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bettafish.flarent.models.Notification
import com.bettafish.flarent.models.Post
import com.bettafish.flarent.models.User
import com.bettafish.flarent.utils.LocalUpdatedValueStore.Companion.DiscussionLastReadPostNumberStore
import com.bettafish.flarent.utils.LocalUpdatedValueStore.Companion.NotificationIsReadStore
import com.bettafish.flarent.utils.relativeTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import java.time.ZonedDateTime

@OptIn(ExperimentalGridApi::class)
@Composable
fun NotificationItem(
    notification: Notification,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    userClick: (User) -> Unit = {},
    postClick: (Post) -> Unit = {},
    shape: RoundedCornerShape = RoundedCornerShape(2.dp)
) {
    val isRead = NotificationIsReadStore
        .observe(notification.id)
        .collectAsState(initial = null)
    Card(
        shape = shape,
        onClick = { onClick() },
        modifier = modifier
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Avatar(
                    avatarUrl = notification.fromUser?.avatarUrl,
                    name = notification.fromUser?.displayName,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .height(40.dp)
                        .width(40.dp)
                        .clip(CircleShape)
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp),
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text(
                            text = notification.fromUser?.displayName
                                ?: notification.fromUser?.username
                                ?: "",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                        )
                        Text(
                            text = notification.createdAt?.relativeTime ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        if(!(isRead.value == true || notification.isRead == true)){
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(8.dp).align(Alignment.CenterVertically)
                            ) { }
                        }
                    }
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.Center,
                    ) {

                        Icon(
                            when (notification.contentType) {
                                "postMentioned" -> Icons.AutoMirrored.Filled.Reply
                                "vote" -> Icons.Default.ThumbsUpDown
                                "postReacted" -> Icons.Default.AddReaction
                                "newFollower" -> Icons.Default.PersonAdd
                                "newPostByUser" -> Icons.Default.Person
                                "userMentioned" -> Icons.Default.AlternateEmail
                                "newPost" -> Icons.Default.Star
                                else -> Icons.Default.Notifications
                            },
                            null,
                            modifier = Modifier
                                .padding(2.dp)
                                .height(16.dp)
                        )
                        val textStyle = MaterialTheme.typography.bodyMedium
                        CompositionLocalProvider(
                            LocalTextStyle provides textStyle,

                            ) {
                            when (notification.contentType) {
                                "postMentioned" -> Text("回复了你")
                                "vote" -> Text(if (notification.content == 1) "赞同了你的帖子" else "反对了你的帖子")
                                "postReacted" -> {
                                    val jsonObject: JsonObject =
                                        Json.decodeFromString(notification.content.toString())
                                    val emoji =
                                        if (jsonObject["type"].toString() == "\"emoji\"") {
                                            val identifier = jsonObject["identifier"].toString()
                                            getEmoji(
                                                identifier.substring(
                                                    1,
                                                    identifier.length - 1
                                                )
                                            )
                                        } else {
                                            jsonObject["display"].toString()
                                        }
                                    Text(
                                        text = "戳了一个 $emoji"
                                    )
                                }

                                "newFollower" -> Text("关注了你")
                                "newPostByUser" -> Text("发表回复")
                                "userMentioned" -> Text("提及了你")
                                "newPost" -> Text("回复了你关注的主题")
                                else -> Text(notification.contentType.toString())
                            }
                        }
                    }
                }
            }

            (notification.subject as? Post)?.text?.let {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceContainerHighest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { postClick(notification.subject as Post) }) {
                    Text(
                        it,
                        softWrap = true,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp, 8.dp)
                    )
                }
            }
        }

    }


}

@Preview(showBackground = true)
@Composable
fun NotificationItemPreview() {
    NotificationItem(notification = Notification().apply {
        id = "1"
        contentType = "postMentioned"
        content = "This is a notification content"
        isRead = false
        createdAt = ZonedDateTime.now()
        fromUser = User().apply {
            displayName = "John Doe"
            username = "AA"
            id = "1"
        }
        subject = Post().apply {
            id = "1"
            text =
                "Lorem ipsum dolor sit amet, consectetur adipisicing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua"
        }
    })
}

@Preview(showBackground = true)
@Composable
fun NotificationItemPreview2() {
    NotificationItem(notification = Notification().apply {
        id = "1"
        contentType = "postMentioned"
        content = "This is a notification content"
        isRead = false
        createdAt = ZonedDateTime.now()
        fromUser = User().apply {
            displayName = "John Doe"
            username = "AA"
            id = "1"
        }
        subject = Post().apply {
            id = "1"
            text = "SupercalifragilisticexpialidociousSupercalifragilisticexpialidocious"
        }
    }, modifier = Modifier.padding(12.dp))
}
