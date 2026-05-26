package com.bettafish.flarent.ui.pages.account

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.bettafish.flarent.ui.widgets.setting.SettingsListItem
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph


lateinit var LocalPrefsDataStore: ProvidableCompositionLocal<DataStore<Preferences>>

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun SettingsPage() {
    Column{
        SettingsListItem(text = {
            Text("主题")
        })

    }
}