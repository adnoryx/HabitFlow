package habitflow.adnoryx.eu.ui.components

import androidx.compose.foundation.background
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
import habitflow.adnoryx.eu.ui.theme.GamingSurface
import habitflow.adnoryx.eu.ui.theme.TextSecondary

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
        modifier = Modifier.padding(vertical = 12.dp)
    ) {
        items(filters) { item ->
            val isSelected = (item.type == selectedFilter)
            val bg = if (isSelected) Color.White else GamingSurface
            val textColor = if (isSelected) Color.Black else TextSecondary

            Box(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(bg)
                    .clickable { onFilterSelected(item.type) }
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = item.label,
                    color = textColor,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.sp
                )
            }
        }
    }
}
