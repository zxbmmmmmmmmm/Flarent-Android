package com.bettafish.flarent.ui.pages

import android.accounts.Account
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bettafish.flarent.models.User
import com.bettafish.flarent.ui.widgets.Avatar
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination<RootGraph>
fun AccountPage(modifier: Modifier = Modifier,navigator: DestinationsNavigator){
    Column{

    }
}

@Composable
fun AccountInfo(modifier: Modifier = Modifier, user: User? = null){
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(24.dp)) {
        Avatar(user?.avatarUrl, user?.displayName, modifier = Modifier.height(64.dp).width(64.dp).clip(CircleShape))
        Column(modifier = Modifier.align(Alignment.CenterVertically)) {
            Text(user?.displayName ?: user?.username?: "未登录", style = MaterialTheme.typography.titleLarge)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                val textStyle = MaterialTheme.typography.bodySmall
                val density = LocalDensity.current
                val textHeightDp = with(density) { textStyle.lineHeight.toDp() }
                Text(if(user != null) "查看个人空间" else "点击登录",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.outline)
                Icon(imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    modifier = Modifier.height(textHeightDp).align(Alignment.CenterVertically),
                    tint = MaterialTheme.colorScheme.outline)
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AccountInfoPreview(){
    AccountInfo(modifier = Modifier.padding(16.dp),
        user = User().apply {
            displayName = "John Doe"
            username = "AA"
            id = "1"
    })
}

@Composable
@Preview(showBackground = true)
fun AccountInfoWithoutUserPreview(){
    AccountInfo(modifier = Modifier.padding(16.dp))
}