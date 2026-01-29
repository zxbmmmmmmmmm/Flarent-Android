package com.bettafish.flarent.ui.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavArgs
import androidx.navigation.NavController
import com.bettafish.flarent.models.Discussion
import com.bettafish.flarent.models.Tag
import com.bettafish.flarent.models.navigation.TagNavArgs
import com.bettafish.flarent.ui.widgets.BackNavigationIcon
import com.bettafish.flarent.utils.relativeTime
import com.bettafish.flarent.utils.toFaIcon
import com.bettafish.flarent.viewModels.TagsViewModel
import com.guru.fontawesomecomposelib.FaIcon
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.DiscussionsPageDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.getViewModel
import java.time.ZonedDateTime

@Composable
@Destination<RootGraph>
@ExperimentalMaterial3Api
fun TagsPage(modifier: Modifier = Modifier, navigator: DestinationsNavigator) {
    val viewModel: TagsViewModel = getViewModel()
    val list by viewModel.tags.collectAsState()
    val typography = MaterialTheme.typography
    var isRefreshing by remember { mutableStateOf(list.isEmpty()) }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
        },
        modifier = modifier.fillMaxSize()
    ){
        LaunchedEffect(isRefreshing) {
            if (isRefreshing) {
                viewModel.refresh()
                isRefreshing = false
            }
        }
        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                MediumTopAppBar(
                    title = { Text("标签") },
                    scrollBehavior = scrollBehavior
                )
            }
        ){ padding ->
            LazyColumn(Modifier.padding(padding)) {
                items(list) { tag ->
                    if(tag.isChild == false){
                        TagViewItem(tag,
                            onClick = {
                                navigator.navigate(DiscussionsPageDestination(TagNavArgs.from(it)))
                            },
                            onChildrenClick = {
                                navigator.navigate(DiscussionsPageDestination(TagNavArgs.from(it)))
                            },
                            onDiscussionClick = {
                            })
                    }
                }
            }
        }

    }
}

@Composable
fun TagViewItem(tag : Tag,
                modifier: Modifier = Modifier,
                onClick: (Tag) -> Unit = {},
                onChildrenClick: (Tag) -> Unit = {},
                onDiscussionClick: (Discussion) -> Unit = {}){
    Surface(modifier = modifier.clickable{ onClick(tag) }){
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
                        ChildrenTagViewItem(it, onClick = onChildrenClick)
                    }
                }
            }


            tag.lastPostedDiscussion?.let {
                TagDiscussionItem(it,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(), onDiscussionClick)
            }
        }
    }
}

@Composable
fun ChildrenTagViewItem(tag : Tag, modifier: Modifier = Modifier, onClick: (Tag) -> Unit) {
    Button(onClick = { onClick(tag) }, modifier){
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
fun TagDiscussionItem(discussion: Discussion, modifier: Modifier, onClick: (Discussion) -> Unit = {}){
    Card(modifier = modifier
        .clickable { onClick(discussion) }
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