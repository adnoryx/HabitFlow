package habitflow.adnoryx.eu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import habitflow.adnoryx.eu.ui.theme.CharcoalDarkCard
import habitflow.adnoryx.eu.ui.theme.GamingBlue
import habitflow.adnoryx.eu.ui.theme.GamingBorder
import habitflow.adnoryx.eu.ui.theme.GamingSurface
import habitflow.adnoryx.eu.ui.theme.GamingSurfaceVariant
import habitflow.adnoryx.eu.ui.theme.TextPrimary
import habitflow.adnoryx.eu.ui.theme.TextSecondary
import habitflow.adnoryx.eu.util.LanguageManager
import habitflow.adnoryx.eu.util.ThemeManager

@Composable
fun SettingsDialog(
    onDismiss: () -> Unit,
    onLanguageClick: () -> Unit,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
    onReplayOnboarding: () -> Unit
) {
    val context = LocalContext.current
    val currentLang = LanguageManager.currentLanguage.value
    val isDark by ThemeManager.isDarkMode

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = GamingSurface,
        shape = RoundedCornerShape(28.dp),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(GamingSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = GamingBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = when (currentLang.code) {
                        "sk" -> "Nastavenia"
                        "cs" -> "Nastavení"
                        "de" -> "Einstellungen"
                        "es" -> "Ajustes"
                        "fr" -> "Paramètres"
                        "pl" -> "Ustawienia"
                        "uk" -> "Налаштування"
                        "it" -> "Impostazioni"
                        else -> "Settings"
                    },
                    color = TextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = when (currentLang.code) {
                        "sk" -> "Vzhľad (Téma)"
                        "cs" -> "Vzhled (Téma)"
                        "de" -> "Erscheinungsbild"
                        "es" -> "Apariencia"
                        "fr" -> "Apparence"
                        "pl" -> "Wygląd (Motyw)"
                        "uk" -> "Вигляд (Тема)"
                        "it" -> "Aspetto (Tema)"
                        else -> "Appearance"
                    },
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(GamingSurfaceVariant)
                        .border(1.dp, GamingBorder, RoundedCornerShape(18.dp))
                        .clickable { ThemeManager.toggleTheme(context) }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                                contentDescription = null,
                                tint = GamingBlue
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = when (currentLang.code) {
                                    "sk" -> if (isDark) "Tmavý režim" else "Svetlý režim"
                                    "cs" -> if (isDark) "Tmavý režim" else "Světlý režim"
                                    "de" -> if (isDark) "Dunkelmodus" else "Hellmodus"
                                    "es" -> if (isDark) "Modo oscuro" else "Modo claro"
                                    "fr" -> if (isDark) "Mode sombre" else "Mode clair"
                                    "pl" -> if (isDark) "Tryb ciemny" else "Tryb jasny"
                                    "uk" -> if (isDark) "Темний режим" else "Світлий режим"
                                    "it" -> if (isDark) "Modalità scura" else "Modalità chiara"
                                    else -> if (isDark) "Dark Mode" else "Light Mode"
                                },
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                maxLines = 1
                            )
                        }
                        Switch(
                            checked = isDark,
                            onCheckedChange = { ThemeManager.setDarkMode(context, it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = GamingBlue
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = when (currentLang.code) {
                        "sk" -> "Jazyk aplikácie"
                        "cs" -> "Jazyk aplikace"
                        "de" -> "App-Sprache"
                        "es" -> "Idioma de la app"
                        "fr" -> "Langue de l'application"
                        "pl" -> "Język aplikacji"
                        "uk" -> "Мова застосунку"
                        "it" -> "Lingua dell'app"
                        else -> "App Language"
                    },
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(GamingSurfaceVariant)
                        .border(1.dp, GamingBorder, RoundedCornerShape(18.dp))
                        .clickable { onLanguageClick() }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = GamingBlue)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "${currentLang.flag} ${currentLang.displayName}",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                maxLines = 1
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = context.getString(habitflow.adnoryx.eu.R.string.backup_settings),
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(GamingSurfaceVariant)
                        .border(1.dp, GamingBorder, RoundedCornerShape(18.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SdCard, contentDescription = null, tint = GamingBlue)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = when (currentLang.code) {
                                    "sk" -> "Lokálna JSON záloha"
                                    "cs" -> "Lokální JSON záloha"
                                    "de" -> "Lokales JSON-Backup"
                                    "es" -> "Copia local JSON"
                                    "fr" -> "Sauvegarde JSON locale"
                                    "pl" -> "Lokalna kopia JSON"
                                    "uk" -> "Локальна JSON-копія"
                                    "it" -> "Backup JSON locale"
                                    else -> "Offline JSON Backup"
                                },
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        val exportBtnLabel = when (currentLang.code) {
                            "sk" -> "Exportovať"
                            "cs" -> "Exportovat"
                            "de" -> "Exportieren"
                            "es" -> "Exportar"
                            "fr" -> "Exporter"
                            "pl" -> "Eksportuj"
                            "uk" -> "Експорт"
                            "it" -> "Esporta"
                            else -> "Export JSON"
                        }
                        val importBtnLabel = when (currentLang.code) {
                            "sk" -> "Importovať"
                            "cs" -> "Importovat"
                            "de" -> "Importieren"
                            "es" -> "Importar"
                            "fr" -> "Importer"
                            "pl" -> "Importuj"
                            "uk" -> "Імпорт"
                            "it" -> "Importa"
                            else -> "Import JSON"
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = onExportClick,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = CharcoalDarkCard,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(50)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Upload, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = exportBtnLabel,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            OutlinedButton(
                                onClick = onImportClick,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                                shape = RoundedCornerShape(50)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Download, contentDescription = null, tint = TextPrimary, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = importBtnLabel,
                                        color = TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        softWrap = false,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = when (currentLang.code) {
                        "sk" -> "Sprievodca"
                        "cs" -> "Průvodce"
                        "de" -> "Einführung"
                        "es" -> "Guía"
                        "fr" -> "Guide"
                        "pl" -> "Przewodnik"
                        "uk" -> "Посібник"
                        "it" -> "Guida"
                        else -> "Guide"
                    },
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(GamingSurfaceVariant)
                        .border(1.dp, GamingBorder, RoundedCornerShape(18.dp))
                        .clickable { onReplayOnboarding() }
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.HelpOutline, contentDescription = null, tint = GamingBlue)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = when (currentLang.code) {
                                    "sk" -> "Zobraziť návod"
                                    "cs" -> "Zobrazit návod"
                                    "de" -> "Anleitung anzeigen"
                                    "es" -> "Ver guía inicial"
                                    "fr" -> "Afficher le guide"
                                    "pl" -> "Pokaż przewodnik"
                                    "uk" -> "Показати інструкцію"
                                    "it" -> "Mostra guida iniziale"
                                    else -> "Show Onboarding Guide"
                                },
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextSecondary)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = when (currentLang.code) {
                        "sk" -> "Zatvoriť"
                        "cs" -> "Zavřít"
                        "de" -> "Schließen"
                        "es" -> "Cerrar"
                        "fr" -> "Fermer"
                        "pl" -> "Zamknij"
                        "uk" -> "Закрити"
                        "it" -> "Chiudi"
                        else -> "Close"
                    },
                    color = GamingBlue,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}
