package com.bettafish.flarent.ui.pages.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.outlined.PinDrop
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.bettafish.flarent.App
import com.bettafish.flarent.BuildConfig
import com.bettafish.flarent.R
import com.bettafish.flarent.config.ForumConfig
import com.bettafish.flarent.models.Forum
import com.bettafish.flarent.models.ForumStats
import com.bettafish.flarent.models.StatsItem
import com.bettafish.flarent.ui.widgets.BackNavigationIcon
import com.bettafish.flarent.ui.widgets.Card
import com.bettafish.flarent.ui.widgets.StandardLargeCard
import com.bettafish.flarent.utils.appSettings
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import retrofit2.http.Url

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination<RootGraph>
fun AboutPage() {
    val uriHandler = LocalUriHandler.current
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("关于") },
                navigationIcon = {
                    BackNavigationIcon { }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            val forum = App.INSTANCE.appSettings.forum!!
            ForumCard(
                forum,
                ForumConfig.privacyPolicyUrl,
                ForumConfig.baseUrl,
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {

                StandardLargeCard(
                    Icons.Default.Sync,
                    "版本更新",
                    "当前版本 ${BuildConfig.VERSION_NAME}",
                    shape = RoundedCornerShape(12.dp, 12.dp, 4.dp, 4.dp)
                ) {
                    uriHandler.openUri(ForumConfig.checkUpdateUrl)
                }
                StandardLargeCard(
                    Icons.TwoTone.Info,
                    "基于 Flarent 开发",
                    "GPL-3  License",
                    shape = RoundedCornerShape(4.dp, 4.dp, 12.dp, 12.dp)
                ) {
                    uriHandler.openUri("https://github.com/zxbmmmmmmmmm/Flarent-Android")
                }
            }

        }
    }
}

@Composable
fun ForumCard(
    forum: Forum,
    privacyPolicyUrl: String,
    baseUrl: String,
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Card(
            shape = RoundedCornerShape(12.dp, 12.dp, 4.dp, 4.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    horizontalArrangement = Arrangement.Center
                ) {
                    AsyncImage(
                        model = forum.faviconUrl,
                        null,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(32.dp)
                    )
                    Text(
                        forum.title!!,
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }

                forum.description?.let {
                    Text(
                        forum.description!!,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        textAlign = TextAlign.Center
                    )
                }

                forum.stats?.let {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ForumStatsItem(it.discussionCount, "讨论")
                        VerticalDivider(
                            thickness = 2.dp,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .height(32.dp)
                        )
                        ForumStatsItem(it.commentPostCount, "帖子")
                        VerticalDivider(
                            thickness = 2.dp,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .height(32.dp)
                        )
                        ForumStatsItem(it.userCount, "用户")
                    }
                }
            }

        }
        val uriHandler = LocalUriHandler.current
        forum.guidelinesUrl?.let {
            StandardLargeCard(
                icon = Icons.Outlined.PinDrop,
                title = "导航帖",
            ) {
                uriHandler.openUri(it)
            }
        }
        StandardLargeCard(
            icon = Icons.Outlined.PrivacyTip,
            title = "隐私政策",
        ) {
            uriHandler.openUri(privacyPolicyUrl)
        }
        StandardLargeCard(
            icon = Icons.Default.Language,
            title = "访问论坛",
            shape = RoundedCornerShape(4.dp, 4.dp, 12.dp, 12.dp),
            actionIcon = Icons.AutoMirrored.Filled.OpenInNew
        ) {
            uriHandler.openUri(baseUrl)
        }
//        Card(
//            shape = RoundedCornerShape(4.dp, 4.dp, 12.dp, 12.dp),
//            onClick = {
//                uriHandler.openUri(ForumConfig.checkUpdateUrl)
//            }) {
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Row(
//                    Modifier.weight(1f),
//                    horizontalArrangement = Arrangement.spacedBy(8.dp),
//                    verticalAlignment = Alignment.CenterVertically
//                ) {
//                    Text(BuildConfig.VERSION_NAME)
//                    // Text("已是最新版本", color = MaterialTheme.colorScheme.outline)
//                }
//                Icon(
//                    Icons.Default.Sync,
//                    null,
//                    tint = MaterialTheme.colorScheme.secondary,
//                    modifier = Modifier.size(20.dp)
//                )
//            }
//        }
    }
}

@Composable
fun ForumStatsItem(item: StatsItem, name: String) {
    Column() {
        Text(
            item.prettyValue,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Text(
            name,
            color = MaterialTheme.colorScheme.outline,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Preview
@Composable
fun ForumCardPreview() {
    ForumCard(
        Forum().apply {
            title = "测试论坛"
            description = "这是一个测试论坛"
            guidelinesUrl = "https://example.com/guidelines"
            stats = ForumStats(
                discussionCount = StatsItem("讨论", null, 123, "123"),
                commentPostCount = StatsItem("帖子", null, 456, "456"),
                userCount = StatsItem("用户", null, 789, "789")
            )
        },
        "https://example.com",
        "https://example.com",
    )
}