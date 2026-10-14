package habitflow.adnoryx.eu.data.model

data class AppBackupData(
    val userProfile: UserProfile,
    val habits: List<Habit>,
    val completions: List<HabitCompletion>,
    val freezeQuests: List<FreezeQuest>,
    val exportedAt: Long = System.currentTimeMillis(),
    val appVersion: String = "1.0"
)
