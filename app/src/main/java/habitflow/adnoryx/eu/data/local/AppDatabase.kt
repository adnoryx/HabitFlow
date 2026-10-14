package habitflow.adnoryx.eu.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import habitflow.adnoryx.eu.data.model.FreezeQuest
import habitflow.adnoryx.eu.data.model.Habit
import habitflow.adnoryx.eu.data.model.HabitCompletion
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
}

@Database(
    entities = [Habit::class, HabitCompletion::class, UserProfile::class, FreezeQuest::class],
    version = 1,
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
