package habitflow.adnoryx.eu.ui.theme

import androidx.compose.ui.graphics.Color
import habitflow.adnoryx.eu.util.ThemeManager

// Dynamic theme-aware colors (Editorial Bento Dark by default, Warm Daylight when switched)
val GamingBackground: Color
    get() = if (ThemeManager.isDarkMode.value) Color(0xFF121316) else Color(0xFFF7F5F0)

val GamingSurface: Color
    get() = if (ThemeManager.isDarkMode.value) Color(0xFF1C1E22) else Color(0xFFFFFFFF)

val GamingSurfaceVariant: Color
    get() = if (ThemeManager.isDarkMode.value) Color(0xFF26292E) else Color(0xFFF0EDE6)

val GamingBorder: Color
    get() = if (ThemeManager.isDarkMode.value) Color(0xFF2E3239) else Color(0xFFE7E2D8)

// Warm Apricot / Terracotta primary accent (works beautifully on both dark espresso & warm cream)
val GamingBlue = Color(0xFFF09A59)
val GamingBlueHover = Color(0xFFE08543)
val GamingBlueGlow = Color(0x26F09A59)

// Editorial Bento accents (adaptive for Dark / Light)
val WarmButterYellow: Color
    get() = if (ThemeManager.isDarkMode.value) Color(0xFF2A2419) else Color(0xFFFDF0B5)

val WarmButterBorder: Color
    get() = if (ThemeManager.isDarkMode.value) Color(0xFF473B24) else Color(0xFFF3DE8A)

val WarmButterSubtext: Color
    get() = if (ThemeManager.isDarkMode.value) Color(0xFFD5C39A) else Color(0xFF6B5E36)

val WarmButterTrack: Color
    get() = if (ThemeManager.isDarkMode.value) Color(0xFF3D3422) else Color(0xFFEDE0A6)

val SageGreenCard: Color
    get() = if (ThemeManager.isDarkMode.value) Color(0xFF2B4230) else Color(0xFF5D7E62)

val SageGreenSoft: Color
    get() = if (ThemeManager.isDarkMode.value) Color(0xFF1E2E22) else Color(0xFFEAF2EC)

val CoralPinkCard: Color
    get() = if (ThemeManager.isDarkMode.value) Color(0xFF4E2928) else Color(0xFFF07B75)

val CharcoalDarkCard: Color
    get() = if (ThemeManager.isDarkMode.value) Color(0xFF2B2F36) else Color(0xFF1F221F)

val CreamCard: Color
    get() = if (ThemeManager.isDarkMode.value) Color(0xFF202227) else Color(0xFFFDF9EC)

val StreakFlame = Color(0xFFF27835)
val FreezeIce = Color(0xFF4DB8D8)

val FreezeIceContainer: Color
    get() = if (ThemeManager.isDarkMode.value) Color(0xFF182C34) else Color(0xFFE6F4F8)

val TextPrimary: Color
    get() = if (ThemeManager.isDarkMode.value) Color(0xFFF4F2ED) else Color(0xFF18181A)

val TextSecondary: Color
    get() = if (ThemeManager.isDarkMode.value) Color(0xFFA3A09A) else Color(0xFF6E6D68)

val TextMuted: Color
    get() = if (ThemeManager.isDarkMode.value) Color(0xFF6E6C67) else Color(0xFFA09E97)

val SuccessGreen = Color(0xFF639A67)
val TargetGreen = Color(0xFF4F8A54)
