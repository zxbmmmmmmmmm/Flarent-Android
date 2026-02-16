package com.bettafish.flarent.ui.pages.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bettafish.flarent.App
import com.bettafish.flarent.models.User
import com.bettafish.flarent.models.navigation.LoginResult
import com.bettafish.flarent.ui.widgets.Avatar
import com.bettafish.flarent.ui.widgets.setting.Item.TextPref
import com.bettafish.flarent.utils.appSettings
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.LoginPageDestination
import com.ramcosta.composedestinations.generated.destinations.UserProfilePageDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import com.ramcosta.composedestinations.result.onResult
import org.koin.androidx.compose.getViewModel

@Composable
@Destination<RootGraph>
@OptIn(ExperimentalMaterial3Api::class)
fun AccountPage(modifier: Modifier = Modifier,
                navigator: DestinationsNavigator,
                resultRecipient: ResultRecipient<LoginPageDestination, LoginResult>
){
    val viewModel : AccountViewModel = getViewModel()
    val user by viewModel.user.collectAsState()
    resultRecipient.onResult(
        onValue = { resultValue ->
            App.INSTANCE.appSettings.userId = resultValue.id
            App.INSTANCE.appSettings.token = resultValue.token
            viewModel.refreshUser(resultValue.id)
        }
    )
    Column(modifier = modifier.fillMaxSize()){
        AccountInfo(user = user,
            modifier = Modifier.clickable{
                if(user != null){
                    navigator.navigate(UserProfilePageDestination(user!!.username!!))
                }
                else{
                    navigator.navigate(LoginPageDestination)
                }
            }
                .statusBarsPadding()
                .padding(16.dp, 48.dp, 16.dp, 32.dp),
            onLogoutClick = { viewModel.logout() })
        TextPref(
            title = "设置",
            leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) }
        )
        TextPref(
            title = "关于",
            leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) }
        )
    }
}

@Composable
fun AccountInfo(modifier: Modifier = Modifier, user: User? = null, onLogoutClick : () -> Unit  = {}){
    Box(modifier = modifier.fillMaxWidth()){
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            Avatar(user?.avatarUrl, user?.displayName, modifier = Modifier
                .height(64.dp)
                .width(64.dp)
                .clip(CircleShape))
            Column(modifier = Modifier.align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(user?.displayName ?: user?.username?: "未登录", style = MaterialTheme.typography.titleLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    val textStyle = MaterialTheme.typography.titleSmall
                    val density = LocalDensity.current
                    val textHeightDp = with(density) { textStyle.lineHeight.toDp() }
                    Text(if(user != null) "查看个人空间" else "点击登录",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.outline)
                    Icon(imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier
                            .height(textHeightDp)
                            .align(Alignment.CenterVertically),
                        tint = MaterialTheme.colorScheme.outline)
                }
            }
        }
        if(user != null){
            Button(colors = ButtonDefaults.filledTonalButtonColors(),
                modifier = Modifier.align(Alignment.CenterEnd),
                onClick = { onLogoutClick() }){
                Text("退出登录")
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