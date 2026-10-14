package habitflow.adnoryx.eu.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    val id: Int = 1,
    val level: Int = 1,
    val currentXp: Int = 0,
    val totalXp: Int = 0,
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val totalFreezes: Int = 0,
    val freezesUsedToday: Int = 0,
    val lastActiveDate: String = "",
    val lastFreezeUsedDate: String = ""
)
