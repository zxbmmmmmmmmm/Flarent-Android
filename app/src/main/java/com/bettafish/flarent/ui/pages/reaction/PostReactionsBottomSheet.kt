package com.bettafish.flarent.ui.pages.reaction

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bettafish.flarent.models.Reaction
import com.bettafish.flarent.models.User
import com.bettafish.flarent.ui.widgets.Avatar
import com.bettafish.flarent.ui.widgets.getEmoji
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.bottomsheet.spec.DestinationStyleBottomSheet
import com.ramcosta.composedestinations.generated.destinations.UserProfilePageDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
@Destination<RootGraph>(style = DestinationStyleBottomSheet::class)
fun PostReactionsBottomSheet(postId: String, navigator: DestinationsNavigator){
    val viewModel: PostReactionsViewModel = getViewModel{ parametersOf(postId) }
    val reactions = viewModel.reactions.collectAsState()
    val screenHeight = LocalWindowInfo.current.containerDpSize.height

    Box(modifier = Modifier.fillMaxWidth()
        .defaultMinSize(minHeight = screenHeight / 2)
        .windowInsetsPadding(WindowInsets.systemBars)){
        if(reactions.value == null){
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        else{
            PostReactionsContent(reactions.value!!, onUserClick = {
                it.username?.let  { username->
                    navigator.navigate(UserProfilePageDestination(username))
                }
            })
        }
    }
}

@Composable
private fun PostReactionsContent(reactions: List<Pair<Reaction, List<User>>> ,
                                 onUserClick: (User) -> Unit = {}){
    Column(modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())){
        reactions.forEach { (reaction, users) ->
            ReactionCard(
                reaction,
                users,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                onUserClick)
        }
    }
}

@Composable
fun ReactionCard(reaction: Reaction,
                 users: List<User>,
                 modifier: Modifier = Modifier,
                 onUserClick: (User) -> Unit = {}){
    Card(modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )){
        Column(
            modifier = Modifier.fillMaxWidth()) {

            Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically){

                if(reaction.type == "emoji"){
                    Text(getEmoji(reaction.identifier) ?: "", style = MaterialTheme.typography.titleLarge)
                }
                Text(reaction.display ?: "",
                    modifier = Modifier.padding(start = 16.dp, end = 12.dp),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary)
                Surface(color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = CircleShape){
                    Text(users.size.toString(),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 12.dp))
                }
                intArrayOf()
            }
            HorizontalDivider()
            Column(modifier = Modifier.fillMaxWidth()){
                users.forEachIndexed { index, user ->
                    val padding =
                        when (index) {
                            0 if users.size == 1 -> PaddingValues(16.dp)
                            0 -> {
                                PaddingValues(16.dp, 16.dp, 16.dp, 12.dp)
                            }
                            users.size - 1 -> {
                                PaddingValues(16.dp, 12.dp, 16.dp, 16.dp)
                            }
                            else -> {
                                PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                            }
                        }

                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .clickable{
                            onUserClick(user)
                        }
                        .padding(padding),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)){
                        Avatar(user.avatarUrl,
                            user.displayName ?: user.username,
                            modifier = Modifier.size(32.dp).clip(CircleShape))
                        Text(user.displayName ?: user.username?: "",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.CenterVertically))
                    }
                }
            }
        }
    }
}

@Composable
@Preview
fun ReactionCardPreview(){
    ReactionCard(Reaction().apply {
        type = "emoji"
        identifier = "heart"
        display = "好"
    }, listOf(
        User().apply {
            displayName = "User"
            username = "user"
            avatarUrl = null
        },
        User().apply {
            displayName = "114514"
            username = "user"
            avatarUrl = null
        }
    ))

}
