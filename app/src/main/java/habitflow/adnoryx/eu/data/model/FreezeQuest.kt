package habitflow.adnoryx.eu.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "freeze_quests")
data class FreezeQuest(
    @PrimaryKey
    val id: String,
    val titleKey: String,
    val targetCount: Int,
    val currentProgress: Int = 0,
    val isCompleted: Boolean = false,
    val isClaimed: Boolean = false,
    val rewardFreezes: Int = 1,
    val rewardXp: Int = 50
)
