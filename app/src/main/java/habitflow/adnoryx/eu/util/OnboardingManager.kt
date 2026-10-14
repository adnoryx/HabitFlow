package habitflow.adnoryx.eu.util

import android.content.Context

object OnboardingManager {
    private const val PREFS_NAME = "habitflow_prefs"
    private const val KEY_ONBOARDING_SEEN = "has_seen_onboarding_v1"

    fun hasSeenOnboarding(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_ONBOARDING_SEEN, false)
    }

    fun setOnboardingSeen(context: Context, seen: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_ONBOARDING_SEEN, seen).apply()
    }
}
