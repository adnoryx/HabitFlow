package habitflow.adnoryx.eu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextOverflow
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
    allCompletions: List<HabitCompletion> = emptyList()
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
        contentPadding = PaddingValues(
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
                    "de" -> "Statistiken, Aktivitäts-Heatmap & Erinnerungen"
                    "es" -> "Analíticas, mapa de actividad y recordatorios"
                    "fr" -> "Statistiques, carte d'activité et rappels"
                    "pl" -> "Statystyki, mapa aktywności i przypomnienia"
                    "uk" -> "Статистика, графік активності та нагадування"
                    "it" -> "Statistiche, mappa attività e promemoria"
                    else -> "Analytics, Activity Heatmap & Reminders"
                },
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(18.dp))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
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
                                "de" -> "Positive Routinen"
                                "es" -> "Hábitos positivos"
                                "fr" -> "Habitudes positives"
                                "pl" -> "Pozytywne nawyki"
                                "uk" -> "Позитивні звички"
                                "it" -> "Abitudini positive"
                                else -> "Positive Habits"
                            },
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "+$weeklyCompletionRate%",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 26.sp,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        val bestLabel = when (currentLang.code) {
                            "sk" -> "Najlepší deň: $bestDayOfWeek"
                            "cs" -> "Nejlepší den: $bestDayOfWeek"
                            "de" -> "Bester Tag: $bestDayOfWeek"
                            "es" -> "Mejor día: $bestDayOfWeek"
                            "fr" -> "Meilleur jour : $bestDayOfWeek"
                            "pl" -> "Najlepszy dzień: $bestDayOfWeek"
                            "uk" -> "Кращий день: $bestDayOfWeek"
                            "it" -> "Giorno top: $bestDayOfWeek"
                            else -> "Best: $bestDayOfWeek"
                        }
                        Text(
                            text = bestLabel,
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

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
                            text = when (currentLang.code) {
                                "sk" -> "Súhrn návykov"
                                "cs" -> "Souhrn návyků"
                                "de" -> "Gewohnheits-Bilanz"
                                "es" -> "Resumen de hábitos"
                                "fr" -> "Bilan d'habitudes"
                                "pl" -> "Podsumowanie"
                                "uk" -> "Підсумок звичок"
                                "it" -> "Riepilogo abitudini"
                                else -> "Habits Wrapped"
                            },
                            color = Color.White.copy(alpha = 0.75f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${completedEntries.size}",
                            color = Color.White,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 26.sp,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        val totalCheckInsLabel = when (currentLang.code) {
                            "sk" -> "Celkovo splnení"
                            "cs" -> "Celkem splnění"
                            "de" -> "Abschlüsse gesamt"
                            "es" -> "Total completados"
                            "fr" -> "Total de validations"
                            "pl" -> "Łącznie ukończeń"
                            "uk" -> "Усього виконань"
                            "it" -> "Totale completamenti"
                            else -> "Total check-ins"
                        }
                        Text(
                            text = mostConsistentHabit?.let { "${it.icon} ${it.title}" } ?: totalCheckInsLabel,
                            color = GamingBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = when (currentLang.code) {
                                    "sk" -> "Obľúbené návyky (7 dní)"
                                    "cs" -> "Oblíbené návyky (7 dní)"
                                    "de" -> "Wochenverlauf (7 Tage)"
                                    "es" -> "Hábitos de la semana"
                                    "fr" -> "Habitudes (7 jours)"
                                    "pl" -> "Ulubione nawyki (7 dni)"
                                    "uk" -> "Активність за 7 днів"
                                    "it" -> "Abitudini (7 giorni)"
                                    else -> "Favorite Habits (7 Days)"
                                },
                                color = TextPrimary,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 17.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = when (currentLang.code) {
                                    "sk" -> "Splnené úlohy za posledných 7 dní"
                                    "cs" -> "Splněné úkoly za posledních 7 dní"
                                    "de" -> "Tägliche Abschlüsse der letzten Woche"
                                    "es" -> "Completados diarios de la última semana"
                                    "fr" -> "Validations quotidiennes sur 7 jours"
                                    "pl" -> "Dzienne ukończenia z ostatniego tygodnia"
                                    "uk" -> "Щоденні виконання за останній тиждень"
                                    "it" -> "Completamenti giornalieri nell'ultima settimana"
                                    else -> "Daily completions over the past week"
                                },
                                color = TextSecondary,
                                fontSize = 12.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

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
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                softWrap = false
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
                                    fontWeight = if (isTodayBar) FontWeight.ExtraBold else FontWeight.Medium,
                                    maxLines = 1
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
                            "de" -> "Monatskonstanz (28 Tage)"
                            "es" -> "Constancia mensual (28 días)"
                            "fr" -> "Régularité mensuelle (28 j)"
                            "pl" -> "Regularność (28 dni)"
                            "uk" -> "Активність за 28 днів"
                            "it" -> "Costanza mensile (28 gg)"
                            else -> "Monthly Consistency (28 Days)"
                        },
                        color = TextPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 17.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when (currentLang.code) {
                            "sk" -> "Krúžky ukazujú dni so splnenými návykmi"
                            "cs" -> "Kroužky ukazují dny se splněnými návyky"
                            "de" -> "Markierte Kreise zeigen deine aktiven Tage"
                            "es" -> "Los círculos resaltados muestran tus días activos"
                            "fr" -> "Les cercles colorés indiquent vos jours actifs"
                            "pl" -> "Podświetlone kółka pokazują Twoje aktywne dni"
                            "uk" -> "Кольорові кружечки показують ваші активні дні"
                            "it" -> "I cerchi evidenziati mostrano i tuoi giorni attivi"
                            else -> "Highlighted circles show your active habit days"
                        },
                        color = TextSecondary,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = when (currentLang.code) {
                                        "sk" -> "Večerný Streak Saver"
                                        "cs" -> "Večerní Streak Saver"
                                        "de" -> "Abend-Streak-Schutz"
                                        "es" -> "Protector de racha nocturno"
                                        "fr" -> "Sauvegarde de série du soir"
                                        "pl" -> "Wieczorny Strażnik Serii"
                                        "uk" -> "Вечірній захист серії"
                                        "it" -> "Salva-serie serale"
                                        else -> "Evening Streak Saver"
                                    },
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = when (currentLang.code) {
                                        "sk" -> "Upozorní na nesplnené návyky"
                                        "cs" -> "Upozorní na nesplněné návyky"
                                        "de" -> "Warnt bei offenen Routinen"
                                        "es" -> "Te avisa si quedan hábitos pendientes"
                                        "fr" -> "Vous alerte s'il reste des habitudes"
                                        "pl" -> "Ostrzega o niewykonanych nawykach"
                                        "uk" -> "Нагадує про невиконані рутини"
                                        "it" -> "Ti avvisa se restano abitudini"
                                        else -> "Warns you if habits remain"
                                    },
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

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
                                        "de" -> "Erinnerungszeit"
                                        "es" -> "Hora de alerta"
                                        "fr" -> "Heure de l'alerte"
                                        "pl" -> "Godzina powiadomienia"
                                        "uk" -> "Час сповіщення"
                                        "it" -> "Orario di avviso"
                                        else -> "Alert Time"
                                    },
                                    color = TextSecondary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1
                                )
                                Text(
                                    text = streakSaverTime,
                                    color = GamingBlue,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 14.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
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
