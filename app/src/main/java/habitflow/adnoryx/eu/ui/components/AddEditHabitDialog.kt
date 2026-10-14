package habitflow.adnoryx.eu.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import habitflow.adnoryx.eu.data.model.Habit
import habitflow.adnoryx.eu.data.model.TimeOfDay
import habitflow.adnoryx.eu.ui.theme.GamingBlue
import habitflow.adnoryx.eu.ui.theme.GamingSurface
import habitflow.adnoryx.eu.ui.theme.TextPrimary
import habitflow.adnoryx.eu.ui.theme.TextSecondary

@Composable
fun AddEditHabitDialog(
    habitToEdit: Habit?,
    onSave: (String, String, TimeOfDay, Int) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf(habitToEdit?.title ?: "") }
    var description by remember { mutableStateOf(habitToEdit?.description ?: "") }
    var timeOfDay by remember { mutableStateOf(habitToEdit?.timeOfDay ?: TimeOfDay.ALL_DAY) }
    var xpValue by remember { mutableFloatStateOf(habitToEdit?.xpValue?.toFloat() ?: 20f) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GamingSurface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = context.getString(
                    if (habitToEdit == null) habitflow.adnoryx.eu.R.string.add_routine else habitflow.adnoryx.eu.R.string.edit_routine
                ),
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(context.getString(habitflow.adnoryx.eu.R.string.routine_title), color = TextSecondary) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = GamingBlue,
                        unfocusedBorderColor = Color(0xFF2C394F)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(context.getString(habitflow.adnoryx.eu.R.string.routine_desc), color = TextSecondary) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedBorderColor = GamingBlue,
                        unfocusedBorderColor = Color(0xFF2C394F)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = context.getString(habitflow.adnoryx.eu.R.string.time_of_day),
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    TimeOfDay.entries.forEach { tod ->
                        val label = when (tod) {
                            TimeOfDay.ALL_DAY -> "All"
                            TimeOfDay.MORNING -> "Morn"
                            TimeOfDay.AFTERNOON -> "Aft"
                            TimeOfDay.EVENING -> "Eve"
                        }
                        FilterChip(
                            selected = (timeOfDay == tod),
                            onClick = { timeOfDay = tod },
                            label = { Text(label, fontSize = 11.sp) },
                            modifier = Modifier.padding(end = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

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
                    steps = 8
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onSave(title.trim(), description.trim(), timeOfDay, xpValue.toInt())
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GamingBlue),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(context.getString(habitflow.adnoryx.eu.R.string.save), fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(context.getString(habitflow.adnoryx.eu.R.string.cancel), color = TextSecondary)
            }
        }
    )
}
