package com.bettafish.flarent.ui.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bettafish.flarent.models.Discussion
import com.bettafish.flarent.models.Tag
import com.bettafish.flarent.ui.widgets.DiscussionItem
import com.bettafish.flarent.utils.relativeTime
import com.bettafish.flarent.utils.toFaIcon
import com.bettafish.flarent.viewModels.TagsViewModel
import com.guru.fontawesomecomposelib.FaIcon
import org.koin.androidx.compose.getViewModel
import java.time.ZonedDateTime

@Composable
fun TagsPage(modifier: Modifier = Modifier) {
    val viewModel: TagsViewModel = getViewModel()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    val list by viewModel.tags.collectAsState()
    val typography = MaterialTheme.typography

    LazyColumn(modifier = modifier) {
        items(list) {
            if(it.isChild == false){
                TagViewItem(it,modifier = Modifier.clickable{})
            }
        }
    }
}

@Composable
fun TagViewItem(tag : Tag, modifier: Modifier = Modifier){
    Surface(modifier = modifier){
        Column(modifier = Modifier.padding(24.dp)) {
            Row() {
                val textStyle = MaterialTheme.typography.headlineLarge
                val density = LocalDensity.current
                val textHeightDp = with(density) { textStyle.lineHeight.toDp() }
                val icon = tag.icon?.toFaIcon()
                if (icon != null)
                    FaIcon(
                        faIcon = icon,
                        size = textHeightDp,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .align(Alignment.CenterVertically),
                        tint = LocalContentColor.current
                    )
                Text(
                    text = tag.name ?: "",
                    style = textStyle,
                )
            }
            tag.description?.let{
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            tag.children?.let { children ->
                FlowRow(
                    modifier = Modifier.padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                )  {
                    children.forEach {
                        ChildrenTagViewItem(it)
                    }
                }
            }


            tag.lastPostedDiscussion?.let {
                TagDiscussionPreview(it,
                    modifier = Modifier.padding(top = 16.dp).fillMaxWidth())
            }
        }
    }
}

@Composable
fun ChildrenTagViewItem(tag : Tag, modifier: Modifier = Modifier) {
    Button(onClick = { /*TODO*/ }, modifier){
        Row{
            val textStyle = MaterialTheme.typography.bodyMedium
            val density = LocalDensity.current
            val textHeightDp = with(density) { textStyle.lineHeight.toDp() }
            val icon = tag.icon?.toFaIcon()
            if(icon != null)
                FaIcon(faIcon = icon,
                    size = textHeightDp,
                    tint = LocalContentColor.current,
                    modifier= Modifier
                        .padding(end = 12.dp)
                        .align(Alignment.CenterVertically))
            Text(text = tag.name ?: "",
                style= textStyle)
        }
    }
}

@Composable
fun TagDiscussionPreview(discussion: Discussion,modifier: Modifier){
    Card(modifier = modifier
        .clickable { /* Navigate to detail */ }
        .clip(RoundedCornerShape(8.dp)))
        {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = discussion.title ?: "",
                style = MaterialTheme.typography.titleMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)){
                Text(text = discussion.lastPostedAt?.relativeTime ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "${discussion.commentCount} 回复" ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline)
            }

        }
    }
}

@Composable
@Preview
fun TagViewItemPreview(tag: Tag = Tag().apply {
    name = "Windows"
    slug = "windows"
    icon = "fas fa-windows"
    description = "Microsoft, your potential, our passion."
    color = "#0077C8"
    children = listOf(Tag().apply{
        name = "Beta"
        slug = "ai"
        icon = "fas fa-flask"
    },
        Tag().apply{
            name = "PC"
            slug = "pc"
            icon = "fas fa-desktop"
        })
    lastPostedDiscussion = Discussion().apply {
        title = "Thin PC 7 全补丁集成版重制 · 最后一更"
        slug = "hello-world"
        commentCount = 10
        participantCount = 10
        createdAt = ZonedDateTime.now()
        lastPostedAt = ZonedDateTime.now()
    }
}){
    TagViewItem(tag)
}