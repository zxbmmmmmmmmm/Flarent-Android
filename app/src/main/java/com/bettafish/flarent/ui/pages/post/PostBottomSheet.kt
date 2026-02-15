package com.bettafish.flarent.ui.pages.post

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp
import com.bettafish.flarent.ui.widgets.LocalImagePreviewer
import com.bettafish.flarent.ui.widgets.post.PostItem
import com.bettafish.flarent.ui.widgets.post.PostItemViewModel
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.bottomsheet.spec.DestinationStyleBottomSheet
import com.ramcosta.composedestinations.generated.destinations.ReplyBottomSheetDestination
import com.ramcosta.composedestinations.generated.destinations.UserProfilePageDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

@Composable
@ExperimentalMaterial3Api
@Destination<RootGraph>(style = DestinationStyleBottomSheet::class)
fun PostBottomSheet(id: String, navigator: DestinationsNavigator){

    val screenHeight = LocalWindowInfo.current.containerDpSize.height
    Box(modifier = Modifier
        .fillMaxWidth()
        .defaultMinSize(minHeight = screenHeight / 2)
        .windowInsetsPadding(WindowInsets.systemBars)
        .verticalScroll(rememberScrollState())) {
        PostItem(null,
            id,
            navigator,
            modifier = Modifier.padding(start = 16.dp,end = 16.dp, bottom = 16.dp))
    }
}