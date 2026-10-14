package habitflow.adnoryx.eu.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import habitflow.adnoryx.eu.ui.theme.FreezeIce
import habitflow.adnoryx.eu.ui.theme.GamingBackground
import habitflow.adnoryx.eu.ui.theme.GamingBlue
import habitflow.adnoryx.eu.ui.theme.GamingSurface
import habitflow.adnoryx.eu.ui.theme.TextMuted
import habitflow.adnoryx.eu.ui.theme.TextPrimary
import habitflow.adnoryx.eu.ui.theme.TextSecondary

data class OnboardingPageData(
    val icon: ImageVector,
    val iconColor: Color,
    val titleEn: String,
    val titleSk: String,
    val titleCz: String,
    val descEn: String,
    val descSk: String,
    val descCz: String
)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    val context = LocalContext.current
    var currentPage by remember { mutableIntStateOf(0) }

    val pages = listOf(
        OnboardingPageData(
            icon = Icons.Default.MilitaryTech,
            iconColor = GamingBlue,
            titleEn = "Gamified Habit Tracker",
            titleSk = "Gamifikovaný Habit Tracker",
            titleCz = "Gamifikovaný Habit Tracker",
            descEn = "Build daily routines, earn XP for completions, keep streak momentum, and level up your avatar to Level 100+!",
            descSk = "Buduj denné rutiny, získavaj XP za dokončenia, udržiavaj streak momentum a zvyšuj svoj level až po Level 100+!",
            descCz = "Buduj denní rutiny, získávej XP za dokončení, udržuj streak momentum a zvyšuj svůj level až po Level 100+!"
        ),
        OnboardingPageData(
            icon = Icons.Default.AcUnit,
            iconColor = FreezeIce,
            titleEn = "Day Freeze & Quests",
            titleSk = "Day Freeze & Úlohy",
            titleCz = "Day Freeze & Úkoly",
            descEn = "Protect your streak when life gets busy! Save up Day Freezes (max 1 used per day) and complete Freeze Quests.",
            descSk = "Uchráň svoj streak, keď nemáš čas! Nasporme si Day Freezy (max. 1 použitý za deň) a plň Freeze úlohy.",
            descCz = "Uchráň svůj streak, když nemáš čas! Nasporme si Day Freezy (max. 1 použitý za den) a plň Freeze úkoly."
        ),
        OnboardingPageData(
            icon = Icons.Default.Security,
            iconColor = Color(0xFF10B981),
            titleEn = "100% Offline & Private",
            titleSk = "100% Offline a Súkromné",
            titleCz = "100% Offline a Soukromé",
            descEn = "Your data stays 100% on your device in an encrypted local database. Export and import JSON backups anytime.",
            descSk = "Tvoje dáta zostávajú 100% v tvojom zariadení v lokálnej databáze. Exportuj a importuj JSON zálohy kedykoľvek.",
            descCz = "Tvoje data zůstávají 100% ve tvém zařízení v lokální databázi. Exportuj a importuj JSON zálohy kdykoliv."
        )
    )

    val activeLangCode = habitflow.adnoryx.eu.util.LanguageManager.currentLanguage.value.code

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GamingBackground)
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
                        else -> "Skip"
                    },
                    color = TextMuted,
                    fontSize = 14.sp
                )
            }
        }

        val page = pages[currentPage]
        val title = when (activeLangCode) {
            "sk" -> page.titleSk
            "cs" -> page.titleCz
            else -> page.titleEn
        }
        val desc = when (activeLangCode) {
            "sk" -> page.descSk
            "cs" -> page.descCz
            else -> page.descEn
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(GamingSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = page.icon,
                    contentDescription = null,
                    tint = page.iconColor,
                    modifier = Modifier.size(54.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = title,
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                            .background(if (isSelected) GamingBlue else Color(0xFF232D42))
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
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (currentPage < pages.size - 1) {
                        when (activeLangCode) {
                            "sk" -> "Ďalej"
                            "cs" -> "Dále"
                            else -> "Next"
                        }
                    } else {
                        when (activeLangCode) {
                            "sk" -> "Začať Hrať"
                            "cs" -> "Začít Hrát"
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
