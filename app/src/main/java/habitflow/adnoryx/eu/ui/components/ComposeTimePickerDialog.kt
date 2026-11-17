package habitflow.adnoryx.eu.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import habitflow.adnoryx.eu.ui.theme.GamingBlue
import habitflow.adnoryx.eu.ui.theme.GamingSurface
import habitflow.adnoryx.eu.ui.theme.GamingSurfaceVariant
import habitflow.adnoryx.eu.ui.theme.TextPrimary
import habitflow.adnoryx.eu.ui.theme.TextSecondary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ComposeTimePickerDialog(
    initialTime: String?,
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val parts = initialTime?.split(":")
    val initialHour = parts?.getOrNull(0)?.toIntOrNull() ?: 8
    val initialMinute = parts?.getOrNull(1)?.toIntOrNull() ?: 0

    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GamingSurface,
        shape = RoundedCornerShape(28.dp),
        title = {
            Text(
                text = context.getString(habitflow.adnoryx.eu.R.string.time_of_day),
                color = TextPrimary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )
        },
        text = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = GamingSurfaceVariant,
                        clockDialSelectedContentColor = Color.White,
                        clockDialUnselectedContentColor = TextPrimary,
                        selectorColor = GamingBlue,
                        containerColor = GamingSurface,
                        timeSelectorSelectedContainerColor = GamingBlue.copy(alpha = 0.2f),
                        timeSelectorUnselectedContainerColor = GamingSurfaceVariant,
                        timeSelectorSelectedContentColor = GamingBlue,
                        timeSelectorUnselectedContentColor = TextPrimary
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val formatted = String.format(
                        Locale.US,
                        "%02d:%02d",
                        timePickerState.hour,
                        timePickerState.minute
                    )
                    onTimeSelected(formatted)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = GamingBlue,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = context.getString(habitflow.adnoryx.eu.R.string.save),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = context.getString(habitflow.adnoryx.eu.R.string.cancel),
                    color = TextSecondary
                )
            }
        }
    )
}
