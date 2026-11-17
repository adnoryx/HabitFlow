package habitflow.adnoryx.eu.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import habitflow.adnoryx.eu.data.model.TimeOfDay
import habitflow.adnoryx.eu.ui.theme.CharcoalDarkCard
import habitflow.adnoryx.eu.ui.theme.GamingBorder
import habitflow.adnoryx.eu.ui.theme.GamingSurface
import habitflow.adnoryx.eu.ui.theme.TextPrimary

data class FilterItem(
    val type: TimeOfDay?,
    val label: String
)

@Composable
fun TimeOfDayFilterChips(
    selectedFilter: TimeOfDay?,
    onFilterSelected: (TimeOfDay?) -> Unit
) {
    val context = LocalContext.current

    val filters = listOf(
        FilterItem(null, context.getString(habitflow.adnoryx.eu.R.string.filter_all)),
        FilterItem(TimeOfDay.MORNING, context.getString(habitflow.adnoryx.eu.R.string.filter_morning)),
        FilterItem(TimeOfDay.AFTERNOON, context.getString(habitflow.adnoryx.eu.R.string.filter_afternoon)),
        FilterItem(TimeOfDay.EVENING, context.getString(habitflow.adnoryx.eu.R.string.filter_evening))
    )

    LazyRow(
        modifier = Modifier.padding(vertical = 10.dp)
    ) {
        items(filters) { item ->
            val isSelected = (item.type == selectedFilter)
            val bg = if (isSelected) CharcoalDarkCard else GamingSurface
            val textColor = if (isSelected) Color.White else TextPrimary

            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clip(RoundedCornerShape(50))
                    .background(bg)
                    .border(
                        1.dp,
                        if (isSelected) CharcoalDarkCard else GamingBorder,
                        RoundedCornerShape(50)
                    )
                    .clickable { onFilterSelected(item.type) }
                    .padding(horizontal = 18.dp, vertical = 9.dp)
            ) {
                Text(
                    text = item.label,
                    color = textColor,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
