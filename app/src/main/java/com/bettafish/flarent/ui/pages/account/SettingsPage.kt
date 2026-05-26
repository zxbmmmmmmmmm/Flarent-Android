package com.bettafish.flarent.ui.pages.account

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bettafish.flarent.ui.theme.AppThemeMode
import com.bettafish.flarent.ui.widgets.BackNavigationIcon
import com.bettafish.flarent.ui.widgets.setting.Item.TextPref
import com.bettafish.flarent.utils.dataStore
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

val LocalPrefsDataStore: ProvidableCompositionLocal<DataStore<Preferences>> = compositionLocalOf {
    error("LocalPrefsDataStore not provided")
}

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun SettingsPage(navigator: DestinationsNavigator) {
    val context = LocalContext.current
    val datastore = context.dataStore
    val scope = rememberCoroutineScope()
    val themePreferenceKey = stringPreferencesKey(AppThemeMode.PreferenceKey)
    val prefs by remember(datastore) { datastore.data }.collectAsState(initial = null)
    val currentMode = AppThemeMode.fromPreference(prefs?.get(themePreferenceKey))
    var showThemeDialog by remember { mutableStateOf(false) }

    fun updateThemeMode(themeMode: AppThemeMode) {
        scope.launch {
            datastore.edit { preferences ->
                preferences[themePreferenceKey] = themeMode.value
            }
        }
    }

    CompositionLocalProvider(LocalPrefsDataStore provides datastore) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("设置") },
                    navigationIcon = {
                        BackNavigationIcon { navigator.navigateUp() }
                    }
                )
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                TextPref(
                    title = "主题",
                    summary = currentMode.label,
                    onClick = { showThemeDialog = true }
                )
            }
        }
    }

    if (showThemeDialog) {
        AlertDialog(
            onDismissRequest = { showThemeDialog = false },
            title = { Text("选择主题") },
            text = {
                Column {
                    AppThemeMode.values().forEach { themeMode ->
                        val onSelect = {
                            updateThemeMode(themeMode)
                            showThemeDialog = false
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onSelect)
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = currentMode == themeMode,
                                onClick = onSelect
                            )
                            Text(
                                text = themeMode.label,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("关闭")
                }
            },
            dismissButton = {
                TextButton(onClick = { showThemeDialog = false }) {
                    Text("取消")
                }
            }
        )
    }
}
