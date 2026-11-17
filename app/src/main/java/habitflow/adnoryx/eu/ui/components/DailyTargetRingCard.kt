package habitflow.adnoryx.eu.ui.components

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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import habitflow.adnoryx.eu.data.model.UserProfile
import habitflow.adnoryx.eu.ui.theme.FreezeIce
import habitflow.adnoryx.eu.ui.theme.GamingBlue
import habitflow.adnoryx.eu.ui.theme.GamingBorder
import habitflow.adnoryx.eu.ui.theme.GamingSurface
import habitflow.adnoryx.eu.ui.theme.TextPrimary
import habitflow.adnoryx.eu.ui.theme.WarmButterBorder
import habitflow.adnoryx.eu.ui.theme.WarmButterSubtext
import habitflow.adnoryx.eu.ui.theme.WarmButterTrack
import habitflow.adnoryx.eu.ui.theme.WarmButterYellow

@Composable
fun DailyTargetRingCard(
    userProfile: UserProfile?,
    completedCount: Int,
    totalCount: Int,
    onUseFreezeClicked: () -> Unit
) {
    val context = LocalContext.current
    val percentage = if (totalCount > 0) (completedCount * 100) / totalCount else 0
    val freezesUsedToday = userProfile?.freezesUsedToday ?: 0
    val totalFreezes = userProfile?.totalFreezes ?: 0
    val currentStreak = userProfile?.currentStreak ?: 0
    val level = userProfile?.level ?: 1

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(WarmButterYellow)
            .border(1.dp, WarmButterBorder, RoundedCornerShape(28.dp))
            .padding(20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(GamingSurface)
                        .border(1.dp, GamingBorder, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (percentage >= 100) "☀️" else "🌤️",
                        fontSize = 26.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "$percentage%",
                        color = TextPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 36.sp
                    )
                    Text(
                        text = context.getString(habitflow.adnoryx.eu.R.string.daily_target_ring),
                        color = WarmButterSubtext,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 3-column metrics exactly like Wind / Pressure / Humidity in the inspiration
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = context.getString(habitflow.adnoryx.eu.R.string.completed_routines),
                        color = WarmButterSubtext,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$completedCount / $totalCount",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Column {
                    Text(
                        text = context.getString(habitflow.adnoryx.eu.R.string.current_streak_title),
                        color = WarmButterSubtext,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "🔥 ${currentStreak}d",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Column {
                    Text(
                        text = "Level / Shield",
                        color = WarmButterSubtext,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Lvl $level • ❄️ $totalFreezes",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LinearProgressIndicator(
                progress = { if (totalCount > 0) completedCount.toFloat() / totalCount else 0f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = GamingBlue,
                trackColor = WarmButterTrack,
                gapSize = 0.dp,
                drawStopIndicator = {}
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Day Freeze action pill inside the card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(GamingSurface.copy(alpha = 0.9f))
                    .border(1.dp, GamingBorder, RoundedCornerShape(16.dp))
                    .clickable { onUseFreezeClicked() }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (freezesUsedToday > 0) "❄️" else "🛡️",
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = context.getString(habitflow.adnoryx.eu.R.string.use_day_freeze),
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = context.getString(
                                    habitflow.adnoryx.eu.R.string.freezes_banner_fmt,
                                    freezesUsedToday,
                                    totalFreezes
                                ),
                                color = if (freezesUsedToday > 0) FreezeIce else WarmButterSubtext,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(WarmButterYellow),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            tint = TextPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
