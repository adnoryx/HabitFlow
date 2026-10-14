package habitflow.adnoryx.eu.util

import android.content.Context
import android.content.res.Configuration
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import java.util.Locale

enum class AppLanguage(val code: String, val displayName: String, val flag: String) {
    ENGLISH("en", "English", "🇬🇧"),
    SLOVAK("sk", "Slovenčina", "🇸🇰"),
    CZECH("cs", "Čeština", "🇨🇿"),
    GERMAN("de", "Deutsch", "🇩🇪"),
    SPANISH("es", "Español", "🇪🇸"),
    FRENCH("fr", "Français", "🇫🇷"),
    POLISH("pl", "Polski", "🇵🇱"),
    UKRAINIAN("uk", "Ukrajina", "🇺🇦"),
    ITALIAN("it", "Italiano", "🇮🇹")
}

object LanguageManager {
    private const val PREFS_NAME = "habitflow_prefs"
    private const val KEY_LANG = "selected_language"

    private val _currentLanguage = mutableStateOf(AppLanguage.ENGLISH)
    val currentLanguage: State<AppLanguage> = _currentLanguage

    fun init(context: Context) {
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val code = prefs.getString(KEY_LANG, AppLanguage.ENGLISH.code) ?: AppLanguage.ENGLISH.code
            val lang = AppLanguage.entries.find { it.code == code } ?: AppLanguage.ENGLISH
            _currentLanguage.value = lang
            val locale = Locale(lang.code)
            Locale.setDefault(locale)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setLanguage(context: Context, language: AppLanguage) {
        try {
            _currentLanguage.value = language
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putString(KEY_LANG, language.code).apply()
            val locale = Locale(language.code)
            Locale.setDefault(locale)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateContextLocale(context: Context, language: AppLanguage): Context {
        return try {
            val locale = Locale(language.code)
            Locale.setDefault(locale)
            val config = Configuration(context.resources.configuration)
            config.setLocale(locale)
            context.createConfigurationContext(config)
        } catch (e: Exception) {
            context
        }
    }
}
