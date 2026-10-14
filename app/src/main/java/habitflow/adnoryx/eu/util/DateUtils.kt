package habitflow.adnoryx.eu.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class WeekDayItem(
    val dayName: String,
    val dayNumber: String,
    val dateString: String,
    val isToday: Boolean,
    val isSelected: Boolean
)

object DateUtils {
    private val isoDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    fun getTodayString(): String {
        return isoDateFormat.format(Date())
    }

    fun addDays(dateString: String, days: Int): String {
        return try {
            val d = isoDateFormat.parse(dateString) ?: Date()
            val cal = Calendar.getInstance()
            cal.time = d
            cal.add(Calendar.DAY_OF_MONTH, days)
            isoDateFormat.format(cal.time)
        } catch (e: Exception) {
            dateString
        }
    }

    fun formatDateHeader(dateString: String, locale: Locale): String {
        return try {
            val date = isoDateFormat.parse(dateString) ?: Date()
            val dayOfWeekFormat = SimpleDateFormat("EEEE", locale)
            val monthDayFormat = SimpleDateFormat("MMM d", locale)
            val dayName = dayOfWeekFormat.format(date).replaceFirstChar { it.uppercase(locale) }
            val monthDay = monthDayFormat.format(date)
            "$dayName, $monthDay"
        } catch (e: Exception) {
            dateString
        }
    }

    fun getWeekDaysAround(selectedDateString: String, locale: Locale): List<WeekDayItem> {
        val calendar = Calendar.getInstance()
        val todayStr = getTodayString()

        try {
            val selectedDate = isoDateFormat.parse(selectedDateString) ?: Date()
            calendar.time = selectedDate
        } catch (e: Exception) {
            calendar.time = Date()
        }

        calendar.firstDayOfWeek = Calendar.MONDAY
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        val dayNameFormat = SimpleDateFormat("EEE", locale)
        val dayNumFormat = SimpleDateFormat("d", locale)

        val list = mutableListOf<WeekDayItem>()
        for (i in 0 until 7) {
            val date = calendar.time
            val dateStr = isoDateFormat.format(date)
            val dayName = dayNameFormat.format(date).uppercase(locale)
            val dayNum = dayNumFormat.format(date)

            list.add(
                WeekDayItem(
                    dayName = dayName,
                    dayNumber = dayNum,
                    dateString = dateStr,
                    isToday = (dateStr == todayStr),
                    isSelected = (dateStr == selectedDateString)
                )
            )
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        return list
    }
}
