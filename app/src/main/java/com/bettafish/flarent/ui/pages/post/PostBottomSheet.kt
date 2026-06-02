package com.bettafish.flarent.ui.pages.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreProvider
import com.bettafish.flarent.ui.widgets.Card
import com.bettafish.flarent.ui.widgets.post.PostItem
import com.bettafish.flarent.ui.widgets.post.PostItemViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.bottomsheet.spec.DestinationStyleBottomSheet
import com.ramcosta.composedestinations.generated.destinations.DiscussionDetailPageDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
@ExperimentalMaterial3Api
@Destination<RootGraph>(style = DestinationStyleBottomSheet::class)
fun PostBottomSheet(
    id: String,
    discussionId: String? = null,
    discussionTitle: String? = null,
    navigator: DestinationsNavigator
) {

    val screenHeight = LocalWindowInfo.current.containerDpSize.height
    val storeProvider = rememberViewModelStoreProvider()
    val owner = rememberViewModelStoreOwner(
        provider = storeProvider,
        key = id
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = screenHeight / 2)
            .windowInsetsPadding(WindowInsets.systemBars)
            .verticalScroll(rememberScrollState())
    ) {
        CompositionLocalProvider(LocalViewModelStoreOwner provides owner) {
            val viewModel: PostItemViewModel =
                koinViewModel<PostItemViewModel>(key = id) { parametersOf(id, null) }
            PostItem(
                viewModel,
                navigator,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            )
        }
        discussionId?.let {
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                onClick = {
                    navigator.navigate(DiscussionDetailPageDestination(discussionId))
                }) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        if (discussionTitle != null) {
                            Text("于", color = MaterialTheme.colorScheme.outline)
                            Text(discussionTitle, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        } else {
                            Text("查看原帖")
                        }

                    }
                    Icon(
                        Icons.AutoMirrored.Filled.OpenInNew,
                        null,
                        modifier = Modifier
                            .size(20.dp)
                            .align(Alignment.CenterVertically),
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }
    }
}