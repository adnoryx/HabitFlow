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
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import habitflow.adnoryx.eu.ui.theme.FreezeIce
import habitflow.adnoryx.eu.ui.theme.GamingBackground
import habitflow.adnoryx.eu.ui.theme.GamingBlue
import habitflow.adnoryx.eu.ui.theme.GamingBorder
import habitflow.adnoryx.eu.ui.theme.GamingSurface
import habitflow.adnoryx.eu.ui.theme.GamingSurfaceVariant
import habitflow.adnoryx.eu.ui.theme.SuccessGreen
import habitflow.adnoryx.eu.ui.theme.TextMuted
import habitflow.adnoryx.eu.ui.theme.TextPrimary
import habitflow.adnoryx.eu.ui.theme.TextSecondary
import habitflow.adnoryx.eu.ui.theme.WarmButterYellow
import habitflow.adnoryx.eu.util.LanguageManager

data class OnboardingPageData(
    val icon: ImageVector,
    val iconColor: Color,
    val titles: Map<String, String>,
    val descs: Map<String, String>
)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    var currentPage by remember { mutableIntStateOf(0) }
    val activeLangCode = LanguageManager.currentLanguage.value.code

    val pages = listOf(
        OnboardingPageData(
            icon = Icons.Default.MilitaryTech,
            iconColor = GamingBlue,
            titles = mapOf(
                "en" to "Warm & Mindful Habit Tracker",
                "sk" to "Prehľadný Habit Tracker",
                "cs" to "Přehledný Habit Tracker",
                "de" to "Klarer & Achtsamer Habit Tracker",
                "es" to "Rastreador de Hábitos Consciente",
                "fr" to "Suivi d'Habitudes Clair & Motivant",
                "pl" to "Przejrzysty Tracker Nawyków",
                "uk" to "Зручний Трекер Звичок",
                "it" to "Habit Tracker Chiaro e Motivante"
            ),
            descs = mapOf(
                "en" to "Build daily routines, earn XP for completions, keep streak momentum, and level up your profile to Level 100+!",
                "sk" to "Buduj denné rutiny, získavaj XP za dokončenia, udržiavaj streak momentum a zvyšuj svoj level až po Level 100+!",
                "cs" to "Buduj denní rutiny, získávej XP za dokončení, udržuj streak momentum a zvyšuj svůj level až po Level 100+!",
                "de" to "Baue tägliche Routinen auf, sammle XP für Abschlüsse, halte deinen Streak aktiv und steige bis auf Level 100+ auf!",
                "es" to "¡Crea rutinas diarias, gana XP por completarlas, mantén tu racha activa y sube tu perfil hasta el Nivel 100+!",
                "fr" to "Créez des routines quotidiennes, gagnez des XP, maintenez votre série et faites évoluer votre profil jusqu'au Niveau 100+ !",
                "pl" to "Buduj codzienne rutyny, zdobywaj XP za ukończenia, utrzymuj serię dni i rozwijaj swój profil aż do Poziomu 100+!",
                "uk" to "Формуйте щоденні рутини, заробляйте XP за виконання, підтримуйте серію днів і підвищуйте свій профіль до Рівня 100+!",
                "it" to "Crea routine giornaliere, guadagna XP per i completamenti, mantieni la tua serie e sali fino al Livello 100+!"
            )
        ),
        OnboardingPageData(
            icon = Icons.Default.AcUnit,
            iconColor = FreezeIce,
            titles = mapOf(
                "en" to "Day Freeze & Quests",
                "sk" to "Day Freeze & Úlohy",
                "cs" to "Day Freeze & Úkoly",
                "de" to "Tages-Freeze & Aufgaben",
                "es" to "Congelador de Día y Misiones",
                "fr" to "Gel de Jour & Quêtes",
                "pl" to "Zamrożenie Dnia i Zadania",
                "uk" to "Заморозка Дня та Завдання",
                "it" to "Congelamento e Missioni"
            ),
            descs = mapOf(
                "en" to "Protect your streak when life gets busy! Save up Day Freezes (max 1 used per day) and complete Freeze Quests.",
                "sk" to "Uchráň svoj streak, keď nemáš čas! Naspor si Day Freezy (max. 1 použitý za deň) a plň Freeze úlohy.",
                "cs" to "Uchraň svůj streak, když nemáš čas! Naspoř si Day Freezy (max. 1 použitý za den) a plň Freeze úkoly.",
                "de" to "Schütze deinen Streak an stressigen Tagen! Sammle Tages-Freezes (max. 1 pro Tag nutzbar) und erfülle Freeze-Aufgaben.",
                "es" to "¡Protege tu racha cuando estés ocupado! Ahorra Congeladores de Día (máx. 1 por día) y completa Misiones de Congelador.",
                "fr" to "Protégez votre série lors des journées chargées ! Économisez des Gels de Jour (max 1 par jour) et accomplissez des Quêtes.",
                "pl" to "Chroń swoją serię, gdy brakuje Ci czasu! Zbieraj Zamrożenia Dnia (maks. 1 dziennie) i wykonuj Zadania Zamrożenia.",
                "uk" to "Захистіть свою серію, коли бракує часу! Зберігайте Заморозки Дня (макс. 1 на день) та виконуйте Завдання Заморозки.",
                "it" to "Proteggi la tua serie quando hai impegni! Risparmia Congelamenti Giornalieri (max 1 al giorno) e completa le Missioni."
            )
        ),
        OnboardingPageData(
            icon = Icons.Default.Security,
            iconColor = SuccessGreen,
            titles = mapOf(
                "en" to "100% Offline & Private",
                "sk" to "100% Offline a Súkromné",
                "cs" to "100% Offline a Soukromé",
                "de" to "100% Offline & Privat",
                "es" to "100% Offline y Privado",
                "fr" to "100% Hors Ligne & Privé",
                "pl" to "100% Offline i Prywatnie",
                "uk" to "100% Офлайн і Приватно",
                "it" to "100% Offline e Privato"
            ),
            descs = mapOf(
                "en" to "Your data stays 100% on your device in a local database. Export and import JSON backups anytime.",
                "sk" to "Tvoje dáta zostávajú 100% v tvojom zariadení v lokálnej databáze. Exportuj a importuj JSON zálohy kedykoľvek.",
                "cs" to "Tvoje data zůstávají 100% ve tvém zařízení v lokální databázi. Exportuj a importuj JSON zálohy kdykoliv.",
                "de" to "Deine Daten bleiben zu 100% lokal auf deinem Gerät. Exportiere und importiere jederzeit JSON-Backups.",
                "es" to "Tus datos permanecen 100% en tu dispositivo en una base de datos local. Exporta e importa copias JSON cuando quieras.",
                "fr" to "Vos données restent 100% sur votre appareil dans une base locale. Exportez et importez des sauvegardes JSON à tout moment.",
                "pl" to "Twoje dane pozostają w 100% na Twoim urządzeniu w lokalnej bazie. Eksportuj i importuj kopie JSON w dowolnej chwili.",
                "uk" to "Ваші дані залишаються на 100% на вашому пристрої в локальній базі. Експортуйте та імпортуйте JSON-копії будь-коли.",
                "it" to "I tuoi dati restano al 100% sul tuo dispositivo in un database locale. Esporta e importa backup JSON in qualsiasi momento."
            )
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GamingBackground)
            .systemBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onFinish) {
                Text(
                    text = when (activeLangCode) {
                        "sk" -> "Preskočiť"
                        "cs" -> "Přeskočit"
                        "de" -> "Überspringen"
                        "es" -> "Omitir"
                        "fr" -> "Passer"
                        "pl" -> "Pomiń"
                        "uk" -> "Пропустити"
                        "it" -> "Salta"
                        else -> "Skip"
                    },
                    color = TextMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        val page = pages[currentPage]
        val title = page.titles[activeLangCode] ?: page.titles["en"].orEmpty()
        val desc = page.descs[activeLangCode] ?: page.descs["en"].orEmpty()

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(32.dp))
                .background(GamingSurface)
                .border(1.dp, GamingBorder, RoundedCornerShape(32.dp))
                .padding(horizontal = 24.dp, vertical = 36.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(WarmButterYellow),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = page.icon,
                    contentDescription = null,
                    tint = page.iconColor,
                    modifier = Modifier.size(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Text(
                text = title,
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = desc,
                color = TextSecondary,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                textAlign = TextAlign.Center
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                pages.indices.forEach { index ->
                    val isSelected = (index == currentPage)
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(if (isSelected) 12.dp else 8.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) GamingBlue else GamingSurfaceVariant)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (currentPage < pages.size - 1) {
                        currentPage++
                    } else {
                        onFinish()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GamingBlue,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = if (currentPage < pages.size - 1) {
                        when (activeLangCode) {
                            "sk" -> "Ďalej"
                            "cs" -> "Dále"
                            "de" -> "Weiter"
                            "es" -> "Siguiente"
                            "fr" -> "Suivant"
                            "pl" -> "Dalej"
                            "uk" -> "Далі"
                            "it" -> "Avanti"
                            else -> "Next"
                        }
                    } else {
                        when (activeLangCode) {
                            "sk" -> "Začať"
                            "cs" -> "Začít"
                            "de" -> "Loslegen"
                            "es" -> "Empezar"
                            "fr" -> "Commencer"
                            "pl" -> "Zaczynamy"
                            "uk" -> "Розпочати"
                            "it" -> "Inizia"
                            else -> "Get Started"
                        }
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
