package habitflow.adnoryx.eu.data.repository

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import habitflow.adnoryx.eu.data.local.FreezeQuestDao
import habitflow.adnoryx.eu.data.local.HabitDao
import habitflow.adnoryx.eu.data.local.UserProfileDao
import habitflow.adnoryx.eu.data.model.AppBackupData
import habitflow.adnoryx.eu.data.model.UserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class BackupRepository(
    private val habitDao: HabitDao,
    private val userProfileDao: UserProfileDao,
    private val freezeQuestDao: FreezeQuestDao
) {
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    suspend fun exportDataToJson(): String = withContext(Dispatchers.IO) {
        val userProfile = userProfileDao.getUserProfile() ?: UserProfile()
        val habits = habitDao.getAllHabitsList()
        val completions = habitDao.getAllCompletionsList()
        val freezeQuests = freezeQuestDao.getAllFreezeQuestsList()

        val backupData = AppBackupData(
            userProfile = userProfile,
            habits = habits,
            completions = completions,
            freezeQuests = freezeQuests
        )
        gson.toJson(backupData)
    }

    suspend fun importDataFromJson(jsonString: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val backupData = gson.fromJson(jsonString, AppBackupData::class.java)
                ?: return@withContext false

            habitDao.deleteAllCompletions()
            habitDao.deleteAllHabits()
            userProfileDao.deleteProfile()
            freezeQuestDao.deleteAllQuests()

            userProfileDao.insertOrUpdateProfile(backupData.userProfile)
            habitDao.insertAllHabits(backupData.habits)
            habitDao.insertAllCompletions(backupData.completions)
            freezeQuestDao.insertAllQuests(backupData.freezeQuests)

            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
