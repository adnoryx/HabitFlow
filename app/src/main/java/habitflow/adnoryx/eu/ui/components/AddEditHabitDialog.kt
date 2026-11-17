package habitflow.adnoryx.eu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import habitflow.adnoryx.eu.data.model.Habit
import habitflow.adnoryx.eu.data.model.HabitCategory
import habitflow.adnoryx.eu.data.model.HabitType
import habitflow.adnoryx.eu.data.model.TimeOfDay
import habitflow.adnoryx.eu.ui.theme.GamingBlue
import habitflow.adnoryx.eu.ui.theme.GamingBorder
import habitflow.adnoryx.eu.ui.theme.GamingSurface
import habitflow.adnoryx.eu.ui.theme.GamingSurfaceVariant
import habitflow.adnoryx.eu.ui.theme.TextPrimary
import habitflow.adnoryx.eu.ui.theme.TextSecondary
import habitflow.adnoryx.eu.util.LanguageManager
import java.util.Locale

@Composable
fun AddEditHabitDialog(
    habitToEdit: Habit?,
    onSave: (
        title: String,
        description: String,
        timeOfDay: TimeOfDay,
        xpValue: Int,
        icon: String,
        category: HabitCategory,
        habitType: HabitType,
        targetValue: Int,
        unit: String,
        reminderTime: String?
    ) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val currentLang = LanguageManager.currentLanguage.value

    var title by remember { mutableStateOf(habitToEdit?.title ?: "") }
    var description by remember { mutableStateOf(habitToEdit?.description ?: "") }
    var timeOfDay by remember { mutableStateOf(habitToEdit?.timeOfDay ?: TimeOfDay.ALL_DAY) }
    var xpValue by remember { mutableFloatStateOf(habitToEdit?.xpValue?.toFloat() ?: 20f) }
    var selectedIcon by remember { mutableStateOf(habitToEdit?.icon ?: "⚡") }
    var selectedCategory by remember { mutableStateOf(habitToEdit?.category ?: HabitCategory.GENERAL) }
    var selectedType by remember { mutableStateOf(habitToEdit?.habitType ?: HabitType.BOOLEAN) }
    var targetValueStr by remember {
        mutableStateOf(
            if (habitToEdit != null && habitToEdit.targetValue > 1) habitToEdit.targetValue.toString()
            else when (habitToEdit?.habitType) {
                HabitType.COUNTER -> "8"
                HabitType.TIMER -> "15"
                else -> "1"
            }
        )
    }
    var unit by remember {
        mutableStateOf(
            habitToEdit?.unit?.ifBlank {
                when (habitToEdit.habitType) {
                    HabitType.COUNTER -> "x"
                    HabitType.TIMER -> "min"
                    else -> ""
                }
            } ?: ""
        )
    }
    var reminderTime by remember { mutableStateOf(habitToEdit?.reminderTime) }
    var showTimePickerDialog by remember { mutableStateOf(false) }

    val availableIcons = listOf("⚡", "💧", "🏃", "📚", "🧘", "💪", "🍎", "💤", "💼", "🎯", "🎸", "🧹")

    val chipColors = FilterChipDefaults.filterChipColors(
        selectedContainerColor = GamingBlue,
        selectedLabelColor = Color.White,
        containerColor = GamingSurfaceVariant,
        labelColor = TextPrimary
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GamingSurface,
        shape = RoundedCornerShape(28.dp),
        title = {
            Text(
                text = context.getString(
                    if (habitToEdit == null) habitflow.adnoryx.eu.R.string.add_routine else habitflow.adnoryx.eu.R.string.edit_routine
                ).removePrefix("+ "),
                color = TextPrimary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = when (currentLang.code) {
                        "sk" -> "Ikona návyku"
                        "cs" -> "Ikona návyku"
                        else -> "Habit Icon"
                    },
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableIcons.forEach { emoji ->
                        val isSelected = selectedIcon == emoji
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) GamingBlue.copy(alpha = 0.2f) else GamingSurfaceVariant)
                                .border(
                                    1.5.dp,
                                    if (isSelected) GamingBlue else GamingBorder,
                                    CircleShape
                                )
                                .clickable { selectedIcon = emoji },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 18.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(context.getString(habitflow.adnoryx.eu.R.string.routine_title), color = TextSecondary) },
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = GamingBlue,
                        unfocusedBorderColor = GamingBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(context.getString(habitflow.adnoryx.eu.R.string.routine_desc), color = TextSecondary) },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = GamingBlue,
                        unfocusedBorderColor = GamingBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = when (currentLang.code) {
                        "sk" -> "Kategória"
                        "cs" -> "Kategorie"
                        else -> "Category"
                    },
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HabitCategory.entries.forEach { cat ->
                        val catLabel = getCategoryLabel(cat, currentLang.code)
                        val isSelected = (selectedCategory == cat)
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = cat },
                            shape = RoundedCornerShape(50),
                            colors = chipColors,
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = GamingBorder,
                                selectedBorderColor = GamingBlue
                            ),
                            label = { Text(catLabel, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = when (currentLang.code) {
                        "sk" -> "Typ cieľa"
                        "cs" -> "Typ cíle"
                        else -> "Goal Type"
                    },
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HabitType.entries.forEach { type ->
                        val label = when (type) {
                            HabitType.BOOLEAN -> when (currentLang.code) {
                                "sk" -> "Áno/Nie"
                                "cs" -> "Ano/Ne"
                                else -> "Checkmark"
                            }
                            HabitType.COUNTER -> when (currentLang.code) {
                                "sk" -> "Počítadlo"
                                "cs" -> "Počítadlo"
                                else -> "Counter"
                            }
                            HabitType.TIMER -> when (currentLang.code) {
                                "sk" -> "Časovač"
                                "cs" -> "Časovač"
                                else -> "Timer"
                            }
                        }
                        val isSelected = (selectedType == type)
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                selectedType = type
                                if (type == HabitType.COUNTER && (targetValueStr == "1" || targetValueStr.isBlank())) {
                                    targetValueStr = "8"
                                    if (unit.isBlank() || unit == "min") unit = "pohárov"
                                } else if (type == HabitType.TIMER) {
                                    if (targetValueStr == "1" || targetValueStr == "8") targetValueStr = "15"
                                    unit = "min"
                                }
                            },
                            shape = RoundedCornerShape(50),
                            colors = chipColors,
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = GamingBorder,
                                selectedBorderColor = GamingBlue
                            ),
                            label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) }
                        )
                    }
                }

                if (selectedType != HabitType.BOOLEAN) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = targetValueStr,
                            onValueChange = { value ->
                                if (value.all { it.isDigit() }) {
                                    targetValueStr = value
                                }
                            },
                            label = {
                                Text(
                                    when (selectedType) {
                                        HabitType.COUNTER -> when (currentLang.code) {
                                            "sk", "cs" -> "Cieľový počet"
                                            else -> "Target Count"
                                        }
                                        HabitType.TIMER -> when (currentLang.code) {
                                            "sk", "cs" -> "Cieľ (minúty)"
                                            else -> "Target (minutes)"
                                        }
                                        else -> "Target"
                                    },
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = GamingBlue,
                                unfocusedBorderColor = GamingBorder
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        if (selectedType == HabitType.COUNTER) {
                            OutlinedTextField(
                                value = unit,
                                onValueChange = { unit = it },
                                label = {
                                    Text(
                                        when (currentLang.code) {
                                            "sk", "cs" -> "Jednotka (ml, strán)"
                                            else -> "Unit (ml, pages)"
                                        },
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(14.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary,
                                    focusedBorderColor = GamingBlue,
                                    unfocusedBorderColor = GamingBorder
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = context.getString(habitflow.adnoryx.eu.R.string.time_of_day),
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    TimeOfDay.entries.forEach { tod ->
                        val label = when (tod) {
                            TimeOfDay.ALL_DAY -> "All"
                            TimeOfDay.MORNING -> "Morn"
                            TimeOfDay.AFTERNOON -> "Aft"
                            TimeOfDay.EVENING -> "Eve"
                        }
                        val isSelected = (timeOfDay == tod)
                        FilterChip(
                            selected = isSelected,
                            onClick = { timeOfDay = tod },
                            shape = RoundedCornerShape(50),
                            colors = chipColors,
                            border = FilterChipDefaults.filterChipBorder(
                                enabled = true,
                                selected = isSelected,
                                borderColor = GamingBorder,
                                selectedBorderColor = GamingBlue
                            ),
                            label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Reminder Time Picker
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(GamingSurfaceVariant)
                        .border(1.dp, GamingBorder, RoundedCornerShape(14.dp))
                        .clickable { showTimePickerDialog = true }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = if (reminderTime != null) GamingBlue else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (reminderTime != null) {
                                when (currentLang.code) {
                                    "sk" -> "Pripomienka: $reminderTime"
                                    "cs" -> "Připomínka: $reminderTime"
                                    else -> "Reminder: $reminderTime"
                                }
                            } else {
                                when (currentLang.code) {
                                    "sk" -> "Nastaviť pripomienku (voliteľné)"
                                    "cs" -> "Nastavit připomínku (volitelné)"
                                    else -> "Set daily reminder (optional)"
                                }
                            },
                            color = if (reminderTime != null) TextPrimary else TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (reminderTime != null) {
                        IconButton(
                            onClick = { reminderTime = null },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = context.getString(habitflow.adnoryx.eu.R.string.xp_reward) + ": ${xpValue.toInt()} XP",
                        color = GamingBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Slider(
                    value = xpValue,
                    onValueChange = { xpValue = it },
                    valueRange = 10f..100f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        thumbColor = GamingBlue,
                        activeTrackColor = GamingBlue,
                        inactiveTrackColor = GamingSurfaceVariant
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val finalTarget = if (selectedType == HabitType.BOOLEAN) {
                            1
                        } else {
                            maxOf(1, targetValueStr.toIntOrNull() ?: 1)
                        }
                        val finalUnit = when (selectedType) {
                            HabitType.BOOLEAN -> ""
                            HabitType.TIMER -> "min"
                            HabitType.COUNTER -> unit.trim()
                        }
                        onSave(
                            title.trim(),
                            description.trim(),
                            timeOfDay,
                            xpValue.toInt(),
                            selectedIcon,
                            selectedCategory,
                            selectedType,
                            finalTarget,
                            finalUnit,
                            reminderTime
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = GamingBlue,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(50)
            ) {
                Text(context.getString(habitflow.adnoryx.eu.R.string.save), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(50)
            ) {
                Text(context.getString(habitflow.adnoryx.eu.R.string.cancel), color = TextSecondary)
            }
        }
    )

    if (showTimePickerDialog) {
        ComposeTimePickerDialog(
            initialTime = reminderTime ?: "08:00",
            onTimeSelected = { selected ->
                reminderTime = selected
                showTimePickerDialog = false
            },
            onDismiss = { showTimePickerDialog = false }
        )
    }
}

fun getCategoryLabel(category: HabitCategory, langCode: String): String {
    return when (category) {
        HabitCategory.GENERAL -> when (langCode) {
            "sk" -> "⚡ Všeobecné"
            "cs" -> "⚡ Obecné"
            else -> "⚡ General"
        }
        HabitCategory.HEALTH -> when (langCode) {
            "sk" -> "❤️ Zdravie"
            "cs" -> "❤️ Zdraví"
            else -> "❤️ Health"
        }
        HabitCategory.FITNESS -> when (langCode) {
            "sk" -> "💪 Šport"
            "cs" -> "💪 Sport"
            else -> "💪 Fitness"
        }
        HabitCategory.MIND -> when (langCode) {
            "sk" -> "🧘 Myseľ"
            "cs" -> "🧘 Mysl"
            else -> "🧘 Mind"
        }
        HabitCategory.WORK -> when (langCode) {
            "sk" -> "💼 Práca"
            "cs" -> "💼 Práce"
            else -> "💼 Work"
        }
        HabitCategory.LEARNING -> when (langCode) {
            "sk" -> "📚 Štúdium"
            "cs" -> "📚 Studium"
            else -> "📚 Learning"
        }
    }
}
