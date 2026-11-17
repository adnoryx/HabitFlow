package habitflow.adnoryx.eu.data.repository

import habitflow.adnoryx.eu.data.local.FreezeQuestDao
import habitflow.adnoryx.eu.data.local.HabitDao
import habitflow.adnoryx.eu.data.local.UserProfileDao
import habitflow.adnoryx.eu.data.model.FreezeQuest
import habitflow.adnoryx.eu.data.model.Habit
import habitflow.adnoryx.eu.data.model.HabitCategory
import habitflow.adnoryx.eu.data.model.HabitCompletion
import habitflow.adnoryx.eu.data.model.HabitType
import habitflow.adnoryx.eu.data.model.TimeOfDay
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
    val archivedHabits: Flow<List<Habit>> = habitDao.getAllArchivedHabits()
    val allCompletions: Flow<List<HabitCompletion>> = habitDao.getAllCompletionsFlow()
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
        updateStreakAndHabitsInternal()
        updateFreezeQuestProgressInternal()
    }

    /**
     * Toggles a boolean habit completion or completes/resets a measurable habit.
     * Returns true if the habit just became completed.
     */
    suspend fun toggleHabitCompletion(habit: Habit, dateString: String): Boolean = withContext(Dispatchers.IO) {
        val existing = habitDao.getCompletionsListForDate(dateString)
            .find { it.habitId == habit.id }

        val profile = userProfileDao.getUserProfile() ?: return@withContext false

        var newTotalXp = profile.totalXp
        val becameCompleted: Boolean

        if (existing == null || !existing.isCompleted) {
            habitDao.insertCompletion(
                HabitCompletion(
                    id = existing?.id ?: 0,
                    habitId = habit.id,
                    dateString = dateString,
                    progressValue = maxOf(1, habit.targetValue),
                    isCompleted = true
                )
            )
            newTotalXp += habit.xpValue
            becameCompleted = true
        } else {
            habitDao.deleteCompletion(habit.id, dateString)
            newTotalXp = maxOf(0, newTotalXp - habit.xpValue)
            becameCompleted = false
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
        becameCompleted
    }

    /**
     * Updates progress for COUNTER or TIMER habits.
     * Returns true if the habit transitioned from incomplete to completed.
     */
    suspend fun updateHabitProgress(habit: Habit, dateString: String, newProgress: Int): Boolean = withContext(Dispatchers.IO) {
        val existing = habitDao.getCompletionsListForDate(dateString)
            .find { it.habitId == habit.id }

        val profile = userProfileDao.getUserProfile() ?: return@withContext false
        val target = maxOf(1, habit.targetValue)
        val clampedProgress = maxOf(0, newProgress)
        val wasCompleted = existing?.isCompleted == true
        val nowCompleted = clampedProgress >= target

        if (clampedProgress == 0) {
            if (existing != null) {
                habitDao.deleteCompletion(habit.id, dateString)
            }
        } else {
            habitDao.insertCompletion(
                HabitCompletion(
                    id = existing?.id ?: 0,
                    habitId = habit.id,
                    dateString = dateString,
                    progressValue = clampedProgress,
                    isCompleted = nowCompleted
                )
            )
        }

        var newTotalXp = profile.totalXp
        if (!wasCompleted && nowCompleted) {
            newTotalXp += habit.xpValue
        } else if (wasCompleted && !nowCompleted) {
            newTotalXp = maxOf(0, newTotalXp - habit.xpValue)
        }

        if (newTotalXp != profile.totalXp) {
            val levelInfo = LevelSystem.calculateLevelInfo(newTotalXp)
            val updatedProfile = profile.copy(
                level = levelInfo.level,
                currentXp = levelInfo.currentLevelXp,
                totalXp = newTotalXp
            )
            userProfileDao.updateProfile(updatedProfile)
        }

        updateStreakAndHabitsInternal()
        updateFreezeQuestProgressInternal()
        (!wasCompleted && nowCompleted)
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

    suspend fun buyFreezeWithXp(costXp: Int = 150): Boolean = withContext(Dispatchers.IO) {
        val profile = userProfileDao.getUserProfile() ?: return@withContext false
        if (profile.totalXp < costXp) return@withContext false

        val newTotalXp = profile.totalXp - costXp
        val levelInfo = LevelSystem.calculateLevelInfo(newTotalXp)
        val updatedProfile = profile.copy(
            totalFreezes = profile.totalFreezes + 1,
            level = levelInfo.level,
            currentXp = levelInfo.currentLevelXp,
            totalXp = newTotalXp
        )
        userProfileDao.updateProfile(updatedProfile)
        updateFreezeQuestProgressInternal()
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

    suspend fun addHabit(
        title: String,
        description: String,
        timeOfDay: TimeOfDay,
        xpValue: Int,
        icon: String = "⚡",
        category: HabitCategory = HabitCategory.GENERAL,
        habitType: HabitType = HabitType.BOOLEAN,
        targetValue: Int = 1,
        unit: String = "",
        reminderTime: String? = null
    ): Habit = withContext(Dispatchers.IO) {
        val currentActive = habitDao.getAllActiveHabitsList()
        val nextSortOrder = (currentActive.maxOfOrNull { it.sortOrder } ?: -1) + 1
        val habit = Habit(
            title = title,
            description = description,
            timeOfDay = timeOfDay,
            xpValue = xpValue,
            icon = icon,
            category = category,
            habitType = habitType,
            targetValue = maxOf(1, targetValue),
            unit = unit,
            sortOrder = nextSortOrder,
            reminderTime = reminderTime
        )
        val id = habitDao.insertHabit(habit)
        updateFreezeQuestProgressInternal()
        habit.copy(id = id)
    }

    suspend fun updateHabit(habit: Habit): Unit = withContext(Dispatchers.IO) {
        habitDao.updateHabit(habit)
        updateFreezeQuestProgressInternal()
    }

    suspend fun archiveHabit(habit: Habit, archived: Boolean): Unit = withContext(Dispatchers.IO) {
        habitDao.updateHabit(habit.copy(isArchived = archived))
        updateStreakAndHabitsInternal()
        updateFreezeQuestProgressInternal()
    }

    suspend fun moveHabitOrder(habit: Habit, moveUp: Boolean): Unit = withContext(Dispatchers.IO) {
        val habits = habitDao.getAllActiveHabitsList().toMutableList()
        val index = habits.indexOfFirst { it.id == habit.id }
        if (index == -1) return@withContext
        val targetIndex = if (moveUp) index - 1 else index + 1
        if (targetIndex !in habits.indices) return@withContext

        val swapped = habits[targetIndex]
        habits[targetIndex] = habits[index]
        habits[index] = swapped

        val updated = habits.mapIndexed { idx, h -> h.copy(sortOrder = idx) }
        habitDao.updateHabits(updated)
    }

    suspend fun deleteHabit(habit: Habit): Unit = withContext(Dispatchers.IO) {
        habitDao.deleteHabit(habit)
        updateStreakAndHabitsInternal()
        updateFreezeQuestProgressInternal()
    }

    private fun updateStreakAndHabitsInternal() {
        val habits = habitDao.getAllActiveHabitsList()
        val allCompletions = habitDao.getAllCompletionsList().filter { it.isCompleted }
        val profile = userProfileDao.getUserProfile() ?: return

        var overallStreak = 0
        for (habit in habits) {
            val completions = allCompletions.filter { it.habitId == habit.id }
            val habitStreak = completions.size
            val newBest = maxOf(habit.bestStreak, habitStreak)
            if (habit.currentStreak != habitStreak || habit.bestStreak != newBest) {
                habitDao.updateHabit(habit.copy(currentStreak = habitStreak, bestStreak = newBest))
            }
            if (habitStreak > overallStreak) {
                overallStreak = habitStreak
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
        val completionsToday = habitDao.getCompletionsListForDate(today).filter { it.isCompleted }

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
