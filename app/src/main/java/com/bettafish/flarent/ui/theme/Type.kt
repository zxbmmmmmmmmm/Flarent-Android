package com.bettafish.flarent.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.DeviceFontFamilyName
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.bettafish.flarent.R

// Set of Material typography styles to start with
val EmojiFontFamily = FontFamily(
    Font(resId = R.font.segoe_ui_emoji)
)
val defaultTypography = Typography()
val Typography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = EmojiFontFamily),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = EmojiFontFamily),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = EmojiFontFamily),
    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = EmojiFontFamily),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = EmojiFontFamily),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = EmojiFontFamily),
    titleLarge = defaultTypography.titleLarge.copy(fontFamily = EmojiFontFamily),
    titleMedium = defaultTypography.titleMedium.copy(fontFamily = EmojiFontFamily),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = EmojiFontFamily),
    bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = EmojiFontFamily),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = EmojiFontFamily),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = EmojiFontFamily),
    labelLarge = defaultTypography.labelLarge.copy(fontFamily = EmojiFontFamily),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = EmojiFontFamily),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = EmojiFontFamily)
)

