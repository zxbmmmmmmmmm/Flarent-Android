package com.bettafish.flarent.ui.pages

import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bettafish.flarent.ui.widgets.DiscussionItem
import com.bettafish.flarent.viewModels.DiscussionsViewModel
import org.koin.androidx.compose.getViewModel

@Composable
@Preview
fun MainPage(modifier: Modifier = Modifier) {
    val viewModel: DiscussionsViewModel = getViewModel()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    val list by viewModel.discussions.collectAsState()
    val typography = MaterialTheme.typography

    LazyColumn(modifier = modifier) {
        items(list) {
            DiscussionItem(it)
        }
    }
}

@Composable
fun IndeterminateCircularIndicator() {
    var loading by remember { mutableStateOf(false) }

    if (!loading) return

    CircularProgressIndicator(
        modifier = Modifier.width(64.dp),
        color = colorScheme.secondary,
        trackColor = colorScheme.surfaceVariant,
    )
}



