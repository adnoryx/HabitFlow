package habitflow.adnoryx.eu.ui.theme

import android.app.Activity
import android.content.ContextWrapper
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import habitflow.adnoryx.eu.util.ThemeManager

@Composable
fun HabitFlowTheme(
    content: @Composable () -> Unit
) {
    val isDark by ThemeManager.isDarkMode

    val colorScheme = if (isDark) {
        darkColorScheme(
            primary = GamingBlue,
            secondary = StreakFlame,
            tertiary = FreezeIce,
            background = GamingBackground,
            surface = GamingSurface,
            surfaceVariant = GamingSurfaceVariant,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = TextPrimary,
            onSurface = TextPrimary,
            onSurfaceVariant = TextSecondary,
            outline = GamingBorder
        )
    } else {
        lightColorScheme(
            primary = GamingBlue,
            secondary = StreakFlame,
            tertiary = FreezeIce,
            background = GamingBackground,
            surface = GamingSurface,
            surfaceVariant = GamingSurfaceVariant,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = TextPrimary,
            onSurface = TextPrimary,
            onSurfaceVariant = TextSecondary,
            outline = GamingBorder
        )
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        val statusBarBg = GamingBackground.toArgb()
        val navBarBg = GamingSurface.toArgb()
        SideEffect {
            var ctx = view.context
            while (ctx is ContextWrapper && ctx !is Activity) {
                ctx = ctx.baseContext
            }
            val window = (ctx as? Activity)?.window
            if (window != null) {
                @Suppress("DEPRECATION")
                window.statusBarColor = statusBarBg
                @Suppress("DEPRECATION")
                window.navigationBarColor = navBarBg
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !isDark
                controller.isAppearanceLightNavigationBars = !isDark
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
