package com.bettafish.flarent.ui.widgets.setting.Item

import androidx.compose.foundation.clickable
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.bettafish.flarent.ui.widgets.setting.SettingsListItem
import com.bettafish.flarent.ui.widgets.setting.ifNotNullThen

@ExperimentalMaterial3Api
@Composable
fun TextPref(
    title: String,
    modifier: Modifier = Modifier,
    summary: String? = null,
    darkenOnDisable: Boolean = true,
    minimalHeight: Boolean = false,
    onClick: (() -> Unit)? = null,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    enabled: Boolean = onClick != null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null
) {
    SettingsListItem(
        text = { Text(title) },
        modifier = if (onClick != null && enabled) modifier.clickable { onClick() } else modifier,
        enabled = enabled,
        darkenOnDisable = darkenOnDisable,
        textColor = textColor,
        minimalHeight = minimalHeight,
        icon = leadingIcon,
        secondaryText = summary.ifNotNullThen { Text(summary!!) },
        trailing = trailingContent
    )
}