package com.bettafish.flarent.ui.widgets.post

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CallSplit
import androidx.compose.material.icons.automirrored.filled.Label
import androidx.compose.material.icons.automirrored.filled.Reply
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Merge
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.AddReaction
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bettafish.flarent.App
import com.bettafish.flarent.R
import com.bettafish.flarent.models.Post
import com.bettafish.flarent.models.User
import com.bettafish.flarent.ui.widgets.Avatar
import com.bettafish.flarent.ui.widgets.LocalImagePreviewer
import com.bettafish.flarent.ui.widgets.ReactionList
import com.bettafish.flarent.utils.ClickableCoil3ImageTransformer
import com.bettafish.flarent.utils.appSettings
import com.bettafish.flarent.utils.relativeTime
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.compose.elements.MarkdownHighlightedCodeBlock
import com.mikepenz.markdown.compose.elements.MarkdownHighlightedCodeFence
import com.mikepenz.markdown.m3.Markdown
import com.mikepenz.markdown.model.rememberMarkdownState
import com.ramcosta.composedestinations.generated.destinations.ReplyBottomSheetDestination
import com.ramcosta.composedestinations.generated.destinations.UserProfilePageDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.snipme.highlights.Highlights
import dev.snipme.highlights.model.SyntaxThemes
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import java.time.ZonedDateTime

val reactions = App.INSTANCE.appSettings.forum?.reactions?.associateBy { it.id }

