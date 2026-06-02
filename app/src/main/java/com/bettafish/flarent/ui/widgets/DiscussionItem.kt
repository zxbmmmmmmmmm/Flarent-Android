package com.bettafish.flarent.ui.widgets

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bettafish.flarent.models.Discussion
import com.bettafish.flarent.models.Tag
import com.bettafish.flarent.models.User
import com.bettafish.flarent.ui.theme.FlarentTheme
import com.bettafish.flarent.utils.LocalUpdatedValueStore.Companion.DiscussionLastReadPostNumberStore
import com.bettafish.flarent.utils.relativeTime
import java.time.ZonedDateTime

private val imageWidth = 40.dp

@Composable
fun DiscussionItem(
    viewModel: DiscussionItemViewModel,
    modifier: Modifier = Modifier,
    click: (Discussion) -> Unit = {},
    tagClick: (Tag) -> Unit = {},
    userClick: (User) -> Unit = {}
) {
    val discussion = viewModel.discussion.collectAsState()

    discussion.value?.let {
        DiscussionItem(
            discussion = it,
            modifier = modifier,
            click = click,
            tagClick = tagClick,
            userClick = userClick
        )
    }
}

@Composable
private fun DiscussionItem(
    discussion: Discussion,
    modifier: Modifier = Modifier,
    click: (Discussion) -> Unit = {},
    tagClick: (Tag) -> Unit = {},
    userClick: (User) -> Unit = {}
) {
    val localLastReadPostNumber = DiscussionLastReadPostNumberStore
        .observe(discussion.id)
        .collectAsState(initial = null)
    val displayedLastReadPostNumber = localLastReadPostNumber.value ?: discussion.lastReadPostNumber
    val hasUnreadPosts =
        displayedLastReadPostNumber != null && discussion.lastPostNumber!! - displayedLastReadPostNumber > 0
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                displayedLastReadPostNumber?.let { discussion.lastReadPostNumber = it }
                click(discussion)
            }
            .padding(16.dp)
            .alpha(if (discussion.isHidden == true) 0.38f else 1f),
    ) {
        Box {
            discussion.user?.let {
                Avatar(
                    avatarUrl = it.avatarUrl,
                    name = it.displayName ?: it.username,
                    modifier = Modifier
                        .padding(top = 4.dp, end = 4.dp)
                        .size(imageWidth)
                        .clip(CircleShape)
                        .clickable { userClick(it) }
                )
            }
            if (hasUnreadPosts) {
                val badgeText = discussion.lastPostNumber!! - displayedLastReadPostNumber
                Badge(
                    containerColor = colorScheme.primary,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(badgeText.toString())
                }
            }
        }
        val density = LocalDensity.current

        Column(
            modifier = Modifier.padding(start = 12.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            discussion.title?.let {
                val annotatedString = buildAnnotatedString {
                    if (discussion.isSticky == true) {
                        appendInlineContent("pinned")
                    }
                    if (discussion.frontpage == true) {
                        appendInlineContent("front")
                    }
                    if (discussion.isLocked == true) {
                        appendInlineContent("locked")
                    }
                    if (discussion.hasBestAnswer == true) {
                        appendInlineContent("hasBestAnswer")
                    }
                    if (discussion.isHidden == true) {
                        appendInlineContent("hidden")
                    }
                    append(it)
                }
                val textMeasurer = androidx.compose.ui.text.rememberTextMeasurer()
                val badgeStyle = MaterialTheme.typography.bodySmall

                fun getAutoInlineContent(
                    text: String,
                    containerColor: Color,
                    contentColor: Color
                ): InlineTextContent {
                    val textLayoutResult = textMeasurer.measure(text, badgeStyle)
                    val width =
                        with(density) { (textLayoutResult.size.width.toDp() + 20.dp).toSp() }
                    val height =
                        with(density) { (textLayoutResult.size.height.toDp() + 4.dp).toSp() }

                    return InlineTextContent(
                        Placeholder(
                            width = width,
                            height = height,
                            placeholderVerticalAlign = PlaceholderVerticalAlign.Center
                        )
                    ) {
                        InlineTag(text, containerColor, contentColor)
                    }
                }

                val inlineContent = mapOf(
                    "pinned" to getAutoInlineContent(
                        "置顶",
                        colorScheme.secondaryContainer,
                        colorScheme.secondary
                    ),
                    "front" to getAutoInlineContent(
                        "精",
                        colorScheme.errorContainer,
                        colorScheme.onErrorContainer
                    ),
                    "locked" to getAutoInlineContent(
                        "已锁定",
                        colorScheme.surfaceContainerHighest,
                        colorScheme.onSurface
                    ),
                    "hasBestAnswer" to getAutoInlineContent(
                        "已有最佳回复",
                        colorScheme.surfaceContainerHighest,
                        colorScheme.onSurface
                    ),
                    "hidden" to getAutoInlineContent(
                        "隐藏",
                        colorScheme.surfaceContainerHighest,
                        colorScheme.onSurface
                    )
                )
                Text(
                    text = annotatedString,
                    inlineContent = inlineContent,
                    style = if (hasUnreadPosts)
                        MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                    else MaterialTheme.typography.bodyLarge,
                    color = if (hasUnreadPosts) colorScheme.onSurface else colorScheme.outline,
                )
            }

            val textStyle = MaterialTheme.typography.bodyMedium
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
                            modifier = Modifier
                                .size(textHeightDp)
                                .padding(end = 4.dp),
                            tint = if (hasUnreadPosts) colorScheme.onSurfaceVariant else colorScheme.outline,
                        )
                    }
                    discussion.lastPostedUser?.displayName?.let {
                        Text(
                            text = it,
                            style = textStyle.copy(
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                )
                            ),
                            color = if (hasUnreadPosts) colorScheme.onSurfaceVariant else colorScheme.outline,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .weight(1f, fill = false)
                        )
                    }
                    discussion.lastPostedAt?.let { lastPostedAt ->
                        val displayTime = remember(lastPostedAt) { (lastPostedAt.relativeTime) }
                        Text(
                            text = displayTime,
                            modifier = Modifier.padding(start = 8.dp),
                            style = textStyle.copy(
                                platformStyle = PlatformTextStyle(
                                    includeFontPadding = false
                                )
                            ),
                            color = colorScheme.outline
                        )
                    }
                }

                // 触发换行
                Spacer(modifier = Modifier.weight(1f, fill = true))
                Row(modifier = Modifier.alpha(if (hasUnreadPosts) 1f else 0.6f),) {
                    discussion.lastPostNumber?.let {
                        Row {
                            val bg = colorScheme.surfaceContainerHighest
                            Canvas(
                                modifier = modifier
                                    .width(3.dp)
                                    .height(4.dp)
                                    .align(Alignment.Bottom)
                            ) {
                                val path = Path().apply {
                                    moveTo(0f, size.height)
                                    lineTo(size.width, 0f)
                                    lineTo(size.width, size.height)
                                    close()
                                }
                                drawPath(path, color = bg)
                            }
                            Surface(
                                color = bg,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .clip(RoundedCornerShape(4.dp, 4.dp, 4.dp, 0.dp))
                            )
                            {
                                if (it / 10 > 0) {
                                    Text(
                                        text = it.toString(),
                                        modifier = Modifier.padding(4.dp, 1.dp),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                } else {
                                    Text(
                                        text = it.toString(),
                                        modifier = Modifier.padding(5.dp, 1.dp),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                    }
                    discussion.tags?.let {
                        Box(
                            modifier = Modifier
                                .wrapContentWidth()
                                .align(Alignment.CenterVertically)
                                .height(IntrinsicSize.Min)
                                .padding(start = 4.dp)
                        ) {
                            TagList(it, click = tagClick)
                        }
                    }

                }


            }

        }
    }
}

@Composable
fun InlineTag(
    text: String,
    containerColor: Color = colorScheme.surfaceContainerHighest,
    contentColor: Color = colorScheme.onSurface
) {
    Box(
        modifier = Modifier
            .padding(end = 6.dp)
            .fillMaxSize()
            .background(containerColor, RoundedCornerShape(4.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = contentColor, style = MaterialTheme.typography.bodySmall)
    }
}

@Preview(showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DiscussionItemPreview() {
    val discussion: Discussion = Discussion().apply {
        id = "discussion-1"
        title = "Discussion Title"
        slug = "discussion-slug"
        commentCount = 10
        participantCount = 5
        lastPostNumber = 24
        lastReadPostNumber = 22
        isSticky = true
        frontpage = true
        hasBestAnswer = true
        isLocked = true
        lastPostedUser = User().apply {
            displayName = "John Doe"
            username = "AA"
            id = "1"
        }
        user = User().apply {
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
    FlarentTheme {
        Surface {
            DiscussionItem(discussion)
        }
    }
}

