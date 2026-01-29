package com.bettafish.flarent.ui.pages

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination<RootGraph>(start=true)
@ExperimentalMaterial3Api
fun MainPage(navigator: DestinationsNavigator){
    DiscussionsPage(navigator = navigator)
}