package habitflow.adnoryx.eu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import habitflow.adnoryx.eu.data.model.UserProfile
import habitflow.adnoryx.eu.ui.theme.GamingBackground
import habitflow.adnoryx.eu.ui.theme.GamingBlue
import habitflow.adnoryx.eu.ui.theme.GamingBorder
import habitflow.adnoryx.eu.ui.theme.GamingSurface
import habitflow.adnoryx.eu.ui.theme.StreakFlame
import habitflow.adnoryx.eu.ui.theme.TextPrimary
import habitflow.adnoryx.eu.ui.theme.TextSecondary
import habitflow.adnoryx.eu.ui.theme.WarmButterBorder
import habitflow.adnoryx.eu.ui.theme.WarmButterYellow

@Composable
fun HeaderTopBar(
    userProfile: UserProfile?,
    onSettingsClick: () -> Unit
) {
    val level = userProfile?.level ?: 1
    val streak = userProfile?.currentStreak ?: 0
    val freezes = userProfile?.totalFreezes ?: 0

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(GamingBackground)
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Breadcrumb-style pill like "🏠 HabitFlow / Lvl 2"
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .weight(1f)
                .height(36.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(GamingSurface)
                .border(1.dp, GamingBorder, RoundedCornerShape(14.dp))
                .padding(horizontal = 10.dp)
        ) {
            Text(
                text = "🏠 HabitFlow",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f, fill = false)
            )
            Text(
                text = " / ",
                color = TextSecondary,
                fontSize = 12.sp,
                maxLines = 1,
                softWrap = false
            )
            Text(
                text = "Lvl $level",
                color = GamingBlue,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp,
                maxLines = 1,
                softWrap = false
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .height(36.dp)
                .clip(CircleShape)
                .background(WarmButterYellow)
                .border(1.dp, WarmButterBorder, CircleShape)
                .padding(horizontal = 11.dp)
        ) {
            Text(
                text = "🔥 ${streak}d",
                color = StreakFlame,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                maxLines = 1,
                softWrap = false
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .height(36.dp)
                .clip(CircleShape)
                .background(GamingSurface)
                .border(1.dp, GamingBorder, CircleShape)
                .padding(horizontal = 11.dp)
        ) {
            Text(
                text = "❄️ $freezes",
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                maxLines = 1,
                softWrap = false
            )
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(GamingSurface)
                .border(1.dp, GamingBorder, CircleShape)
                .clickable { onSettingsClick() }
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Settings",
                tint = TextPrimary,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