@Composable
fun PostItem(
    initPost: Post?,
    postId: String?,
    navigator: DestinationsNavigator,
    modifier: Modifier = Modifier,
    userClickEnabled: Boolean = true,
    isOp: Boolean = false,
) {
    val id = initPost?.id ?: postId!!
    val vm: PostItemViewModel = getViewModel( key = id ){ parametersOf(id, initPost) }
    val post = vm.post.collectAsState()
    val imagePreviewer = LocalImagePreviewer.current
    val canVoteCommandExec = vm.voteCommand.canExecute.collectAsState()

    Box(modifier = Modifier.fillMaxSize()){
        if(post.value != null){
            PostItem(post.value!!,
                modifier,
                isOp,
                userClickEnabled = userClickEnabled,
                userClick = { navigator.navigate(UserProfilePageDestination(it)) },
                imageClick = { url-> imagePreviewer(listOf(url),0) },
                replyClick = { name, postId ->
                    post.value?.discussion?.id?.let {
                        val content = "@\"$name\"#p$postId "
                        navigator.navigate(ReplyBottomSheetDestination(it, content = content))
                    }},
                onVote = { postId, isUpvoted, isDownvoted ->
                    vm.voteCommand.execute(postId, isUpvoted, isDownvoted)
                },
                isVoting = !canVoteCommandExec.value)
        }
        else{
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
    }

}

@Composable
private fun PostItem(
    post: Post,
    modifier: Modifier = Modifier,
    isOp: Boolean = false,
    userClickEnabled: Boolean = true,
    userClick: (username: String) -> Unit = {  },
    imageClick: ((String) -> Unit) = {},
    replyClick: (name: String, postId:String) -> Unit = { _,_ -> },
    onVote: (postId: String, isUpvoted: Boolean, isDownvoted: Boolean) -> Unit = { _,_,_ -> },
    isVoting: Boolean = false
) {

    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        val isComment = post.contentType == "comment"
        // Header
        if(isComment){

            val rowModifier = if(userClickEnabled){
                Modifier.fillMaxWidth().clickable { post.user?.username?.let { username -> userClick(username) } }
            }
            else{
                Modifier.fillMaxWidth()
            }

            Row(
                modifier = rowModifier,
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
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        if (isOp) {
                            Surface(
                                color = colorScheme.primary,
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.padding(start = 8.dp).align(Alignment.CenterVertically)
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
        }
        // Content
        if(!isComment){

            val contentTextColor = colorScheme.outline
            val contentTextStyle =MaterialTheme.typography.titleSmall

            var icon = Icons.Default.Menu
            var detailsContent: @Composable () -> Unit = {}

            when(post.contentType){
                "discussionRenamed" -> {
                    icon = Icons.Default.EditNote
                    detailsContent = {
                        val arr = post.content as? List<*>
                        arr?.get(0)?.toString()?.let{
                            Text("更改标题为",
                                style = contentTextStyle,
                                color = contentTextColor)
                            Text(it,
                                style = contentTextStyle,
                                textDecoration = TextDecoration.LineThrough,
                                color = contentTextColor)
                            Text(it,
                                style = contentTextStyle,
                                color = contentTextColor)
                        }
                    }
                }
                "discussionTagged" -> {
                    icon = Icons.AutoMirrored.Filled.Label
                    detailsContent = {
                        Text("修改了标签",
                            style = contentTextStyle,
                            color = contentTextColor)
                    }
                }
                "discussionSplit" -> {
                    icon = Icons.AutoMirrored.Filled.CallSplit
                    detailsContent = {
                        val map = post.content as? LinkedHashMap<*, *>
                        val title = map?.get("title")?.toString() ?: "未知主题"
                        val count = map?.get("count") ?: 0
                        val url = map?.get("url")?.toString() ?: ""

                        val annotatedString = buildAnnotatedString {
                            append("从 ")

                            withLink(
                                LinkAnnotation.Url(
                                    url = url,
                                )
                            ) {
                                append(title)
                            }

                            append(" 拆分来 $count 个回复")
                        }

                        Text(
                            text = annotatedString,
                            style = contentTextStyle,
                            color = contentTextColor
                        )
                    }
                }
                "discussionMerged" -> {
                    icon = Icons.Default.Merge
                    detailsContent = {
                        val map = post.content as? LinkedHashMap<*,*>
                        val count = map?.get("count")
                        val titles = map?.get("titles") as? ArrayList<*>
                        val titlesString = titles?.joinToString(separator = ", ") ?: "未知主题"
                        Text("合并主题 $titlesString 下的 $count 个回复",
                            style = contentTextStyle,
                            color = contentTextColor)
                    }
                }

                "discussionStickied" -> {
                    icon = Icons.Default.PushPin
                    detailsContent = {
                        val map = post.content as? LinkedHashMap<*,*>
                        val isSticky = map?.get("sticky") as? Boolean
                        Text(if(isSticky == true) "置顶此贴" else "取消置顶",
                            style = contentTextStyle,
                            color = contentTextColor)
                    }
                }

                "discussionLocked" -> {
                    val map = post.content as? LinkedHashMap<*,*>
                    val locked = map?.get("locked") as? Boolean
                    icon = if(locked == true) Icons.Filled.Lock else Icons.Filled.LockOpen

                    detailsContent = {
                        Text(if(locked == true) "锁定此贴" else "取消锁定",
                            style = contentTextStyle,
                            color = contentTextColor)
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(colorScheme.surfaceContainer)
                    .padding(12.dp)
                    .padding(end = 8.dp)
                    .fillMaxWidth())
            {

                Icon(icon,
                    modifier = Modifier.height(36.dp).width(36.dp),
                    tint = colorScheme.outline,
                    contentDescription = null)
                FlowRow(modifier = Modifier.fillMaxWidth().align(Alignment.CenterVertically),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)){
                    Row(modifier = Modifier.clickable{ post.user?.id?.let { userClick(it)  } },
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically){
                        Avatar(
                            avatarUrl = post.user?.avatarUrl,
                            name = post.user?.displayName,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                        )
                        Text(
                            text = post.user?.displayName ?: post.user?.username ?: "",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = contentTextStyle,
                            modifier = Modifier.weight(1f, fill = false)
                        )
                        post.createdAt?.relativeTime?.let {
                            Text(text = it,
                                style = contentTextStyle,
                                color = contentTextColor)
                        }

                    }

                    FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp),
                        modifier = Modifier.align(Alignment.CenterVertically)){
                        detailsContent()
                    }

                }
            }
        }

        if(isComment){
            post.contentMarkdown?.let { markdown ->
                val isDarkTheme = isSystemInDarkTheme()
                val markdownState = rememberMarkdownState(post.id, retainState = true) {
                    markdown
                }
                val markdownComponents = remember(isDarkTheme) {
                    val highlightsBuilder = Highlights.Builder().theme(SyntaxThemes.atom(darkMode = isDarkTheme))
                    markdownComponents(
                        codeBlock = {
                            MarkdownHighlightedCodeBlock(
                                content = it.content,
                                node = it.node,
                                highlightsBuilder = highlightsBuilder,
                                showHeader = true,
                            )
                        },
                        codeFence = {
                            MarkdownHighlightedCodeFence(
                                content = it.content,
                                node = it.node,
                                highlightsBuilder = highlightsBuilder,
                                showHeader = true,
                            )
                        },
                    )
                }
                SelectionContainer{
                    Markdown(
                        markdownState = markdownState,
                        imageTransformer = ClickableCoil3ImageTransformer(imageClick),
                        components = markdownComponents,
                        modifier = Modifier
                            .padding(vertical = 12.dp)
                            .fillMaxWidth(),
                    )
                }
            }
            val reactions = post.reactionCounts?.mapNotNull {
                    (id,value) ->
                val reaction = reactions?.get(id)
                if(reaction != null)
                    reaction to value
                else null
            }
            if(!reactions.isNullOrEmpty() && !reactions.all { it.second == 0 }){
                ReactionList(
                    reactions = reactions,
                    selectedReaction = post.userReactionIdentifier,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
            Box(modifier = Modifier.fillMaxWidth()){
                Row(
                    modifier = Modifier.align(Alignment.CenterStart),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val isUpvoted = post.hasUpvoted ?: false
                    val votes = post.votes ?: 0

                    Row(
                        modifier = Modifier.align(Alignment.CenterVertically),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconToggleButton(
                            checked = post.hasUpvoted ?: false,
                            enabled = post.canVote ?: false,
                            onCheckedChange = {
                                val newUpvoted = !isUpvoted
                                onVote(post.id, newUpvoted, post.hasDownvoted?:false)
                            }
                        ) {
                            if(isVoting){
                                CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                            }
                            else
                            {
                                Icon(
                                    imageVector = if (isUpvoted) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp,
                                    tint = if (isUpvoted) colorScheme.primary else colorScheme.outline,
                                    contentDescription = null
                                )
                            }
                        }
                        if (votes != 0) {
                            Text(
                                text = votes.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isUpvoted) colorScheme.primary else colorScheme.outline
                            )
                        }
                    }




                    Button(onClick ={},
                        contentPadding = PaddingValues(4.dp),
                        colors = ButtonDefaults.outlinedButtonColors()){
                        Icon(Icons.Outlined.AddReaction,
                            tint = colorScheme.outline,
                            contentDescription = null)
                    }
                }


                Row(
                    modifier = Modifier.offset(12.dp).align(Alignment.CenterEnd),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        replyClick(post.user?.displayName ?: post.user?.username ?: "", post.id)
                    }){
                        Icon(Icons.AutoMirrored.Filled.Reply,
                            tint = colorScheme.outline,
                            contentDescription = null)
                    }
                    IconButton(onClick = {}){
                        Icon(Icons.Default.MoreHoriz,
                            tint = colorScheme.outline,
                            contentDescription =  null)
                    }
                }
            }

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
        id = "1"
        contentType = "comment"
        user = sampleUser
        createdAt = ZonedDateTime.now().minusHours(1)
        number = 2
        votes = 1
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
        PostItem(post = samplePost, isOp = true, modifier = Modifier.padding(16.dp))
    }
}


@Preview(showBackground = true)
@Composable
fun PostItemRenamePreview() {
    val sampleUser = User().apply {
        displayName = "User"
        username = "user"
        avatarUrl = null
    }
    val samplePost = Post().apply {
        id = "1"
        contentType = "discussionRenamed"
        user = sampleUser
        createdAt = ZonedDateTime.now().minusHours(1)
        number = 2
        content = listOf("11111111", "22222222")
    }
    MaterialTheme {
        PostItem(post = samplePost, isOp = true, modifier = Modifier.padding(16.dp))
    }
}
