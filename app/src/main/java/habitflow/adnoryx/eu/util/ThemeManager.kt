package habitflow.adnoryx.eu.util

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

object ThemeManager {
    private const val PREFS_NAME = "habitflow_theme_prefs"
    private const val KEY_IS_DARK = "is_dark_mode"

    // Default to true (Dark Mode enabled by default)
    private val _isDarkMode = mutableStateOf(true)
    val isDarkMode: State<Boolean> = _isDarkMode

    fun init(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        _isDarkMode.value = prefs.getBoolean(KEY_IS_DARK, true)
    }

    fun setDarkMode(context: Context, isDark: Boolean) {
        _isDarkMode.value = isDark
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_IS_DARK, isDark).apply()
    }

    fun toggleTheme(context: Context) {
        setDarkMode(context, !_isDarkMode.value)
    }
}
