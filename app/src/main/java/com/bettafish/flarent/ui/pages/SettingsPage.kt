package com.bettafish.flarent.ui.pages

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences


lateinit var LocalPrefsDataStore: ProvidableCompositionLocal<DataStore<Preferences>>

fun SettingsPage() {
}