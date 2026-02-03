package com.bettafish.flarent.ui.widgets.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

@Composable
@ExperimentalMaterial3Api
fun SettingsListItem(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    darkenOnDisable: Boolean = true,
    icon: @Composable (() -> Unit)? = null,
    secondaryText: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
    textColor: Color = MaterialTheme.colorScheme.onBackground,
    minimalHeight: Boolean = false,
    text: @Composable () -> Unit
) {

    val typography = MaterialTheme.typography

    val styledIcon = @Composable {
        val alpha = when {
            enabled || !darkenOnDisable -> 1f
            else -> 0.38f
        }
        CompositionLocalProvider(LocalContentColor provides LocalContentColor.current.copy(alpha = alpha)) {
            icon?.invoke()
        }
    }

    val styledText = applyTextStyle(
        typography.titleSmall,
        textColor,
        when {
            enabled || !darkenOnDisable -> 1f
            else -> 0.38f
        },
        text
    )!!
    val styledSecondaryText = applyTextStyle(
        typography.bodyMedium,
        textColor,
        when {
            enabled || !darkenOnDisable -> 0.6f
            else -> 0.38f
        },
        secondaryText
    )
    val styledTrailing = applyTextStyle(
        typography.bodySmall,
        textColor,
        when {
            enabled || !darkenOnDisable -> 1f
            else -> 0.38f
        },
        trailing
    )

    AnyLine.CustomListItem(
        modifier = modifier,
        minimalHeight,
        styledIcon,
        styledSecondaryText,
        styledTrailing,
        styledText
    )
}

private object AnyLine {
    private val MinHeight = 48.dp
    private val MinHeightSmaller = 32.dp
    private val IconMinPaddedWidth = 40.dp
    private val ContentPadding = 16.dp
    private val VerticalPadding = 16.dp
    private val SingleLinePadding = 16.dp //used when no secondary text is supplied

    @Composable
    fun CustomListItem(
        modifier: Modifier = Modifier,
        minimalHeight: Boolean = true,
        icon: @Composable (() -> Unit)? = null,
        secondaryText: (@Composable (() -> Unit))? = null,
        trailing: @Composable (() -> Unit)? = null,
        text: @Composable (() -> Unit)
    ) {
        Row(
            modifier
                .heightIn(min = if (minimalHeight) MinHeightSmaller else MinHeight)
                .padding(
                    start = ContentPadding,
                    end = ContentPadding,
                    top = when {
                        secondaryText == null && !minimalHeight -> SingleLinePadding
                        else -> VerticalPadding
                    },
                    bottom = when {
                        minimalHeight -> 0.dp
                        secondaryText == null -> SingleLinePadding
                        else -> VerticalPadding
                    }
                )
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (icon != null) {
                val minSize = IconMinPaddedWidth
                Box(
                    Modifier.sizeIn(minWidth = minSize, minHeight = minSize),
                    contentAlignment = Alignment.CenterStart
                ) { icon() }
            }

            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.weight(1f)
            ) {
                text()
                secondaryText?.invoke()
            }

            if (trailing != null) {
                Spacer(modifier = Modifier.width(24.dp))
                trailing.invoke()
            }
        }
    }
}

private fun applyTextStyle(
    textStyle: TextStyle,
    textColor: Color,
    contentAlpha: Float,
    content: @Composable (() -> Unit)?
): @Composable (() -> Unit)? {
    if (content == null) return null
    return {
        val colorWithAlpha = textColor.copy(alpha = contentAlpha)
        val newTextStyle = textStyle.copy(
            color = colorWithAlpha
        )
        CompositionLocalProvider(
            LocalContentColor provides colorWithAlpha,
            LocalTextStyle provides newTextStyle
        ) {
            content()
        }
    }
}
