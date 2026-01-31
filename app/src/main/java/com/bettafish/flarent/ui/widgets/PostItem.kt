package com.bettafish.flarent.ui.widgets

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bettafish.flarent.R
import com.bettafish.flarent.models.Post
import com.bettafish.flarent.models.User
import com.bettafish.flarent.utils.relativeTime
import com.mikepenz.markdown.coil2.Coil2ImageTransformerImpl
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.compose.elements.MarkdownHighlightedCodeBlock
import com.mikepenz.markdown.compose.elements.MarkdownHighlightedCodeFence
import com.mikepenz.markdown.m3.Markdown
import com.vladsch.flexmark.html.HtmlRenderer
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter
import com.vladsch.flexmark.parser.Parser
import com.vladsch.flexmark.util.data.MutableDataSet
import dev.snipme.highlights.Highlights
import dev.snipme.highlights.model.SyntaxThemes
import java.time.ZonedDateTime


@Composable
fun PostItem(
    post: Post,
    modifier: Modifier = Modifier,
    isOp: Boolean = false,
    onReplyClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Avatar(
                avatarUrl = post.user?.avatarUrl,
                name = post.user?.displayName,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )

            Text(
                text = post.user?.displayName ?: post.user?.username ?: "",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 12.dp)
            )

            if (isOp) {
                Surface(
                    color = colorScheme.primary,
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = stringResource(R.string.op_badge),
                        color = colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            post.createdAt?.let {
                val displayTime = remember(it) { it.relativeTime }
                Text(
                    text = displayTime,
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.outline,
                    modifier = Modifier.padding(start = 12.dp)
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            post.number?.let {
                Text(
                    text = "#$it",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorScheme.outline
                )
            }
        }
        post.contentHtml?.let {
            val converter = FlexmarkHtmlConverter.builder().build()
            val markdown = converter.convert(it)
            val isDarkTheme = isSystemInDarkTheme()
            val highlightsBuilder = remember(isDarkTheme) {
                Highlights.Builder().theme(SyntaxThemes.atom(darkMode = isDarkTheme))
            }
            Markdown(markdown,imageTransformer = Coil2ImageTransformerImpl,
                components = markdownComponents(
                    codeBlock = {
                        MarkdownHighlightedCodeBlock(
                            content = it.content,
                            node = it.node,
                            highlightsBuilder = highlightsBuilder,
                            showHeader = true, // optional enable header with code language + copy button
                        )
                    },
                    codeFence = {
                        MarkdownHighlightedCodeFence(
                            content = it.content,
                            node = it.node,
                            highlightsBuilder = highlightsBuilder,
                            showHeader = true, // optional enable header with code language + copy button
                        )
                    },))
        }

        // Footer Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Reply,
                    tint = colorScheme.outline,
                    contentDescription = null)
                Text(
                    text = stringResource(R.string.reply),
                    color = colorScheme.outline,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            Icon(Icons.Default.MoreHoriz,
                tint = colorScheme.outline,
                contentDescription =  null)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PostItemPreview() {
    val sampleUser = User().apply {
        displayName = "xkai"
        username = "xkai"
        avatarUrl = null
    }
    val samplePost = Post().apply {
        user = sampleUser
        createdAt = ZonedDateTime.now().minusHours(1)
        number = 2
        contentHtml =  """
<h3>Hello Markdown</h3>
<p>This is a simple markdown example with:</p>
<ul>
<li>Bullet points</li>
<li><strong>Bold text</strong></li>
<li><em>Italic text</em></li>
</ul>
<p><a href="https://github.com/mikepenz/multiplatform-markdown-renderer">Check out this link</a></p>
"""
    }

    MaterialTheme {
        PostItem(post = samplePost, isOp = true)
    }
}
