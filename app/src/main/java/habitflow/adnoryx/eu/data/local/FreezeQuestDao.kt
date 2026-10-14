package habitflow.adnoryx.eu.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import habitflow.adnoryx.eu.data.model.FreezeQuest
import kotlinx.coroutines.flow.Flow

@Dao
interface FreezeQuestDao {
    @Query("SELECT * FROM freeze_quests")
    fun getAllFreezeQuests(): Flow<List<FreezeQuest>>

    @Query("SELECT * FROM freeze_quests")
    fun getAllFreezeQuestsList(): List<FreezeQuest>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertOrUpdateQuest(quest: FreezeQuest)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllQuests(quests: List<FreezeQuest>)

    @Update
    fun updateQuest(quest: FreezeQuest): Int

    @Query("DELETE FROM freeze_quests")
    fun deleteAllQuests(): Int
}
