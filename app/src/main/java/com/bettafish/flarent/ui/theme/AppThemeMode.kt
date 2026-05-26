package com.bettafish.flarent.ui.theme

enum class AppThemeMode(
    val value: String,
    val label: String
) {
    SYSTEM("system", "跟随系统"),
    LIGHT("light", "亮色"),
    DARK("dark", "暗色");

    companion object {
        const val PreferenceKey = "themeMode"

        fun fromPreference(value: String?): AppThemeMode {
            return entries.firstOrNull { it.value == value } ?: SYSTEM
        }
    }
}
