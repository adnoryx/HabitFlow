package habitflow.adnoryx.eu.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import habitflow.adnoryx.eu.ui.theme.GamingBlue
import habitflow.adnoryx.eu.ui.theme.GamingSurface
import habitflow.adnoryx.eu.ui.theme.TextPrimary
import habitflow.adnoryx.eu.ui.theme.TextSecondary
import habitflow.adnoryx.eu.util.AppLanguage
import habitflow.adnoryx.eu.util.LanguageManager

@Composable
fun LanguageSelectionDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val currentLang = LanguageManager.currentLanguage.value

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GamingSurface,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = context.getString(habitflow.adnoryx.eu.R.string.select_language),
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        },
        text = {
            androidx.compose.foundation.layout.Column {
                AppLanguage.entries.forEach { lang ->
                    val isSelected = (lang == currentLang)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                LanguageManager.setLanguage(context, lang)
                                onDismiss()
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = {
                                LanguageManager.setLanguage(context, lang)
                                onDismiss()
                            },
                            colors = RadioButtonDefaults.colors(selectedColor = GamingBlue)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${lang.flag} ${lang.displayName}",
                            color = TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 16.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = context.getString(habitflow.adnoryx.eu.R.string.cancel),
                    color = TextSecondary
                )
            }
        }
    )
}
