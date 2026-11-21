package habitflow.adnoryx.eu.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import habitflow.adnoryx.eu.data.model.Habit
import habitflow.adnoryx.eu.data.model.HabitType
import habitflow.adnoryx.eu.data.model.TimeOfDay
import habitflow.adnoryx.eu.ui.theme.GamingBlue
import habitflow.adnoryx.eu.ui.theme.GamingBorder
import habitflow.adnoryx.eu.ui.theme.GamingSurface
import habitflow.adnoryx.eu.ui.theme.GamingSurfaceVariant
import habitflow.adnoryx.eu.ui.theme.SuccessGreen
import habitflow.adnoryx.eu.ui.theme.TextMuted
import habitflow.adnoryx.eu.ui.theme.TextPrimary
import habitflow.adnoryx.eu.ui.theme.TextSecondary
import habitflow.adnoryx.eu.util.LanguageManager
import kotlinx.coroutines.delay
import java.util.Locale

@Composable
fun HabitItemCard(
    habit: Habit,
    isCompleted: Boolean,
    progressValue: Int = if (isCompleted) habit.targetValue else 0,
    enabled: Boolean = true,
    showCheckbox: Boolean = true,
    showReorderControls: Boolean = false,
    onToggleCompletion: () -> Unit,
    onProgressChange: ((Int) -> Unit)? = null,
    onMoveUp: (() -> Unit)? = null,
    onMoveDown: (() -> Unit)? = null,
    onArchiveClick: (() -> Unit)? = null,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val currentLang = LanguageManager.currentLanguage.value
    var menuExpanded by remember { mutableStateOf(false) }

    // Timer state for HabitType.TIMER
    var isTimerRunning by remember { mutableStateOf(false) }
    var elapsedSecondsInCurrentMinute by remember { mutableIntStateOf(0) }

    LaunchedEffect(isTimerRunning, progressValue, habit.targetValue) {
        if (isTimerRunning) {
            while (isTimerRunning && progressValue < habit.targetValue) {
                delay(1000L)
                elapsedSecondsInCurrentMinute += 1
                if (elapsedSecondsInCurrentMinute >= 60) {
                    elapsedSecondsInCurrentMinute = 0
                    val nextProgress = progressValue + 1
                    onProgressChange?.invoke(nextProgress)
                    if (nextProgress >= habit.targetValue) {
                        isTimerRunning = false
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(GamingSurface)
            .border(
                1.dp,
                if (isCompleted) SuccessGreen.copy(alpha = 0.45f) else GamingBorder,
                RoundedCornerShape(22.dp)
            )
            .padding(horizontal = 14.dp, vertical = 14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left side: soft rounded emoji container + title with strikethrough + inline metadata
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(15.dp))
                            .background(GamingSurfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = habit.icon,
                            fontSize = 21.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = habit.title,
                            color = if (isCompleted) TextSecondary else TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                        )

                        if (habit.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = habit.description,
                                color = TextSecondary,
                                fontSize = 12.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(5.dp))

                        val timeLabel = if (!habit.reminderTime.isNullOrBlank()) {
                            habit.reminderTime
                        } else {
                            when (habit.timeOfDay) {
                                TimeOfDay.MORNING -> context.getString(habitflow.adnoryx.eu.R.string.filter_morning)
                                TimeOfDay.AFTERNOON -> context.getString(habitflow.adnoryx.eu.R.string.filter_afternoon)
                                TimeOfDay.EVENING -> context.getString(habitflow.adnoryx.eu.R.string.filter_evening)
                                TimeOfDay.ALL_DAY -> when (currentLang.code) {
                                    "sk" -> "Celý deň"
                                    "cs" -> "Celý den"
                                    "de" -> "Ganztägig"
                                    "es" -> "Todo el día"
                                    "fr" -> "Toute la journée"
                                    "pl" -> "Cały dzień"
                                    "uk" -> "Весь день"
                                    "it" -> "Tutto il giorno"
                                    else -> "All Day"
                                }
                            }
                        }

                        val catShortLabel = getCategoryLabel(habit.category, currentLang.code)
                            .substringAfter(" ")

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$timeLabel • $catShortLabel • ",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Text(
                                text = "+${habit.xpValue} XP",
                                color = GamingBlue,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Right side: Reorder controls, Menu, and Rounded Square Checkbox
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (showReorderControls && !habit.isArchived) {
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onMoveUp?.invoke()
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowUpward,
                                contentDescription = "Move Up",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        IconButton(
                            onClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                onMoveDown?.invoke()
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDownward,
                                contentDescription = "Move Down",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Box {
                        IconButton(
                            onClick = { menuExpanded = true },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "Options",
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            modifier = Modifier.background(GamingSurface)
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Edit, contentDescription = null, tint = TextPrimary)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            context.getString(habitflow.adnoryx.eu.R.string.edit_routine),
                                            color = TextPrimary
                                        )
                                    }
                                },
                                onClick = {
                                    menuExpanded = false
                                    onEditClick()
                                }
                            )
                            if (onArchiveClick != null) {
                                DropdownMenuItem(
                                    text = {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                if (habit.isArchived) Icons.Default.Unarchive else Icons.Default.Archive,
                                                contentDescription = null,
                                                tint = GamingBlue
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = if (habit.isArchived) {
                                                    when (currentLang.code) {
                                                        "sk" -> "Obnoviť z archívu"
                                                        "cs" -> "Obnovit z archivu"
                                                        "de" -> "Wiederherstellen"
                                                        "es" -> "Restaurar del archivo"
                                                        "fr" -> "Restaurer de l'archive"
                                                        "pl" -> "Przywróć z archiwum"
                                                        "uk" -> "Відновити з архіву"
                                                        "it" -> "Ripristina dall'archivio"
                                                        else -> "Restore from Archive"
                                                    }
                                                } else {
                                                    when (currentLang.code) {
                                                        "sk" -> "Archivovať návyk"
                                                        "cs" -> "Archivovat návyk"
                                                        "de" -> "Routine archivieren"
                                                        "es" -> "Archivar rutina"
                                                        "fr" -> "Archiver la routine"
                                                        "pl" -> "Archiwizuj rutynę"
                                                        "uk" -> "Архівувати рутину"
                                                        "it" -> "Archivia routine"
                                                        else -> "Archive Routine"
                                                    }
                                                },
                                                color = TextPrimary
                                            )
                                        }
                                    },
                                    onClick = {
                                        menuExpanded = false
                                        onArchiveClick()
                                    }
                                )
                            }
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFD9534F))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            context.getString(habitflow.adnoryx.eu.R.string.delete_routine),
                                            color = Color(0xFFD9534F)
                                        )
                                    }
                                },
                                onClick = {
                                    menuExpanded = false
                                    onDeleteClick()
                                }
                            )
                        }
                    }

                    if (showCheckbox) {
                        Spacer(modifier = Modifier.width(4.dp))

                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    when {
                                        isCompleted -> SuccessGreen
                                        else -> GamingSurfaceVariant
                                    }
                                )
                                .border(
                                    1.5.dp,
                                    when {
                                        isCompleted -> SuccessGreen
                                        else -> GamingBorder
                                    },
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    if (enabled) {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onToggleCompletion()
                                    } else {
                                        val msg = when (currentLang.code) {
                                            "sk" -> "Zapisovať plnenie je možné len pre Dnešok!"
                                            "cs" -> "Zapisovat plnění je možné pouze pro Dnešek!"
                                            "de" -> "Abhaken ist nur für Heute möglich!"
                                            "es" -> "¡Los registros solo se permiten para Hoy!"
                                            "fr" -> "Validations autorisées uniquement pour Aujourd'hui !"
                                            "pl" -> "Oznaczanie możliwe tylko dla Dzisiaj!"
                                            "uk" -> "Відмічати виконання можна лише за Сьогодні!"
                                            "it" -> "I completamenti sono consentiti solo per Oggi!"
                                            else -> "Check-ins are only allowed for Today!"
                                        }
                                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Completed",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else if (!enabled) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Locked",
                                    tint = TextMuted,
                                    modifier = Modifier.size(13.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Measurable progress bar & controls for COUNTER and TIMER
            if (habit.habitType != HabitType.BOOLEAN && onProgressChange != null) {
                Spacer(modifier = Modifier.height(10.dp))
                val target = maxOf(1, habit.targetValue)
                val fraction = (progressValue.toFloat() / target.toFloat()).coerceIn(0f, 1f)
                val unitLabel = habit.unit.ifBlank {
                    if (habit.habitType == HabitType.TIMER) "min" else ""
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (habit.habitType == HabitType.TIMER && isTimerRunning) {
                                    val remSec = 60 - elapsedSecondsInCurrentMinute
                                    "$progressValue / $target $unitLabel (${String.format(Locale.US, "00:%02d", remSec)})"
                                } else {
                                    "$progressValue / $target $unitLabel"
                                },
                                color = if (isCompleted) SuccessGreen else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${(fraction * 100).toInt()}%",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { fraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (isCompleted) SuccessGreen else GamingBlue,
                            trackColor = GamingSurfaceVariant,
                            gapSize = 0.dp,
                            drawStopIndicator = {}
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    if (habit.habitType == HabitType.COUNTER) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(GamingSurfaceVariant)
                                    .clickable(enabled = enabled && progressValue > 0) {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        onProgressChange(maxOf(0, progressValue - 1))
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Decrease",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(GamingBlue)
                                    .clickable(enabled = enabled) {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        onProgressChange(progressValue + 1)
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Increase",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    } else if (habit.habitType == HabitType.TIMER) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isTimerRunning) Color(0xFFE65100) else GamingBlue)
                                    .clickable(enabled = enabled) {
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                        isTimerRunning = !isTimerRunning
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "Start/Pause Timer",
                                        tint = Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isTimerRunning) {
                                            when (currentLang.code) {
                                                "sk" -> "Pauza"
                                                "cs" -> "Pauza"
                                                "de" -> "Pause"
                                                "es" -> "Pausa"
                                                "fr" -> "Pause"
                                                "pl" -> "Pauza"
                                                "uk" -> "Пауза"
                                                "it" -> "Pausa"
                                                else -> "Pause"
                                            }
                                        } else {
                                            when (currentLang.code) {
                                                "sk" -> "Štart"
                                                "cs" -> "Start"
                                                "de" -> "Start"
                                                "es" -> "Iniciar"
                                                "fr" -> "Démarrer"
                                                "pl" -> "Start"
                                                "uk" -> "Старт"
                                                "it" -> "Avvia"
                                                else -> "Start"
                                            }
                                        },
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(GamingSurfaceVariant)
                                    .clickable(enabled = enabled) {
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        onProgressChange(progressValue + 1)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+1m",
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
