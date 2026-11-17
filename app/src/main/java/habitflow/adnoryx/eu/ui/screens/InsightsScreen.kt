package habitflow.adnoryx.eu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import habitflow.adnoryx.eu.data.model.Habit
import habitflow.adnoryx.eu.data.model.HabitCompletion
import habitflow.adnoryx.eu.data.model.UserProfile
import habitflow.adnoryx.eu.ui.components.ComposeTimePickerDialog
import habitflow.adnoryx.eu.ui.theme.CharcoalDarkCard
import habitflow.adnoryx.eu.ui.theme.GamingBackground
import habitflow.adnoryx.eu.ui.theme.GamingBlue
import habitflow.adnoryx.eu.ui.theme.GamingBorder
import habitflow.adnoryx.eu.ui.theme.GamingSurface
import habitflow.adnoryx.eu.ui.theme.GamingSurfaceVariant
import habitflow.adnoryx.eu.ui.theme.SageGreenCard
import habitflow.adnoryx.eu.ui.theme.SuccessGreen
import habitflow.adnoryx.eu.ui.theme.TextMuted
import habitflow.adnoryx.eu.ui.theme.TextPrimary
import habitflow.adnoryx.eu.ui.theme.TextSecondary
import habitflow.adnoryx.eu.util.DateUtils
import habitflow.adnoryx.eu.util.LanguageManager
import habitflow.adnoryx.eu.util.NotificationHelper
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun InsightsScreen(
    userProfile: UserProfile?,
    habits: List<Habit>,
    allCompletions: List<HabitCompletion> = emptyList(),
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
    onLanguageClick: () -> Unit,
    onReplayOnboarding: () -> Unit
) {
    val context = LocalContext.current
    val currentLang = LanguageManager.currentLanguage.value
    val todayStr = DateUtils.getTodayString()

    var streakSaverEnabled by remember {
        mutableStateOf(NotificationHelper.isStreakSaverEnabled(context))
    }
    var streakSaverTime by remember {
        mutableStateOf(NotificationHelper.getStreakSaverTime(context))
    }
    var showStreakTimePicker by remember { mutableStateOf(false) }

    val completedEntries = remember(allCompletions) {
        allCompletions.filter { it.isCompleted }
    }

    val countByDate = remember(completedEntries) {
        completedEntries.groupingBy { it.dateString }.eachCount()
    }

    val last28Days = remember(todayStr) {
        (27 downTo 0).map { offset ->
            DateUtils.addDays(todayStr, -offset)
        }
    }

    val last7Days = remember(todayStr) {
        (6 downTo 0).map { offset ->
            DateUtils.addDays(todayStr, -offset)
        }
    }

    val mostConsistentHabit = remember(habits, completedEntries) {
        val countsByHabit = completedEntries.groupingBy { it.habitId }.eachCount()
        habits.maxByOrNull { countsByHabit[it.id] ?: 0 }
    }

    val bestDayOfWeek = remember(completedEntries, currentLang) {
        if (completedEntries.isEmpty()) {
            "—"
        } else {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val dayNameFmt = SimpleDateFormat("EEEE", Locale(currentLang.code))
            val counts = mutableMapOf<String, Int>()
            completedEntries.forEach { comp ->
                try {
                    val d = sdf.parse(comp.dateString)
                    if (d != null) {
                        val name = dayNameFmt.format(d).replaceFirstChar { it.uppercase() }
                        counts[name] = (counts[name] ?: 0) + 1
                    }
                } catch (_: Exception) {
                }
            }
            counts.maxByOrNull { it.value }?.key ?: "—"
        }
    }

    val weeklyCompletionRate = remember(last7Days, countByDate, habits.size) {
        if (habits.isEmpty()) 0
        else {
            val totalPossible = habits.size * 7
            val totalDone = last7Days.sumOf { d -> minOf(habits.size, countByDate[d] ?: 0) }
            ((totalDone * 100) / maxOf(1, totalPossible)).coerceIn(0, 100)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(GamingBackground),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 14.dp,
            bottom = 24.dp
        )
    ) {
        item {
            Text(
                text = context.getString(habitflow.adnoryx.eu.R.string.nav_insights),
                color = TextPrimary,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = when (currentLang.code) {
                    "sk" -> "Štatistiky, grafy plnenia a pripomienky"
                    "cs" -> "Statistiky, grafy plnění a připomínky"
                    else -> "Analytics, Activity Heatmap & Reminders"
                },
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Bento Cards: Sage Green "Positive Habits" + Charcoal Dark "Habits Wrapped"
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Sage Green Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(SageGreenCard)
                        .padding(18.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "😎", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = when (currentLang.code) {
                                "sk" -> "Pozitívne návyky"
                                "cs" -> "Pozitivní návyky"
                                else -> "Positive Habits"
                            },
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "+$weeklyCompletionRate%",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 26.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Best: $bestDayOfWeek",
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 11.sp
                        )
                    }
                }

                // Charcoal Dark "Habits Wrapped" Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(24.dp))
                        .background(CharcoalDarkCard)
                        .padding(18.dp)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🎁", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Habits Wrapped",
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${completedEntries.size}",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 26.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = mostConsistentHabit?.let { "${it.icon} ${it.title}" } ?: "Total check-ins",
                            color = GamingBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Favorite Habits / Weekly Bar Chart (inspired by the "Favorite Habits" card with orange highlight bar)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(26.dp))
                    .background(GamingSurface)
                    .border(1.dp, GamingBorder, RoundedCornerShape(26.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = when (currentLang.code) {
                                    "sk" -> "Obľúbené návyky (7 dní)"
                                    "cs" -> "Oblíbené návyky (7 dní)"
                                    else -> "Favorite Habits"
                                },
                                color = TextPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = when (currentLang.code) {
                                    "sk" -> "Denné splnené úlohy za posledný týždeň"
                                    "cs" -> "Denní splněné úkoly za poslední týden"
                                    else -> "Daily completions over the past week"
                                },
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(GamingSurfaceVariant)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Lvl ${userProfile?.level ?: 1}",
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    val maxTarget = maxOf(1, habits.size)
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    val shortDayFmt = SimpleDateFormat("EEE", Locale(currentLang.code))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        last7Days.forEach { dateStr ->
                            val count = countByDate[dateStr] ?: 0
                            val ratio = (count.toFloat() / maxTarget.toFloat()).coerceIn(0.12f, 1f)
                            val dayLabel = try {
                                val parsed = sdf.parse(dateStr)
                                if (parsed != null) shortDayFmt.format(parsed) else dateStr.takeLast(2)
                            } catch (_: Exception) {
                                dateStr.takeLast(2)
                            }
                            val isTodayBar = dateStr == todayStr

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.fillMaxHeight()
                            ) {
                                Text(
                                    text = "$count",
                                    color = if (isTodayBar) GamingBlue else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier
                                        .width(30.dp)
                                        .weight(1f, fill = false)
                                        .fillMaxHeight(ratio)
                                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp, bottomStart = 6.dp, bottomEnd = 6.dp))
                                        .background(
                                            when {
                                                isTodayBar -> GamingBlue
                                                count >= maxTarget && maxTarget > 0 -> SuccessGreen
                                                count > 0 -> GamingBlue.copy(alpha = 0.55f)
                                                else -> GamingSurfaceVariant
                                            }
                                        )
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = dayLabel,
                                    color = if (isTodayBar) TextPrimary else TextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = if (isTodayBar) FontWeight.ExtraBold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // 28-Day Circular Bubble Heatmap (matching the circular calendar bubbles in inspiration)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(26.dp))
                    .background(GamingSurface)
                    .border(1.dp, GamingBorder, RoundedCornerShape(26.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = when (currentLang.code) {
                            "sk" -> "Prehľad mesiaca (28 dní)"
                            "cs" -> "Přehled měsíce (28 dní)"
                            else -> "Monthly Consistency (28 Days)"
                        },
                        color = TextPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when (currentLang.code) {
                            "sk" -> "Krúžky ukazujú dni so splnenými návykmi"
                            "cs" -> "Kroužky ukazují dny se splněnými návyky"
                            else -> "Highlighted circles show your active habit days"
                        },
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    val maxHabits = maxOf(1, habits.size)
                    val weeks = last28Days.chunked(7)
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        weeks.forEach { weekDays ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                weekDays.forEach { dateStr ->
                                    val count = countByDate[dateStr] ?: 0
                                    val isTodayCell = dateStr == todayStr
                                    val bg = when {
                                        isTodayCell && count > 0 -> GamingBlue
                                        count >= maxHabits -> GamingBlue
                                        count > 0 -> GamingSurfaceVariant
                                        else -> GamingSurfaceVariant.copy(alpha = 0.6f)
                                    }
                                    val borderColor = when {
                                        count > 0 && count < maxHabits -> GamingBlue
                                        isTodayCell -> CharcoalDarkCard
                                        else -> Color.Transparent
                                    }
                                    val textColor = when {
                                        (isTodayCell && count > 0) || count >= maxHabits -> Color.White
                                        count > 0 -> TextPrimary
                                        else -> TextSecondary
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(bg)
                                            .border(
                                                width = if (borderColor != Color.Transparent) 2.dp else 0.dp,
                                                color = borderColor,
                                                shape = CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = dateStr.takeLast(2).removePrefix("0"),
                                            color = textColor,
                                            fontSize = 12.sp,
                                            fontWeight = if (count > 0 || isTodayCell) FontWeight.ExtraBold else FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Streak Saver Notification Settings
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(GamingSurface)
                    .border(1.dp, GamingBorder, RoundedCornerShape(24.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(GamingSurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = GamingBlue,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = when (currentLang.code) {
                                        "sk" -> "Večerný Streak Saver"
                                        "cs" -> "Večerní Streak Saver"
                                        else -> "Evening Streak Saver"
                                    },
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = when (currentLang.code) {
                                        "sk" -> "Upozorní ťa, ak máš nesplnené návyky"
                                        "cs" -> "Upozorní tě, pokud máš nesplněné návyky"
                                        else -> "Warns you before midnight if habits remain"
                                    },
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Switch(
                            checked = streakSaverEnabled,
                            onCheckedChange = { enabled ->
                                streakSaverEnabled = enabled
                                NotificationHelper.setStreakSaverEnabled(context, enabled)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = GamingBlue
                            )
                        )
                    }

                    if (streakSaverEnabled) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(GamingSurfaceVariant)
                                .clickable { showStreakTimePicker = true }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = when (currentLang.code) {
                                        "sk" -> "Čas večerného upozornenia"
                                        "cs" -> "Čas večerního upozornění"
                                        else -> "Alert Time"
                                    },
                                    color = TextSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = streakSaverTime,
                                    color = GamingBlue,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Cream Offline Sync Card (inspired by "Sync anywhere with Hebats Mobile App")
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(GamingSurface)
                    .border(1.dp, GamingBorder, RoundedCornerShape(24.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(GamingSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.SdCard, contentDescription = null, tint = GamingBlue)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = context.getString(habitflow.adnoryx.eu.R.string.backup_settings),
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "100% offline JSON export & import",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onExportClick,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CharcoalDarkCard,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(50)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = context.getString(habitflow.adnoryx.eu.R.string.export_data),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = onImportClick,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Download, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = context.getString(habitflow.adnoryx.eu.R.string.import_data),
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(GamingSurface)
                    .border(1.dp, GamingBorder, RoundedCornerShape(20.dp))
                    .clickable { onLanguageClick() }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = GamingBlue)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = context.getString(habitflow.adnoryx.eu.R.string.select_language),
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Text(
                        text = "${currentLang.flag} ${currentLang.displayName}",
                        color = GamingBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(GamingSurface)
                    .border(1.dp, GamingBorder, RoundedCornerShape(20.dp))
                    .clickable { onReplayOnboarding() }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.HelpOutline, contentDescription = null, tint = GamingBlue)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = when (currentLang.code) {
                                "sk" -> "Zobraziť Návod (Onboarding)"
                                "cs" -> "Zobrazit Návod (Onboarding)"
                                else -> "Show Onboarding Guide"
                            },
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    if (showStreakTimePicker) {
        ComposeTimePickerDialog(
            initialTime = streakSaverTime,
            onTimeSelected = { formatted ->
                streakSaverTime = formatted
                NotificationHelper.setStreakSaverTime(context, formatted)
                showStreakTimePicker = false
            },
            onDismiss = { showStreakTimePicker = false }
        )
    }
}
