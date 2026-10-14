package habitflow.adnoryx.eu.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import habitflow.adnoryx.eu.data.model.Habit
import habitflow.adnoryx.eu.data.model.HabitCompletion
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {
    @Query("SELECT * FROM habits WHERE isArchived = 0 ORDER BY createdAt DESC")
    fun getAllActiveHabits(): Flow<List<Habit>>

    @Query("SELECT * FROM habits WHERE isArchived = 0 ORDER BY createdAt DESC")
    fun getAllActiveHabitsList(): List<Habit>

    @Query("SELECT * FROM habits")
    fun getAllHabitsList(): List<Habit>

    @Query("SELECT * FROM habits WHERE id = :id")
    fun getHabitById(id: Long): Habit?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertHabit(habit: Habit): Long

    @Update
    fun updateHabit(habit: Habit): Int

    @Delete
    fun deleteHabit(habit: Habit): Int

    @Query("SELECT * FROM habit_completions WHERE dateString = :dateString")
    fun getCompletionsForDate(dateString: String): Flow<List<HabitCompletion>>

    @Query("SELECT * FROM habit_completions WHERE dateString = :dateString")
    fun getCompletionsListForDate(dateString: String): List<HabitCompletion>

    @Query("SELECT * FROM habit_completions")
    fun getAllCompletionsList(): List<HabitCompletion>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCompletion(completion: HabitCompletion): Long

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId AND dateString = :dateString")
    fun deleteCompletion(habitId: Long, dateString: String): Int

    @Query("DELETE FROM habit_completions")
    fun deleteAllCompletions(): Int

    @Query("DELETE FROM habits")
    fun deleteAllHabits(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllHabits(habits: List<Habit>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllCompletions(completions: List<HabitCompletion>)
}
