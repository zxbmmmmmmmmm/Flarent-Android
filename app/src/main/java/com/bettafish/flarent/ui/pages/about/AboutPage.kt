package com.bettafish.flarent.ui.pages.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bettafish.flarent.BuildConfig
import com.bettafish.flarent.R
import com.bettafish.flarent.config.ForumConfig
import com.bettafish.flarent.ui.widgets.BackNavigationIcon
import com.bettafish.flarent.ui.widgets.Card
import com.bettafish.flarent.ui.widgets.StandardLargeCard
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination<RootGraph>
@Preview
fun AboutPage() {
    val uriHandler = LocalUriHandler.current
    val forumName = stringResource(ForumConfig.nameRes)
    val forumHandle = stringResource(ForumConfig.handleRes)
    val websiteUrl = stringResource(ForumConfig.websiteUrlRes)
    val sourceCodeUrl = stringResource(ForumConfig.sourceCodeUrlRes)
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
            ForumCard(
                forumName = forumName,
                forumHandle = forumHandle
            ) {
                uriHandler.openUri(websiteUrl)
            }
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                StandardLargeCard(
                    Icons.Default.Language,
                    "官方网站",
                    websiteUrl,
                    shape = RoundedCornerShape(12.dp, 12.dp, 4.dp, 4.dp)
                ) {
                    uriHandler.openUri(websiteUrl)
                }
                StandardLargeCard(
                    Icons.Default.Code,
                    "源代码",
                    sourceCodeUrl
                ) {
                    uriHandler.openUri(sourceCodeUrl)
                }
                StandardLargeCard(
                    Icons.Default.Sync,
                    "版本更新",
                    "当前版本 ${BuildConfig.VERSION_NAME}",
                    shape = RoundedCornerShape(4.dp, 4.dp, 12.dp, 12.dp)
                ) {

                }
            }
            StandardLargeCard(
                Icons.TwoTone.Info,
                "基于 Flarent 开发",
                "GPL-3  License",
                shape = RoundedCornerShape(12.dp)
            ) {
                uriHandler.openUri(sourceCodeUrl)
            }
        }
    }
}

@Composable
fun ForumCard(
    forumName: String = stringResource(ForumConfig.nameRes),
    forumHandle: String = stringResource(ForumConfig.handleRes),
    onClick: (() -> Unit)? = null
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Card(
            shape = RoundedCornerShape(12.dp, 12.dp, 4.dp, 4.dp),
            onClick = onClick
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Image(
                    painter = painterResource(id = R.drawable.guest),
                    null,
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                )
                Column(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        forumName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(forumHandle, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        Card(shape = RoundedCornerShape(4.dp, 4.dp, 12.dp, 12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(BuildConfig.VERSION_NAME)
                    Text("已是最新版本", color = MaterialTheme.colorScheme.outline)
                }
                Icon(
                    Icons.Default.Sync,
                    null,
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

