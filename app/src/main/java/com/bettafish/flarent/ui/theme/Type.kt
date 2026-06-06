package com.bettafish.flarent.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.bettafish.flarent.R

// Set of Material typography styles to start with
val EmojiFontFamily = FontFamily(
    Font(resId = R.font.segoe_ui_emoji)
)
val defaultTypography = Typography()
val Typography = Typography(
    displayLarge = defaultTypography.displayLarge,
    displayMedium = defaultTypography.displayMedium,
    displaySmall = defaultTypography.displaySmall,
    headlineLarge = defaultTypography.headlineLarge,
    headlineMedium = defaultTypography.headlineMedium,
    headlineSmall = defaultTypography.headlineSmall,
    titleLarge = defaultTypography.titleLarge,
    titleMedium = defaultTypography.titleMedium,
    titleSmall = defaultTypography.titleSmall,
    bodyLarge = defaultTypography.bodyLarge,
    bodyMedium = defaultTypography.bodyMedium,
    bodySmall = defaultTypography.bodySmall,
    labelLarge = defaultTypography.labelLarge,
    labelMedium = defaultTypography.labelMedium,
    labelSmall = defaultTypography.labelSmall
)

