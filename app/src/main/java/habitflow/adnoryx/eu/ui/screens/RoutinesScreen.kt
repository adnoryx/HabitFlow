package habitflow.adnoryx.eu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import habitflow.adnoryx.eu.data.model.Habit
import habitflow.adnoryx.eu.ui.components.HabitItemCard
import habitflow.adnoryx.eu.ui.theme.CharcoalDarkCard
import habitflow.adnoryx.eu.ui.theme.GamingBackground
import habitflow.adnoryx.eu.ui.theme.GamingBlue
import habitflow.adnoryx.eu.ui.theme.GamingBorder
import habitflow.adnoryx.eu.ui.theme.GamingSurface
import habitflow.adnoryx.eu.ui.theme.TextMuted
import habitflow.adnoryx.eu.ui.theme.TextPrimary
import habitflow.adnoryx.eu.ui.theme.TextSecondary
import habitflow.adnoryx.eu.util.LanguageManager

@Composable
fun RoutinesScreen(
    habits: List<Habit>,
    archivedHabits: List<Habit>,
    onAddRoutineClick: () -> Unit,
    onMoveHabit: (Habit, Boolean) -> Unit,
    onArchiveHabit: (Habit, Boolean) -> Unit,
    onEditHabit: (Habit) -> Unit,
    onDeleteHabit: (Habit) -> Unit
) {
    val context = LocalContext.current
    val currentLang = LanguageManager.currentLanguage.value
    var showArchived by remember { mutableStateOf(false) }

    val displayedList = if (showArchived) archivedHabits else habits

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = context.getString(habitflow.adnoryx.eu.R.string.nav_routines),
                        color = TextPrimary,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.ExtraBold,
                        maxLines = 1
                    )
                    Text(
                        text = when (currentLang.code) {
                            "sk" -> "${habits.size} aktívnych • ${archivedHabits.size} v archíve"
                            "cs" -> "${habits.size} aktivních • ${archivedHabits.size} v archivu"
                            "de" -> "${habits.size} Aktiv • ${archivedHabits.size} Archiviert"
                            "es" -> "${habits.size} Activas • ${archivedHabits.size} Archivadas"
                            "fr" -> "${habits.size} Actives • ${archivedHabits.size} Archivées"
                            "pl" -> "${habits.size} Aktywnych • ${archivedHabits.size} W archiwum"
                            "uk" -> "${habits.size} Активних • ${archivedHabits.size} В архіві"
                            "it" -> "${habits.size} Attive • ${archivedHabits.size} Archiviate"
                            else -> "${habits.size} Active • ${archivedHabits.size} Archived"
                        },
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Button(
                    onClick = onAddRoutineClick,
                    modifier = Modifier.height(42.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
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
                                .padding(end = 4.dp)
                                .size(17.dp)
                        )
                        Text(
                            text = context.getString(habitflow.adnoryx.eu.R.string.add_routine).removePrefix("+ "),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                FilterChip(
                    selected = !showArchived,
                    onClick = { showArchived = false },
                    shape = RoundedCornerShape(50),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CharcoalDarkCard,
                        selectedLabelColor = Color.White,
                        containerColor = GamingSurface,
                        labelColor = TextPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = !showArchived,
                        borderColor = GamingBorder,
                        selectedBorderColor = CharcoalDarkCard
                    ),
                    label = {
                        Text(
                            when (currentLang.code) {
                                "sk" -> "Aktívne (${habits.size})"
                                "cs" -> "Aktivní (${habits.size})"
                                "de" -> "Aktiv (${habits.size})"
                                "es" -> "Activas (${habits.size})"
                                "fr" -> "Actives (${habits.size})"
                                "pl" -> "Aktywne (${habits.size})"
                                "uk" -> "Активні (${habits.size})"
                                "it" -> "Attive (${habits.size})"
                                else -> "Active (${habits.size})"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
                FilterChip(
                    selected = showArchived,
                    onClick = { showArchived = true },
                    shape = RoundedCornerShape(50),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = CharcoalDarkCard,
                        selectedLabelColor = Color.White,
                        containerColor = GamingSurface,
                        labelColor = TextPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = showArchived,
                        borderColor = GamingBorder,
                        selectedBorderColor = CharcoalDarkCard
                    ),
                    label = {
                        Text(
                            when (currentLang.code) {
                                "sk" -> "Archivované (${archivedHabits.size})"
                                "cs" -> "Archivované (${archivedHabits.size})"
                                "de" -> "Archiviert (${archivedHabits.size})"
                                "es" -> "Archivadas (${archivedHabits.size})"
                                "fr" -> "Archivées (${archivedHabits.size})"
                                "pl" -> "Archiwum (${archivedHabits.size})"
                                "uk" -> "Архівні (${archivedHabits.size})"
                                "it" -> "Archiviate (${archivedHabits.size})"
                                else -> "Archived (${archivedHabits.size})"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            softWrap = false
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        if (displayedList.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (showArchived) {
                            when (currentLang.code) {
                                "sk" -> "Žiadne archivované rutiny."
                                "cs" -> "Žádné archivované rutiny."
                                "de" -> "Keine archivierten Routinen."
                                "es" -> "No hay rutinas archivadas."
                                "fr" -> "Aucune routine archivée."
                                "pl" -> "Brak zarchiwizowanych rutyn."
                                "uk" -> "Немає архівованих рутин."
                                "it" -> "Nessuna routine archiviata."
                                else -> "No archived routines."
                            }
                        } else {
                            context.getString(habitflow.adnoryx.eu.R.string.no_routines_yet)
                        },
                        color = TextMuted,
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            items(displayedList, key = { it.id }) { habit ->
                HabitItemCard(
                    habit = habit,
                    isCompleted = false,
                    showCheckbox = false,
                    showReorderControls = !showArchived,
                    onToggleCompletion = { },
                    onMoveUp = { onMoveHabit(habit, true) },
                    onMoveDown = { onMoveHabit(habit, false) },
                    onArchiveClick = { onArchiveHabit(habit, !habit.isArchived) },
                    onEditClick = { onEditHabit(habit) },
                    onDeleteClick = { onDeleteHabit(habit) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
