package habitflow.adnoryx.eu.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import habitflow.adnoryx.eu.data.model.FreezeQuest
import habitflow.adnoryx.eu.data.model.Habit
import habitflow.adnoryx.eu.data.model.HabitCategory
import habitflow.adnoryx.eu.data.model.HabitCompletion
import habitflow.adnoryx.eu.data.model.HabitType
import habitflow.adnoryx.eu.data.model.TimeOfDay
import habitflow.adnoryx.eu.data.model.UserProfile

class Converters {
    @TypeConverter
    fun fromTimeOfDay(timeOfDay: TimeOfDay?): String? = timeOfDay?.name

    @TypeConverter
    fun toTimeOfDay(value: String?): TimeOfDay = if (value != null) {
        try {
            TimeOfDay.valueOf(value)
        } catch (e: Exception) {
            TimeOfDay.ALL_DAY
        }
    } else {
        TimeOfDay.ALL_DAY
    }

    @TypeConverter
    fun fromHabitType(habitType: HabitType?): String? = habitType?.name

    @TypeConverter
    fun toHabitType(value: String?): HabitType = if (value != null) {
        try {
            HabitType.valueOf(value)
        } catch (e: Exception) {
            HabitType.BOOLEAN
        }
    } else {
        HabitType.BOOLEAN
    }

    @TypeConverter
    fun fromHabitCategory(category: HabitCategory?): String? = category?.name

    @TypeConverter
    fun toHabitCategory(value: String?): HabitCategory = if (value != null) {
        try {
            HabitCategory.valueOf(value)
        } catch (e: Exception) {
            HabitCategory.GENERAL
        }
    } else {
        HabitCategory.GENERAL
    }
}

@Database(
    entities = [Habit::class, HabitCompletion::class, UserProfile::class, FreezeQuest::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun freezeQuestDao(): FreezeQuestDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "habitflow_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
