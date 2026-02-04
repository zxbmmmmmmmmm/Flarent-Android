package com.bettafish.flarent.ui.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import coil3.compose.AsyncImage
import com.bettafish.flarent.R
import com.bettafish.flarent.models.Post
import com.bettafish.flarent.models.User
import com.bettafish.flarent.utils.relativeTime
import dev.snipme.highlights.Highlights
import dev.snipme.highlights.model.SyntaxThemes
import java.time.ZonedDateTime


@Composable
fun PostItem(
    post: Post,
    modifier: Modifier = Modifier,
    isOp: Boolean = false,
    userClick: (String) -> Unit = {  }
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { post.user?.username?.let{ username -> userClick(username) } },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Avatar(
                avatarUrl = post.user?.avatarUrl,
                name = post.user?.displayName,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )

            Column(modifier = Modifier.padding(start = 12.dp)) {
                Row{
                    Text(
                        text = post.user?.displayName ?: post.user?.username ?: "",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
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


                }

                Row{
                    post.createdAt?.let {
                        val displayTime = remember(it) { it.relativeTime }
                        Text(
                            text = displayTime,
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorScheme.outline,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                    post.number?.let {
                        Text(
                            text = "#$it",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorScheme.outline,
                        )
                    }
                }
            }
        }

        // Content
        post.contentMarkdown?.let {
            Markdown(it,
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .fillMaxWidth(),
                color = colorScheme.onBackground)
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
        displayName = "User"
        username = "user"
        avatarUrl = null
    }
    val samplePost = Post().apply {
        user = sampleUser
        createdAt = ZonedDateTime.now().minusHours(1)
        number = 2
        contentMarkdown =  """
### Hello Markdown

This is a simple markdown example with:

- Bullet points
- **Bold text**
- *Italic text*

[Check out this link](https://github.com/mikepenz/multiplatform-markdown-renderer)
"""
    }

    MaterialTheme {
        PostItem(post = samplePost, isOp = true)
    }
}
