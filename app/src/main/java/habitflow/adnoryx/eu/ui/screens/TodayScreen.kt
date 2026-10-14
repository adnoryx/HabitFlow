package habitflow.adnoryx.eu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import habitflow.adnoryx.eu.data.model.HabitCompletion
import habitflow.adnoryx.eu.data.model.TimeOfDay
import habitflow.adnoryx.eu.data.model.UserProfile
import habitflow.adnoryx.eu.ui.components.CalendarWeekBar
import habitflow.adnoryx.eu.ui.components.DailyTargetRingCard
import habitflow.adnoryx.eu.ui.components.FreezeQuestCard
import habitflow.adnoryx.eu.ui.components.HabitItemCard
import habitflow.adnoryx.eu.ui.components.TimeOfDayFilterChips
import habitflow.adnoryx.eu.ui.theme.GamingBackground
import habitflow.adnoryx.eu.ui.theme.GamingBlue
import habitflow.adnoryx.eu.ui.theme.GamingSurface
import habitflow.adnoryx.eu.ui.theme.TextMuted
import habitflow.adnoryx.eu.ui.theme.TextPrimary
import habitflow.adnoryx.eu.ui.theme.TextSecondary
import habitflow.adnoryx.eu.util.DateUtils
import habitflow.adnoryx.eu.util.LanguageManager

@Composable
fun TodayScreen(
    userProfile: UserProfile?,
    habits: List<Habit>,
    completions: List<HabitCompletion>,
    freezeQuests: List<FreezeQuest>,
    selectedDate: String,
    timeFilter: TimeOfDay?,
    onDateSelected: (String) -> Unit,
    onFilterSelected: (TimeOfDay?) -> Unit,
    onToggleCompletion: (Habit) -> Unit,
    onUseFreezeClicked: () -> Unit,
    onClaimQuest: (String) -> Unit,
    onAddRoutineClick: () -> Unit,
    onEditHabit: (Habit) -> Unit,
    onDeleteHabit: (Habit) -> Unit
) {
    val context = LocalContext.current
    val currentLang = LanguageManager.currentLanguage.value
    val isToday = (selectedDate == DateUtils.getTodayString())

    val filteredHabits = habits.filter { habit ->
        timeFilter == null || habit.timeOfDay == timeFilter || habit.timeOfDay == TimeOfDay.ALL_DAY
    }

    val completedHabitIds = completions.map { it.habitId }.toSet()
    val completedCount = habits.count { completedHabitIds.contains(it.id) }
    val totalCount = habits.size

    val subtitleText = context.getString(
        habitflow.adnoryx.eu.R.string.routines_completed_fmt,
        completedCount,
        totalCount
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(GamingBackground)
            .padding(horizontal = 16.dp)
    ) {
        item {
            CalendarWeekBar(
                selectedDateString = selectedDate,
                onDateSelected = onDateSelected
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        if (!isToday) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF2B2012))
                        .border(1.dp, Color(0xFFFF9800), RoundedCornerShape(16.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = Color(0xFFFF9800),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = when (currentLang.code) {
                                "sk" -> "Iba na čítanie — Plnenie je možné zapisovať len pre Dnešok!"
                                "cs" -> "Pouze pro čtení — Plnění lze zapisovat pouze pro Dnešek!"
                                else -> "View Only — Check-ins are only allowed for Today!"
                            },
                            color = Color(0xFFFFCC80),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = context.getString(habitflow.adnoryx.eu.R.string.daily_habits_routines),
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = context.getString(habitflow.adnoryx.eu.R.string.today),
                        color = TextPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Button(
                        onClick = onAddRoutineClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GamingBlue,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                            Text(
                                text = context.getString(habitflow.adnoryx.eu.R.string.add_routine),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }

                Text(
                    text = subtitleText,
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            DailyTargetRingCard(
                userProfile = userProfile,
                completedCount = completedCount,
                totalCount = totalCount,
                onUseFreezeClicked = onUseFreezeClicked
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            FreezeQuestCard(
                quests = freezeQuests,
                onClaimClick = onClaimQuest
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            TimeOfDayFilterChips(
                selectedFilter = timeFilter,
                onFilterSelected = onFilterSelected
            )
        }

        if (filteredHabits.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
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
        } else if (timeFilter != null) {
            items(filteredHabits, key = { it.id }) { habit ->
                val isCompleted = completedHabitIds.contains(habit.id)
                HabitItemCard(
                    habit = habit,
                    isCompleted = isCompleted,
                    enabled = isToday,
                    onToggleCompletion = { onToggleCompletion(habit) },
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
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(GamingSurface)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "$categoryDone/${categoryHabits.size}",
                                    color = GamingBlue,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    items(categoryHabits, key = { it.id }) { habit ->
                        val isCompleted = completedHabitIds.contains(habit.id)
                        HabitItemCard(
                            habit = habit,
                            isCompleted = isCompleted,
                            enabled = isToday,
                            onToggleCompletion = { onToggleCompletion(habit) },
                            onEditClick = { onEditHabit(habit) },
                            onDeleteClick = { onDeleteHabit(habit) }
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
