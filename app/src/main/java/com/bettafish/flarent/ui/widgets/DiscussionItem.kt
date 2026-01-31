package com.bettafish.flarent.ui.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bettafish.flarent.models.Discussion
import com.bettafish.flarent.models.Tag
import com.bettafish.flarent.models.User
import com.bettafish.flarent.utils.relativeTime
import java.time.ZonedDateTime

private val imageWidth = 40.dp
@Composable
fun DiscussionItem(discussion: Discussion,
                   modifier: Modifier = Modifier,
                   click : (Discussion) -> Unit = {},
                   tagClick : (Tag) -> Unit = {}){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { click(discussion) }
            .padding(16.dp),) {
        Box{
            Avatar(
                avatarUrl = discussion.user?.avatarUrl,
                name = discussion.user?.displayName,
                modifier = Modifier
                    .padding(top = 4.dp,end = 4.dp)
                    .size(imageWidth)
                    .clip(CircleShape)
            )
            discussion.commentCount?.let{
                Badge(
                    containerColor = colorScheme.primary,
                    modifier = Modifier.align(Alignment.TopEnd)){
                    Text(it.toString())
                }
            }
        }
        Column(modifier = Modifier.padding(start = 12.dp)) {
            discussion.title?.let { Text(text = it) }

            val textStyle = MaterialTheme.typography.bodyMedium
            val density = LocalDensity.current
            val textHeightDp = with(density) { textStyle.lineHeight.toDp() }

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.wrapContentWidth()
                ) {
                    val isReply = discussion.lastPostedUser?.id != discussion.user?.id
                    if (isReply) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Reply,
                            contentDescription = null,
                            modifier = Modifier.size(textHeightDp).padding(end = 4.dp),
                            tint = colorScheme.onSurfaceVariant,
                        )
                    }
                    discussion.lastPostedUser?.displayName?.let {
                        Text(
                            text = it,
                            style = textStyle.copy(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                            color = colorScheme.onSurfaceVariant
                        )
                    }
                    discussion.lastPostedAt?.let { lastPostedAt ->
                        val displayTime = remember(lastPostedAt) { (lastPostedAt.relativeTime)}
                        Text(
                            text = displayTime,
                            modifier = Modifier.padding(start = 8.dp),
                            style = textStyle.copy(platformStyle = PlatformTextStyle(includeFontPadding = false)),
                            color = colorScheme.outline
                        )
                    }
                }

                // 触发换行
                Spacer(modifier = Modifier.weight(1f, fill = true))

                discussion.tags?.let {
                    Box(
                        modifier = Modifier
                            .wrapContentWidth().align(Alignment.CenterVertically)
                            .height(IntrinsicSize.Min)
                    ) {
                        TagList(it, click = tagClick)
                    }
                }
            }

        }
    }
}

@Preview
@Composable
fun DiscussionItemPreview() {
    val discussion: Discussion = Discussion().apply {
        title = "Discussion Title"
        slug = "discussion-slug"
        commentCount = 10
        participantCount = 5
        lastPostedUser = User().apply {
            displayName = "John Doe"
            username = "AA"
            id = "1"
        }
        lastPostedAt = ZonedDateTime.now()
        tags = listOf(
            Tag().apply {
                name = "PC"
                slug = "tag-1"
            },
            Tag().apply {
                name = "Mobile"
                slug = "tag-2"
            }
        )
    }
    DiscussionItem(discussion)
}

