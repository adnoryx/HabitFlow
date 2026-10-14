package habitflow.adnoryx.eu.data.repository

import habitflow.adnoryx.eu.data.local.FreezeQuestDao
import habitflow.adnoryx.eu.data.local.HabitDao
import habitflow.adnoryx.eu.data.local.UserProfileDao
import habitflow.adnoryx.eu.data.model.FreezeQuest
import habitflow.adnoryx.eu.data.model.Habit
import habitflow.adnoryx.eu.data.model.HabitCompletion
import habitflow.adnoryx.eu.data.model.UserProfile
import habitflow.adnoryx.eu.domain.LevelSystem
import habitflow.adnoryx.eu.util.DateUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class HabitRepository(
    private val habitDao: HabitDao,
    private val userProfileDao: UserProfileDao,
    private val freezeQuestDao: FreezeQuestDao
) {
    val activeHabits: Flow<List<Habit>> = habitDao.getAllActiveHabits()
    val userProfile: Flow<UserProfile?> = userProfileDao.getUserProfileFlow()
    val freezeQuests: Flow<List<FreezeQuest>> = freezeQuestDao.getAllFreezeQuests()

    fun getCompletionsForDate(dateString: String): Flow<List<HabitCompletion>> {
        return habitDao.getCompletionsForDate(dateString)
    }

    suspend fun ensureProfileAndQuestsInitialized(): Unit = withContext(Dispatchers.IO) {
        val today = DateUtils.getTodayString()
        var profile = userProfileDao.getUserProfile()

        if (profile == null) {
            profile = UserProfile(
                id = 1,
                level = 1,
                currentXp = 0,
                totalXp = 0,
                currentStreak = 0,
                totalFreezes = 1,
                freezesUsedToday = 0,
                lastActiveDate = today
            )
            userProfileDao.insertOrUpdateProfile(profile)
        } else {
            if (profile.lastActiveDate != today) {
                profile = profile.copy(
                    freezesUsedToday = 0,
                    lastActiveDate = today
                )
                userProfileDao.updateProfile(profile)
            }
        }

        val quests = freezeQuestDao.getAllFreezeQuestsList()
        if (quests.isEmpty()) {
            val defaultQuests = listOf(
                FreezeQuest(
                    id = "quest_7day_streak",
                    titleKey = "quest_7day_streak",
                    targetCount = 7,
                    currentProgress = 0,
                    rewardFreezes = 1,
                    rewardXp = 50
                ),
                FreezeQuest(
                    id = "quest_all_routines",
                    titleKey = "quest_all_routines",
                    targetCount = 1,
                    currentProgress = 0,
                    rewardFreezes = 1,
                    rewardXp = 30
                ),
                FreezeQuest(
                    id = "quest_reach_lvl5",
                    titleKey = "quest_reach_lvl5",
                    targetCount = 5,
                    currentProgress = 0,
                    rewardFreezes = 2,
                    rewardXp = 100
                )
            )
            freezeQuestDao.insertAllQuests(defaultQuests)
        }
        updateFreezeQuestProgressInternal()
    }

    suspend fun toggleHabitCompletion(habit: Habit, dateString: String): Unit = withContext(Dispatchers.IO) {
        val existing = habitDao.getCompletionsListForDate(dateString)
            .find { it.habitId == habit.id }

        val profile = userProfileDao.getUserProfile() ?: return@withContext

        var newTotalXp = profile.totalXp

        if (existing == null) {
            habitDao.insertCompletion(HabitCompletion(habitId = habit.id, dateString = dateString))
            newTotalXp += habit.xpValue
        } else {
            habitDao.deleteCompletion(habit.id, dateString)
            newTotalXp = maxOf(0, newTotalXp - habit.xpValue)
        }

        val levelInfo = LevelSystem.calculateLevelInfo(newTotalXp)
        val updatedProfile = profile.copy(
            level = levelInfo.level,
            currentXp = levelInfo.currentLevelXp,
            totalXp = newTotalXp
        )
        userProfileDao.updateProfile(updatedProfile)

        updateStreakAndHabitsInternal()
        updateFreezeQuestProgressInternal()
    }

    suspend fun useDayFreeze(): Boolean = withContext(Dispatchers.IO) {
        val profile = userProfileDao.getUserProfile() ?: return@withContext false
        val today = DateUtils.getTodayString()

        if (profile.freezesUsedToday >= 1) {
            return@withContext false
        }
        if (profile.totalFreezes <= 0) {
            return@withContext false
        }

        val updatedProfile = profile.copy(
            totalFreezes = profile.totalFreezes - 1,
            freezesUsedToday = 1,
            lastFreezeUsedDate = today
        )
        userProfileDao.updateProfile(updatedProfile)
        return@withContext true
    }

    suspend fun claimQuestReward(questId: String): Unit = withContext(Dispatchers.IO) {
        val quest = freezeQuestDao.getAllFreezeQuestsList().find { it.id == questId } ?: return@withContext
        if (!quest.isCompleted || quest.isClaimed) return@withContext

        val profile = userProfileDao.getUserProfile() ?: return@withContext

        val newTotalXp = profile.totalXp + quest.rewardXp
        val levelInfo = LevelSystem.calculateLevelInfo(newTotalXp)

        val updatedProfile = profile.copy(
            totalFreezes = profile.totalFreezes + quest.rewardFreezes,
            level = levelInfo.level,
            currentXp = levelInfo.currentLevelXp,
            totalXp = newTotalXp
        )
        userProfileDao.updateProfile(updatedProfile)

        val updatedQuest = quest.copy(isClaimed = true)
        freezeQuestDao.updateQuest(updatedQuest)
    }

    suspend fun addHabit(title: String, description: String, timeOfDay: habitflow.adnoryx.eu.data.model.TimeOfDay, xpValue: Int): Unit = withContext(Dispatchers.IO) {
        val habit = Habit(
            title = title,
            description = description,
            timeOfDay = timeOfDay,
            xpValue = xpValue
        )
        habitDao.insertHabit(habit)
        updateFreezeQuestProgressInternal()
    }

    suspend fun updateHabit(habit: Habit): Unit = withContext(Dispatchers.IO) {
        habitDao.updateHabit(habit)
    }

    suspend fun deleteHabit(habit: Habit): Unit = withContext(Dispatchers.IO) {
        habitDao.deleteHabit(habit)
    }

    private fun updateStreakAndHabitsInternal() {
        val today = DateUtils.getTodayString()
        val habits = habitDao.getAllActiveHabitsList()
        val profile = userProfileDao.getUserProfile() ?: return

        var overallStreak = 0
        for (habit in habits) {
            val completions = habitDao.getAllCompletionsList().filter { it.habitId == habit.id }
            val completedToday = completions.any { it.dateString == today }
            val streak = if (completedToday) completions.size else completions.size
            if (streak > overallStreak) {
                overallStreak = streak
            }
        }

        val bestStreak = maxOf(profile.bestStreak, overallStreak)
        val updatedProfile = profile.copy(
            currentStreak = overallStreak,
            bestStreak = bestStreak
        )
        userProfileDao.updateProfile(updatedProfile)
    }

    private fun updateFreezeQuestProgressInternal() {
        val profile = userProfileDao.getUserProfile() ?: return
        val today = DateUtils.getTodayString()
        val habits = habitDao.getAllActiveHabitsList()
        val completionsToday = habitDao.getCompletionsListForDate(today)

        val quests = freezeQuestDao.getAllFreezeQuestsList()
        for (quest in quests) {
            var newProgress = quest.currentProgress
            var isCompleted = quest.isCompleted

            when (quest.id) {
                "quest_7day_streak" -> {
                    newProgress = minOf(7, profile.currentStreak)
                    isCompleted = newProgress >= 7
                }
                "quest_all_routines" -> {
                    val target = if (habits.isNotEmpty()) habits.size else 1
                    val progress = completionsToday.size
                    newProgress = if (progress >= target && habits.isNotEmpty()) 1 else 0
                    isCompleted = newProgress >= 1
                }
                "quest_reach_lvl5" -> {
                    newProgress = minOf(5, profile.level)
                    isCompleted = newProgress >= 5
                }
            }

            if (newProgress != quest.currentProgress || isCompleted != quest.isCompleted) {
                freezeQuestDao.updateQuest(
                    quest.copy(
                        currentProgress = newProgress,
                        isCompleted = isCompleted
                    )
                )
            }
        }
    }
}
