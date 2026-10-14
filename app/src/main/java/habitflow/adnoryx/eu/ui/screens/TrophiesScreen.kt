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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import habitflow.adnoryx.eu.data.model.UserProfile
import habitflow.adnoryx.eu.domain.LevelSystem
import habitflow.adnoryx.eu.ui.theme.FreezeIce
import habitflow.adnoryx.eu.ui.theme.GamingBackground
import habitflow.adnoryx.eu.ui.theme.GamingBlue
import habitflow.adnoryx.eu.ui.theme.GamingBorder
import habitflow.adnoryx.eu.ui.theme.GamingSurface
import habitflow.adnoryx.eu.ui.theme.StreakFlame
import habitflow.adnoryx.eu.ui.theme.TextMuted
import habitflow.adnoryx.eu.ui.theme.TextPrimary
import habitflow.adnoryx.eu.ui.theme.TextSecondary

data class AchievementItem(
    val icon: String,
    val titleRes: Int,
    val descRes: Int,
    val isUnlocked: Boolean
)

@Composable
fun TrophiesScreen(
    userProfile: UserProfile?
) {
    val context = LocalContext.current

    val totalXp = userProfile?.totalXp ?: 0
    val levelInfo = LevelSystem.calculateLevelInfo(totalXp)
    val totalFreezes = userProfile?.totalFreezes ?: 0
    val bestStreak = userProfile?.bestStreak ?: 0

    val achievements = listOf(
        AchievementItem(
            icon = "🚀",
            titleRes = habitflow.adnoryx.eu.R.string.ach_1_title,
            descRes = habitflow.adnoryx.eu.R.string.ach_1_desc,
            isUnlocked = totalXp > 0
        ),
        AchievementItem(
            icon = "⚡",
            titleRes = habitflow.adnoryx.eu.R.string.ach_2_title,
            descRes = habitflow.adnoryx.eu.R.string.ach_2_desc,
            isUnlocked = totalXp >= 50
        ),
        AchievementItem(
            icon = "🌟",
            titleRes = habitflow.adnoryx.eu.R.string.ach_3_title,
            descRes = habitflow.adnoryx.eu.R.string.ach_3_desc,
            isUnlocked = levelInfo.level >= 2
        ),
        AchievementItem(
            icon = "🔥",
            titleRes = habitflow.adnoryx.eu.R.string.ach_4_title,
            descRes = habitflow.adnoryx.eu.R.string.ach_4_desc,
            isUnlocked = bestStreak >= 2
        ),
        AchievementItem(
            icon = "🔥",
            titleRes = habitflow.adnoryx.eu.R.string.ach_5_title,
            descRes = habitflow.adnoryx.eu.R.string.ach_5_desc,
            isUnlocked = bestStreak >= 3
        ),
        AchievementItem(
            icon = "❄️",
            titleRes = habitflow.adnoryx.eu.R.string.ach_6_title,
            descRes = habitflow.adnoryx.eu.R.string.ach_6_desc,
            isUnlocked = totalFreezes >= 1
        ),
        AchievementItem(
            icon = "🛡️",
            titleRes = habitflow.adnoryx.eu.R.string.ach_7_title,
            descRes = habitflow.adnoryx.eu.R.string.ach_7_desc,
            isUnlocked = totalFreezes >= 3
        ),
        AchievementItem(
            icon = "🎖️",
            titleRes = habitflow.adnoryx.eu.R.string.ach_8_title,
            descRes = habitflow.adnoryx.eu.R.string.ach_8_desc,
            isUnlocked = levelInfo.level >= 5
        ),
        AchievementItem(
            icon = "🥈",
            titleRes = habitflow.adnoryx.eu.R.string.ach_9_title,
            descRes = habitflow.adnoryx.eu.R.string.ach_9_desc,
            isUnlocked = levelInfo.level >= 10
        ),
        AchievementItem(
            icon = "🏆",
            titleRes = habitflow.adnoryx.eu.R.string.ach_10_title,
            descRes = habitflow.adnoryx.eu.R.string.ach_10_desc,
            isUnlocked = levelInfo.level >= 25
        ),
        AchievementItem(
            icon = "👑",
            titleRes = habitflow.adnoryx.eu.R.string.ach_11_title,
            descRes = habitflow.adnoryx.eu.R.string.ach_11_desc,
            isUnlocked = levelInfo.level >= 50
        ),
        AchievementItem(
            icon = "💥",
            titleRes = habitflow.adnoryx.eu.R.string.ach_12_title,
            descRes = habitflow.adnoryx.eu.R.string.ach_12_desc,
            isUnlocked = levelInfo.level >= 100
        )
    )

    val unlockedCount = achievements.count { it.isUnlocked }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(GamingBackground)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        item {
            Text(
                text = context.getString(habitflow.adnoryx.eu.R.string.nav_trophies),
                color = TextPrimary,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "$unlockedCount / ${achievements.size} Unlocked Achievements",
                color = TextSecondary,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(GamingSurface)
                    .border(1.dp, GamingBorder, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(GamingBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MilitaryTech,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Level ${levelInfo.level}",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                                Text(
                                    text = context.getString(habitflow.adnoryx.eu.R.string.xp_fmt, levelInfo.currentLevelXp, levelInfo.requiredXpForNextLevel),
                                    color = GamingBlue,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x220088FF))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Rank #${levelInfo.level}",
                                color = GamingBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LinearProgressIndicator(
                        progress = { levelInfo.progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = GamingBlue,
                        trackColor = Color(0xFF1B2333)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(GamingSurface)
                        .padding(16.dp)
                ) {
                    Column {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = GamingBlue)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Total XP", color = TextSecondary, fontSize = 12.sp)
                        Text("$totalXp XP", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(GamingSurface)
                        .padding(16.dp)
                ) {
                    Column {
                        Icon(Icons.Default.Whatshot, contentDescription = null, tint = StreakFlame)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Best Streak", color = TextSecondary, fontSize = 12.sp)
                        Text("${bestStreak}d", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(GamingSurface)
                        .padding(16.dp)
                ) {
                    Column {
                        Icon(Icons.Default.AcUnit, contentDescription = null, tint = FreezeIce)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Freezes", color = TextSecondary, fontSize = 12.sp)
                        Text("$totalFreezes Freezes", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }

        item {
            Text(
                text = context.getString(habitflow.adnoryx.eu.R.string.achievements),
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(achievements) { item ->
            val title = context.getString(item.titleRes)
            val desc = context.getString(item.descRes)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (item.isUnlocked) GamingSurface else Color(0xFF131A26))
                    .border(
                        1.dp,
                        if (item.isUnlocked) GamingBlue.copy(alpha = 0.5f) else GamingBorder,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (item.isUnlocked) Color(0x330088FF) else Color(0xFF1E293B)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.icon,
                            fontSize = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = title,
                            color = if (item.isUnlocked) TextPrimary else TextMuted,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = desc,
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }

                    if (item.isUnlocked) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(GamingBlue)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "UNLOCKED",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
