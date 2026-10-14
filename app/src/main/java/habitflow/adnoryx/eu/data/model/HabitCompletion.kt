package habitflow.adnoryx.eu.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "habit_completions",
    indices = [Index(value = ["habitId", "dateString"], unique = true)]
)
data class HabitCompletion(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val habitId: Long,
    val dateString: String, // Format "yyyy-MM-dd"
    val completedAt: Long = System.currentTimeMillis()
)
