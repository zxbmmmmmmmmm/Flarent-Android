package com.bettafish.flarent.ui.pages.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.rememberViewModelStoreProvider
import com.bettafish.flarent.R
import com.bettafish.flarent.data.PostsRepository
import com.bettafish.flarent.models.request.PostsRequest
import com.bettafish.flarent.ui.widgets.Card
import com.bettafish.flarent.ui.widgets.post.PostItem
import com.bettafish.flarent.ui.widgets.post.PostItemViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.bottomsheet.spec.DestinationStyleBottomSheet
import com.ramcosta.composedestinations.generated.destinations.DiscussionDetailPageDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.inject

@Composable
@ExperimentalMaterial3Api
@Destination<RootGraph>(style = DestinationStyleBottomSheet::class)
fun PostBottomSheet(
    postId: String? = null,
    discussionId: String? = null,
    postNumber: String? = null,
    discussionTitle: String? = null,
    navigator: DestinationsNavigator,
    viewModel: PostBottomSheetViewModel = koinViewModel()
) {
    val screenHeight = LocalWindowInfo.current.containerDpSize.height
    val id = remember { mutableStateOf(postId) }
    if (postNumber != null && discussionId != null) {
        LaunchedEffect(discussionId) {
            viewModel.getPostId(discussionId, postNumber)?.let {
                id.value = it
            }
        }

    }
    val storeProvider = rememberViewModelStoreProvider()
    val owner = rememberViewModelStoreOwner(
        provider = storeProvider,
        key = id.value
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = screenHeight / 2)
            .windowInsetsPadding(WindowInsets.systemBars)
            .verticalScroll(rememberScrollState())
    ) {
        CompositionLocalProvider(LocalViewModelStoreOwner provides owner) {
            if (id.value != null) {
                val viewModel: PostItemViewModel =
                    koinViewModel<PostItemViewModel>(key = id.value) { parametersOf(id.value, null) }
                PostItem(
                    viewModel,
                    navigator,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                )
            }
            else{
                CircularProgressIndicator(modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp, bottom = 32.dp)
                    .size(48.dp))
            }
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
                            Text(stringResource(R.string.in_discussion), color = MaterialTheme.colorScheme.outline)
                            Text(discussionTitle, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        } else {
                            Text(stringResource(R.string.view_original_post))
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
