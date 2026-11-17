package habitflow.adnoryx.eu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import habitflow.adnoryx.eu.data.model.FreezeQuest
import habitflow.adnoryx.eu.data.model.Habit
import habitflow.adnoryx.eu.data.model.HabitCategory
import habitflow.adnoryx.eu.data.model.HabitCompletion
import habitflow.adnoryx.eu.data.model.TimeOfDay
import habitflow.adnoryx.eu.data.model.UserProfile
import habitflow.adnoryx.eu.ui.components.CalendarWeekBar
import habitflow.adnoryx.eu.ui.components.DailyTargetRingCard
import habitflow.adnoryx.eu.ui.components.FreezeQuestCard
import habitflow.adnoryx.eu.ui.components.HabitItemCard
import habitflow.adnoryx.eu.ui.components.TimeOfDayFilterChips
import habitflow.adnoryx.eu.ui.components.getCategoryLabel
import habitflow.adnoryx.eu.ui.theme.CharcoalDarkCard
import habitflow.adnoryx.eu.ui.theme.GamingBackground
import habitflow.adnoryx.eu.ui.theme.GamingBlue
import habitflow.adnoryx.eu.ui.theme.GamingBorder
import habitflow.adnoryx.eu.ui.theme.GamingSurface
import habitflow.adnoryx.eu.ui.theme.GamingSurfaceVariant
import habitflow.adnoryx.eu.ui.theme.TextMuted
import habitflow.adnoryx.eu.ui.theme.TextPrimary
import habitflow.adnoryx.eu.ui.theme.TextSecondary
import habitflow.adnoryx.eu.ui.theme.WarmButterBorder
import habitflow.adnoryx.eu.ui.theme.WarmButterYellow
import habitflow.adnoryx.eu.util.DateUtils
import habitflow.adnoryx.eu.util.LanguageManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TodayScreen(
    userProfile: UserProfile?,
    habits: List<Habit>,
    completions: List<HabitCompletion>,
    freezeQuests: List<FreezeQuest>,
    selectedDate: String,
    timeFilter: TimeOfDay?,
    categoryFilter: HabitCategory?,
    onDateSelected: (String) -> Unit,
    onFilterSelected: (TimeOfDay?) -> Unit,
    onCategoryFilterSelected: (HabitCategory?) -> Unit,
    onToggleCompletion: (Habit) -> Unit,
    onProgressChange: (Habit, Int) -> Unit,
    onUseFreezeClicked: () -> Unit,
    onClaimQuest: (String) -> Unit,
    onAddRoutineClick: () -> Unit,
    onArchiveHabit: (Habit) -> Unit,
    onEditHabit: (Habit) -> Unit,
    onDeleteHabit: (Habit) -> Unit
) {
    val context = LocalContext.current
    val currentLang = LanguageManager.currentLanguage.value
    val locale = Locale(currentLang.code)
    val isToday = (selectedDate == DateUtils.getTodayString())

    val filteredHabits = habits.filter { habit ->
        val matchesTime = timeFilter == null || habit.timeOfDay == timeFilter || habit.timeOfDay == TimeOfDay.ALL_DAY
        val matchesCategory = categoryFilter == null || habit.category == categoryFilter
        matchesTime && matchesCategory
    }

    val completionByHabitId = completions.associateBy { it.habitId }
    val completedHabitIds = completions.filter { it.isCompleted }.map { it.habitId }.toSet()
    val completedCount = habits.count { completedHabitIds.contains(it.id) }
    val totalCount = habits.size

    val subtitleText = context.getString(
        habitflow.adnoryx.eu.R.string.routines_completed_fmt,
        completedCount,
        totalCount
    )

    val dayGreeting = remember(selectedDate, currentLang) {
        try {
            val iso = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val parsed = iso.parse(selectedDate) ?: Date()
            val dayName = SimpleDateFormat("EEEE", locale).format(parsed)
                .replaceFirstChar { it.uppercase(locale) }
            when (currentLang.code) {
                "sk" -> "Krásny $dayName 👋"
                "cs" -> "Krásný $dayName 👋"
                else -> "Happy\n$dayName 👋"
            }
        } catch (_: Exception) {
            "Happy Today 👋"
        }
    }

    val formattedSubtitleDate = remember(selectedDate, currentLang, subtitleText) {
        try {
            val iso = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val parsed = iso.parse(selectedDate) ?: Date()
            val datePart = SimpleDateFormat("d MMM yyyy", locale).format(parsed)
            "$datePart  •  $subtitleText"
        } catch (_: Exception) {
            subtitleText
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(GamingBackground),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 6.dp,
            bottom = 24.dp
        )
    ) {
        // Editorial "Happy Tuesday 👋" Header + Warm Apricot Pill CTA
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = dayGreeting,
                    color = TextPrimary,
                    fontSize = 32.sp,
                    lineHeight = 38.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = formattedSubtitleDate,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onAddRoutineClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GamingBlue,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(50)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 6.dp)
                                .size(18.dp)
                        )
                        Text(
                            text = context.getString(habitflow.adnoryx.eu.R.string.add_routine).removePrefix("+ "),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Calendar Card ("December, 2023" style)
        item {
            CalendarWeekBar(
                selectedDateString = selectedDate,
                onDateSelected = onDateSelected
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (!isToday) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(WarmButterYellow)
                        .border(1.dp, WarmButterBorder, RoundedCornerShape(18.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFFB4690E),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = when (currentLang.code) {
                                "sk" -> "Iba na čítanie — Plnenie je možné zapisovať len pre Dnešok!"
                                "cs" -> "Pouze pro čtení — Plnění lze zapisovat pouze pro Dnešek!"
                                else -> "View Only — Check-ins are only allowed for Today!"
                            },
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Weather-style Daily Target Ring Card
        item {
            DailyTargetRingCard(
                userProfile = userProfile,
                completedCount = completedCount,
                totalCount = totalCount,
                onUseFreezeClicked = onUseFreezeClicked
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Today's Todos Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (currentLang.code) {
                        "sk" -> "Dnešné úlohy"
                        "cs" -> "Dnešní úkoly"
                        else -> "Today's Todos"
                    },
                    color = TextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
                Text(
                    text = "$completedCount / $totalCount",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            TimeOfDayFilterChips(
                selectedFilter = timeFilter,
                onFilterSelected = onFilterSelected
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = (categoryFilter == null),
                    onClick = { onCategoryFilterSelected(null) },
                    shape = RoundedCornerShape(50),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GamingBlue,
                        selectedLabelColor = Color.White,
                        containerColor = GamingSurface,
                        labelColor = TextPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = (categoryFilter == null),
                        borderColor = GamingBorder,
                        selectedBorderColor = GamingBlue
                    ),
                    label = {
                        Text(
                            text = when (currentLang.code) {
                                "sk" -> "Všetky kategórie"
                                "cs" -> "Všechny kategorie"
                                else -> "All Categories"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                )
                HabitCategory.entries.forEach { cat ->
                    val isSelected = (categoryFilter == cat)
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            onCategoryFilterSelected(if (isSelected) null else cat)
                        },
                        shape = RoundedCornerShape(50),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GamingBlue,
                            selectedLabelColor = Color.White,
                            containerColor = GamingSurface,
                            labelColor = TextPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = GamingBorder,
                            selectedBorderColor = GamingBlue
                        ),
                        label = {
                            Text(
                                text = getCategoryLabel(cat, currentLang.code),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    )
                }
            }
        }

        if (filteredHabits.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 28.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(GamingSurface)
                        .border(1.dp, GamingBorder, RoundedCornerShape(24.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (habits.isEmpty()) {
                            context.getString(habitflow.adnoryx.eu.R.string.no_routines_yet)
                        } else {
                            context.getString(habitflow.adnoryx.eu.R.string.no_routines_time_of_day)
                        },
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                }
            }
        } else if (timeFilter != null || categoryFilter != null) {
            items(filteredHabits, key = { it.id }) { habit ->
                val comp = completionByHabitId[habit.id]
                val isCompleted = comp?.isCompleted == true
                val progressValue = comp?.progressValue ?: 0
                HabitItemCard(
                    habit = habit,
                    isCompleted = isCompleted,
                    progressValue = progressValue,
                    enabled = isToday,
                    onToggleCompletion = { onToggleCompletion(habit) },
                    onProgressChange = { newProg -> onProgressChange(habit, newProg) },
                    onArchiveClick = { onArchiveHabit(habit) },
                    onEditClick = { onEditHabit(habit) },
                    onDeleteClick = { onDeleteHabit(habit) }
                )
            }
        } else {
            val timeCategories = listOf(
                TimeOfDay.MORNING to when (currentLang.code) {
                    "sk" -> "🌅 Ranné rutiny"
                    "cs" -> "🌅 Ranní rutiny"
                    else -> "🌅 Morning Routines"
                },
                TimeOfDay.AFTERNOON to when (currentLang.code) {
                    "sk" -> "☀️ Popoludňajšie rutiny"
                    "cs" -> "☀️ Odpolední rutiny"
                    else -> "☀️ Afternoon Routines"
                },
                TimeOfDay.EVENING to when (currentLang.code) {
                    "sk" -> "🌙 Večerné rutiny"
                    "cs" -> "🌙 Večerní rutiny"
                    else -> "🌙 Evening Routines"
                },
                TimeOfDay.ALL_DAY to when (currentLang.code) {
                    "sk" -> "📅 Celodenné rutiny"
                    "cs" -> "📅 Celodenní rutiny"
                    else -> "📅 All-Day Routines"
                }
            )

            timeCategories.forEach { (tod, headerLabel) ->
                val categoryHabits = filteredHabits.filter { it.timeOfDay == tod }
                if (categoryHabits.isNotEmpty()) {
                    item(key = "header_${tod.name}") {
                        val categoryDone = categoryHabits.count { completedHabitIds.contains(it.id) }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp, bottom = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = headerLabel,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(GamingSurfaceVariant)
                                    .padding(horizontal = 10.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "$categoryDone/${categoryHabits.size}",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    items(categoryHabits, key = { it.id }) { habit ->
                        val comp = completionByHabitId[habit.id]
                        val isCompleted = comp?.isCompleted == true
                        val progressValue = comp?.progressValue ?: 0
                        HabitItemCard(
                            habit = habit,
                            isCompleted = isCompleted,
                            progressValue = progressValue,
                            enabled = isToday,
                            onToggleCompletion = { onToggleCompletion(habit) },
                            onProgressChange = { newProg -> onProgressChange(habit, newProg) },
                            onArchiveClick = { onArchiveHabit(habit) },
                            onEditClick = { onEditHabit(habit) },
                            onDeleteClick = { onDeleteHabit(habit) }
                        )
                    }
                }
            }
        }

        // Should Do! / Freeze Quests at the bottom
        item {
            Spacer(modifier = Modifier.height(20.dp))
            FreezeQuestCard(
                quests = freezeQuests,
                onClaimClick = onClaimQuest
            )
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
