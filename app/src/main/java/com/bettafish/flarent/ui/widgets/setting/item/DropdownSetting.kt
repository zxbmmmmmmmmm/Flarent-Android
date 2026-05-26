package com.bettafish.flarent.ui.widgets.setting.item

import androidx.compose.runtime.Composable

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.bettafish.flarent.ui.pages.account.LocalPrefsDataStore
import kotlinx.coroutines.launch


@ExperimentalMaterial3Api
@Composable
fun DropdownSetting(
    key: String,
    title: String,
    modifier: Modifier = Modifier,
    summary: String? = null,
    defaultValue: String? = null,
    onValueChange: ((String) -> Unit)? = null,
    useSelectedAsSummary: Boolean = false,
    dropdownBackgroundColor: Color? = null,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    enabled: Boolean = true,
    leadingIcon: @Composable (() -> Unit)? = null,
    entries: Map<String, String> = mapOf()
) {

    var expanded by rememberSaveable { mutableStateOf(false) }
    val selectionKey = stringPreferencesKey(key)
    val scope = rememberCoroutineScope()

    val datastore = LocalPrefsDataStore.current
    val prefs by remember { datastore.data }.collectAsState(initial = null)

    var value = defaultValue
    prefs?.get(selectionKey)?.also { value = it } // starting value if it exists in datastore

    fun edit(item: Map.Entry<String, String>) = run {
        scope.launch {
            try {
                datastore.edit { preferences ->
                    preferences[selectionKey] = item.key
                }
                expanded = false
                onValueChange?.invoke(item.key)
            } catch (e: Exception) {
                Log.e("DropDownPref", "Could not write pref $key to database. ${e.printStackTrace()}")
            }
        }
    }

    Column {
        TextSetting(
            title = title,
            modifier = modifier,
            summary = when {
                useSelectedAsSummary && value != null -> entries[value]
                useSelectedAsSummary && value == null -> "Not Set"
                else -> summary
            },
            textColor = textColor,
            leadingIcon = leadingIcon,
            enabled = enabled,
            onClick = {
                expanded = true
            },
        )

        Box(
            modifier = Modifier
                .padding(start = 16.dp)
        ) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = if (dropdownBackgroundColor != null) Modifier.background(dropdownBackgroundColor) else Modifier
            ) {
                entries.forEach { item ->
                    DropdownMenuItem(
                        onClick = {
                            edit(item)
                        },
                        text = {
                            Text(
                                text = item.value,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    )
                }
            }
        }
    }
}