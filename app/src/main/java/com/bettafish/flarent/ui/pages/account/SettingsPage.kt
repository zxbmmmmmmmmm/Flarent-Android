package com.bettafish.flarent.ui.pages.account

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.bettafish.flarent.App
import com.bettafish.flarent.ui.theme.AppThemeMode
import com.bettafish.flarent.ui.widgets.BackNavigationIcon
import com.bettafish.flarent.ui.widgets.setting.item.DropdownSetting
import com.bettafish.flarent.utils.appSettings
import com.bettafish.flarent.utils.dataStore
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

val LocalPrefsDataStore: ProvidableCompositionLocal<DataStore<Preferences>> = compositionLocalOf {
    error("LocalPrefsDataStore not provided")
}

@OptIn(ExperimentalMaterial3Api::class)
@Destination<RootGraph>
@Composable
fun SettingsPage(navigator: DestinationsNavigator) {
    val context = LocalContext.current
    val datastore = context.dataStore
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
                val themeMode = App.INSTANCE.appSettings.themeMode!!
                DropdownSetting(
                    title = "主题",
                    leadingIcon = {
                        val icon = when(AppThemeMode.fromPreference(themeMode)){
                            AppThemeMode.SYSTEM -> Icons.Default.WbSunny
                            AppThemeMode.DARK -> Icons.Default.DarkMode
                            AppThemeMode.LIGHT -> if (isSystemInDarkTheme()) Icons.Default.DarkMode else Icons.Default.WbSunny
                        }
                        Icon(icon, null)
                    },
                    summary = AppThemeMode.fromPreference(themeMode).label,
                    key = themeMode,
                    entries = AppThemeMode.entries.associate{ it.value to it.label },
                    onValueChange = {
                        App.INSTANCE.appSettings.themeMode = it
                    }
                )
            }
        }
    }


}
