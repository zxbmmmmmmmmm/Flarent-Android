package com.bettafish.flarent.ui.pages.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.twotone.History
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material.icons.twotone.Star
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bettafish.flarent.App
import com.bettafish.flarent.R
import com.bettafish.flarent.models.User
import com.bettafish.flarent.models.navigation.LoginResult
import com.bettafish.flarent.ui.widgets.Avatar
import com.bettafish.flarent.ui.widgets.setting.item.TextSetting
import com.bettafish.flarent.utils.appSettings
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.AboutPageDestination
import com.ramcosta.composedestinations.generated.destinations.DiscussionListPageDestination
import com.ramcosta.composedestinations.generated.destinations.LoginPageDestination
import com.ramcosta.composedestinations.generated.destinations.SettingsPageDestination
import com.ramcosta.composedestinations.generated.destinations.UserProfilePageDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.ResultRecipient
import com.ramcosta.composedestinations.result.onResult
import org.koin.androidx.compose.koinViewModel

@Composable
@Destination<RootGraph>
@OptIn(ExperimentalMaterial3Api::class)
fun AccountPage(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator,
    resultRecipient: ResultRecipient<LoginPageDestination, LoginResult>,
    viewModel: AccountViewModel = koinViewModel()
) {
    val user by viewModel.user.collectAsState()
    resultRecipient.onResult(
        onValue = { resultValue ->
            App.INSTANCE.appSettings.userId = resultValue.id
            App.INSTANCE.appSettings.token = resultValue.token
            viewModel.refreshUser(resultValue.id, true)
        }
    )
    Column(modifier = modifier.fillMaxSize()) {
        AccountInfo(
            user = user,
            modifier = Modifier
                .clickable {
                    if (user != null) {
                        navigator.navigate(UserProfilePageDestination(user!!.username!!))
                    } else {
                        navigator.navigate(LoginPageDestination)
                    }
                }
                .statusBarsPadding()
                .padding(16.dp, 48.dp, 16.dp, 32.dp),
            onLogoutClick = { viewModel.logout() })
        user?.let {
            val historyTitle = stringResource(R.string.history)
            val followingTitle = stringResource(R.string.following_discussions)
            TextSetting(
                title = historyTitle,
                minimalHeight = true,
                leadingIcon = { Icon(Icons.Default.History, contentDescription = null) },
                onClick = {
                    navigator.navigate(
                        DiscussionListPageDestination(
                            title = historyTitle,
                            sort = "-lastReadAt"
                        )
                    )
                }
            )
            TextSetting(
                title = followingTitle,
                minimalHeight = true,
                leadingIcon = { Icon(Icons.Default.Star, contentDescription = null) },
                onClick = {
                    navigator.navigate(
                        DiscussionListPageDestination(
                            filter = arrayOf(
                                "subscription",
                                "following"
                            ),
                            title = followingTitle
                        )
                    )
                }
            )
        }

        TextSetting(
            title = stringResource(R.string.settings),
            minimalHeight = true,
            leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) },
            onClick = { navigator.navigate(SettingsPageDestination) }
        )
        TextSetting(
            title = stringResource(R.string.about),
            minimalHeight = true,
            leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
            onClick = { navigator.navigate(AboutPageDestination) }
        )
    }
}

@Composable
fun AccountInfo(modifier: Modifier = Modifier, user: User? = null, onLogoutClick: () -> Unit = {}) {
    Row(modifier = modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp), modifier = Modifier.weight(1f)) {
            Avatar(
                user?.avatarUrl, user?.displayName, modifier = Modifier
                    .height(64.dp)
                    .width(64.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterVertically)
            )
            Column(
                modifier = Modifier.align(Alignment.CenterVertically),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    user?.displayName ?: user?.username ?: stringResource(R.string.not_logged_in),
                    style = MaterialTheme.typography.titleLarge
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    val textStyle = MaterialTheme.typography.titleSmall
                    val density = LocalDensity.current
                    val textHeightDp = with(density) { textStyle.lineHeight.toDp() }
                    Text(
                        if (user != null) stringResource(R.string.view_profile) else stringResource(R.string.tap_to_login),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        modifier = Modifier
                            .height(textHeightDp)
                            .align(Alignment.CenterVertically),
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
        if (user != null) {
            Button(
                colors = ButtonDefaults.filledTonalButtonColors(),
                modifier = Modifier.align(Alignment.CenterVertically),
                onClick = { onLogoutClick() }) {
                Text(stringResource(R.string.logout))
            }
        }
    }


}

@Composable
@Preview(showBackground = true)
fun AccountInfoPreview() {
    AccountInfo(
        modifier = Modifier.padding(16.dp),
        user = User().apply {
            displayName = "John Doe"
            username = "AA"
            id = "1"
        })
}

@Composable
@Preview(showBackground = true)
fun AccountInfoWithoutUserPreview() {
    AccountInfo(modifier = Modifier.padding(16.dp))
}
