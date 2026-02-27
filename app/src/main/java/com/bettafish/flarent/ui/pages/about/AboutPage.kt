package com.bettafish.flarent.ui.pages.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Info
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bettafish.flarent.R
import com.bettafish.flarent.ui.widgets.BackNavigationIcon
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Destination<RootGraph>
@Preview
fun AboutPage() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("关于") },
                navigationIcon = {
                    BackNavigationIcon { }
                },
            )
        },) {
            innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            ForumCard()
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)){
                StandardLargeCard(Icons.Default.Language, "官方网站", shape = RoundedCornerShape(12.dp,12.dp,4.dp,4.dp)){

                }
                StandardLargeCard(Icons.Default.Code, "源代码"){

                }
                StandardLargeCard(Icons.Default.Sync, "版本更新", "已是最新版本", shape = RoundedCornerShape(4.dp,4.dp,12.dp,12.dp)){

                }
            }
            StandardLargeCard(Icons.TwoTone.Info, "基于 Flarent 开发", "GPL-3  License", shape = RoundedCornerShape(12.dp)){

            }
        }
    }
}

@Composable
fun ForumCard(){
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)){
        Card(RoundedCornerShape(12.dp,12.dp,4.dp,4.dp)){
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)){
                Image(
                    painter = painterResource(id  = R.drawable.guest),
                    null,
                    modifier = Modifier.size(64.dp).clip(CircleShape))
                Column(modifier = Modifier.align(Alignment.CenterVertically),
                    verticalArrangement = Arrangement.spacedBy(4.dp)){
                    Text("wvbCommunity", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Text("@wvbtech", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
        Card(RoundedCornerShape(4.dp,4.dp,12.dp,12.dp)){
            Row(verticalAlignment = Alignment.CenterVertically,){
                Row(Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically){
                    Text("1.1.0")
                    Text("已是最新版本",  color = MaterialTheme.colorScheme.outline)
                }
                Icon(Icons.Default.Sync, null, tint = MaterialTheme.colorScheme.secondary, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun Card(shape: Shape = RoundedCornerShape(4.dp), content : @Composable () -> Unit){
    Surface(color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier
        .fillMaxWidth()
        .clip(shape)) {
        Box(modifier = Modifier.padding(16.dp)){
            content()
        }
    }
}

@Composable
fun LargeCard(shape: Shape = RoundedCornerShape(4.dp), onClick : () -> Unit = {}, content : @Composable () -> Unit){
    Surface(color = MaterialTheme.colorScheme.surfaceContainer,
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .clickable{ onClick() }) {
        Box(modifier = Modifier.padding(16.dp)){
            content()
        }
    }
}

@Composable
fun StandardLargeCard(icon: ImageVector,
                      title: String,
                      description: String? = null,
                      shape: Shape = RoundedCornerShape(4.dp),
                      onClick : () -> Unit = {}){
    LargeCard(shape, onClick){
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically){
            Icon(icon, null, tint = MaterialTheme.colorScheme.primary,  modifier = Modifier.size(28.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                description?.let {
                    Text(it, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.outline)
                }
            }
            Icon(Icons.Default.ChevronRight, null, tint = MaterialTheme.colorScheme.outline)
        }
    }
}