package com.bettafish.flarent.ui.theme

import com.bettafish.flarent.R

enum class AppThemeMode(
    val value: String,
    val labelRes: Int
) {
    SYSTEM("system", R.string.theme_system),
    LIGHT("light", R.string.theme_light),
    DARK("dark", R.string.theme_dark);

    companion object {
        const val PreferenceKey = "themeMode"

        fun fromPreference(value: String?): AppThemeMode {
            return entries.firstOrNull { it.value == value } ?: SYSTEM
        }
    }
}
