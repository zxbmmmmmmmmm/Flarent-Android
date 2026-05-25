package com.bettafish.flarent.ui.pages.notification

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.bettafish.flarent.ui.widgets.NotificationItem
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import org.koin.androidx.compose.getViewModel

@Composable
@Destination<RootGraph>
fun NotificationsPage(
    navigator: DestinationsNavigator,
    ) {
    val viewModel: NotificationsViewModel = getViewModel()
    val notifications = viewModel.notifications.collectAsLazyPagingItems()
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        if (notifications.loadState.refresh is LoadState.Loading) {
            item {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
        }
        items(count = notifications.itemCount) { index ->
            val notification = notifications[index]
            if (notification != null) {
                NotificationItem(
                    notification = notification,
                )
            }
        }
    }
}