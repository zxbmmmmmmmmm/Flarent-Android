package com.bettafish.flarent.ui.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Grid
import androidx.compose.foundation.layout.GridTrackSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material.icons.filled.ThumbsUpDown
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bettafish.flarent.models.Notification
import com.bettafish.flarent.models.Post
import com.bettafish.flarent.models.User
import com.bettafish.flarent.utils.relativeTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import java.time.ZonedDateTime

private const val LongWordWrapChunkSize = 16
private const val ZeroWidthSpace = "\u200B"

private fun String.withWrapOpportunities(chunkSize: Int = LongWordWrapChunkSize): String {
    if (length <= chunkSize) return this

    val builder = StringBuilder(length + length / chunkSize)
    var consecutiveNonWhitespace = 0

    codePoints().forEach { codePoint ->
        builder.appendCodePoint(codePoint)

        if (Character.isWhitespace(codePoint)) {
            consecutiveNonWhitespace = 0
        } else {
            consecutiveNonWhitespace++
            if (consecutiveNonWhitespace >= chunkSize) {
                builder.append(ZeroWidthSpace)
                consecutiveNonWhitespace = 0
            }
        }
    }

    return builder.toString()
}

@OptIn(ExperimentalGridApi::class)
@Composable
fun NotificationItem(
    notification: Notification,
    modifier: Modifier = Modifier,
    userClick: (User) -> Unit = {},
    postClick: (Post) -> Unit = {}
) {
    Grid(config = {
        repeat(3) {
            row(GridTrackSize.Auto)
        }
        column(GridTrackSize.Auto)
        column(1.fr)
        rowGap(2.dp)
        columnGap(12.dp)
    }, modifier = modifier.fillMaxWidth()) {
        Avatar(
            avatarUrl = notification.fromUser?.avatarUrl,
            name = notification.fromUser?.displayName,
            modifier = Modifier
                .height(28.dp)
                .width(28.dp)
                .clip(CircleShape)
                .clickable { userClick(notification.fromUser!!) },
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxHeight()
        ) {

            Text(
                text = notification.fromUser?.displayName ?: notification.fromUser?.username ?: "",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .clickable { userClick(notification.fromUser!!) })
            Text(
                text = notification.createdAt?.relativeTime ?: "",
                color = MaterialTheme.colorScheme.outline
            )
        }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.gridItem(row = 2, column = 2)
        ) {
            val density = LocalDensity.current
            val textStyle = MaterialTheme.typography.bodyLarge
            val textHeightDp = with(density) { textStyle.lineHeight.toDp() }
            Icon(
                when (notification.contentType) {
                    "postMentioned" -> Icons.AutoMirrored.Filled.Reply
                    "vote" -> Icons.Default.ThumbsUpDown
                    "postReacted" -> Icons.Default.AddReaction
                    "newFollower" -> Icons.Default.PersonAdd
                    "newPostByUser" -> Icons.Default.Person
                    "userMentioned" -> Icons.Default.AlternateEmail
                    else -> Icons.Default.Notifications
                },
                null,
                modifier = Modifier
                    .height(textHeightDp * 0.8F)
                    .align(Alignment.CenterVertically)
            )
            CompositionLocalProvider(
                LocalTextStyle provides textStyle
            ) {
                when (notification.contentType) {
                    "postMentioned" -> Text("回复了你")
                    "vote" -> Text(if (notification.content == 1) "赞同了你的帖子" else "反对了你的帖子")
                    "postReacted" -> {
                        val jsonObject: JsonObject =
                            Json.decodeFromString(notification.content.toString())
                        val emoji = if (jsonObject["type"].toString() == "\"emoji\"") {
                            val identifier = jsonObject["identifier"].toString()
                            getEmoji(identifier.substring(1, identifier.length - 1))
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
                    else -> Text(notification.contentType.toString())
                }
            }
        }

        (notification.subject as? Post)?.text?.let {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier
                    .gridItem(row = 3, column = 2)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { postClick(notification.subject as Post) }) {
                Text(
                    it.withWrapOpportunities(),
                    color = MaterialTheme.colorScheme.outline,
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
    }, modifier = Modifier.padding(12.dp))
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
