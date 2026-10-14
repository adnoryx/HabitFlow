package habitflow.adnoryx.eu.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TimeOfDay {
    ALL_DAY,
    MORNING,
    AFTERNOON,
    EVENING
}

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val timeOfDay: TimeOfDay = TimeOfDay.ALL_DAY,
    val xpValue: Int = 20,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val isArchived: Boolean = false
)
