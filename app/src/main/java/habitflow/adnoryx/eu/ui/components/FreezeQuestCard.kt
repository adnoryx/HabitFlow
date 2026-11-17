package habitflow.adnoryx.eu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import habitflow.adnoryx.eu.data.model.FreezeQuest
import habitflow.adnoryx.eu.ui.theme.GamingBlue
import habitflow.adnoryx.eu.ui.theme.GamingBorder
import habitflow.adnoryx.eu.ui.theme.GamingSurface
import habitflow.adnoryx.eu.ui.theme.GamingSurfaceVariant
import habitflow.adnoryx.eu.ui.theme.SuccessGreen
import habitflow.adnoryx.eu.ui.theme.TextMuted
import habitflow.adnoryx.eu.ui.theme.TextPrimary
import habitflow.adnoryx.eu.ui.theme.TextSecondary

@Composable
fun FreezeQuestCard(
    quests: List<FreezeQuest>,
    onClaimClick: (String) -> Unit
) {
    val context = LocalContext.current

    if (quests.isEmpty()) return

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = context.getString(habitflow.adnoryx.eu.R.string.freeze_quests_title),
                color = TextPrimary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )
            Text(
                text = "Quests & Rewards",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        quests.forEachIndexed { index, quest ->
            val title = when (quest.id) {
                "quest_7day_streak" -> context.getString(habitflow.adnoryx.eu.R.string.quest_7day_streak)
                "quest_all_routines" -> context.getString(habitflow.adnoryx.eu.R.string.quest_all_routines)
                "quest_reach_lvl5" -> context.getString(habitflow.adnoryx.eu.R.string.quest_reach_lvl5)
                else -> quest.titleKey
            }

            val emoji = when (index % 3) {
                0 -> "💪"
                1 -> "⏰"
                else -> "🏆"
            }

            val progressFraction = if (quest.targetCount > 0) {
                (quest.currentProgress.toFloat() / quest.targetCount.toFloat()).coerceIn(0f, 1f)
            } else 0f

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(GamingSurface)
                    .border(1.dp, GamingBorder, RoundedCornerShape(22.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(GamingSurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = emoji, fontSize = 20.sp)
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Text(
                                    text = title,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${quest.currentProgress}/${quest.targetCount} • +${quest.rewardFreezes} ❄️  +${quest.rewardXp} XP",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        if (quest.isClaimed) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(SuccessGreen.copy(alpha = 0.15f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = context.getString(habitflow.adnoryx.eu.R.string.claimed),
                                    color = SuccessGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else if (quest.isCompleted) {
                            Button(
                                onClick = { onClaimClick(quest.id) },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = GamingBlue,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(50),
                                modifier = Modifier.height(34.dp)
                            ) {
                                Text(
                                    text = context.getString(habitflow.adnoryx.eu.R.string.claim_reward),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(5.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = if (quest.isCompleted) SuccessGreen else GamingBlue,
                        trackColor = GamingSurfaceVariant,
                        gapSize = 0.dp,
                        drawStopIndicator = {}
                    )
                }
            }
        }
    }
}
