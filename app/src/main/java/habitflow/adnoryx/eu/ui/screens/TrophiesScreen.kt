package habitflow.adnoryx.eu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import habitflow.adnoryx.eu.data.model.FreezeQuest
import habitflow.adnoryx.eu.data.model.Habit
import habitflow.adnoryx.eu.data.model.HabitCategory
import habitflow.adnoryx.eu.data.model.HabitCompletion
import habitflow.adnoryx.eu.data.model.HabitType
import habitflow.adnoryx.eu.data.model.TimeOfDay
import habitflow.adnoryx.eu.data.model.UserProfile
import habitflow.adnoryx.eu.domain.LevelSystem
import habitflow.adnoryx.eu.ui.theme.CharcoalDarkCard
import habitflow.adnoryx.eu.ui.theme.CoralPinkCard
import habitflow.adnoryx.eu.ui.theme.FreezeIce
import habitflow.adnoryx.eu.ui.theme.GamingBackground
import habitflow.adnoryx.eu.ui.theme.GamingBlue
import habitflow.adnoryx.eu.ui.theme.GamingBorder
import habitflow.adnoryx.eu.ui.theme.GamingSurface
import habitflow.adnoryx.eu.ui.theme.GamingSurfaceVariant
import habitflow.adnoryx.eu.ui.theme.StreakFlame
import habitflow.adnoryx.eu.ui.theme.SuccessGreen
import habitflow.adnoryx.eu.ui.theme.TextMuted
import habitflow.adnoryx.eu.ui.theme.TextPrimary
import habitflow.adnoryx.eu.ui.theme.TextSecondary
import habitflow.adnoryx.eu.ui.theme.WarmButterBorder
import habitflow.adnoryx.eu.ui.theme.WarmButterSubtext
import habitflow.adnoryx.eu.ui.theme.WarmButterTrack
import habitflow.adnoryx.eu.ui.theme.WarmButterYellow
import habitflow.adnoryx.eu.util.LanguageManager

data class AchievementItem(
    val id: String,
    val icon: String,
    val title: String,
    val desc: String,
    val currentProgress: Int,
    val targetProgress: Int,
    val isUnlocked: Boolean
)

@Composable
fun TrophiesScreen(
    userProfile: UserProfile?,
    habits: List<Habit> = emptyList(),
    archivedHabits: List<Habit> = emptyList(),
    allCompletions: List<HabitCompletion> = emptyList(),
    freezeQuests: List<FreezeQuest> = emptyList(),
    onBuyFreezeClick: () -> Unit = {}
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val currentLang = LanguageManager.currentLanguage.value
    val lang = currentLang.code

    var filterMode by remember { mutableIntStateOf(0) } // 0 = All, 1 = Unlocked, 2 = Locked

    val totalXp = userProfile?.totalXp ?: 0
    val levelInfo = LevelSystem.calculateLevelInfo(totalXp)
    val totalFreezes = userProfile?.totalFreezes ?: 0
    val currentStreak = userProfile?.currentStreak ?: 0
    val bestStreak = maxOf(userProfile?.bestStreak ?: 0, currentStreak)

    val allUserHabits = remember(habits, archivedHabits) { habits + archivedHabits }
    val totalHabitsCreated = allUserHabits.size
    val totalCompletedCheckIns = remember(allCompletions) { allCompletions.count { it.isCompleted } }
    val distinctActiveDays = remember(allCompletions) {
        allCompletions.filter { it.isCompleted }.map { it.dateString }.distinct().size
    }
    val distinctCategories = remember(allUserHabits) {
        allUserHabits.map { it.category }.distinct().size
    }
    val hasCounterHabit = remember(allUserHabits) {
        allUserHabits.any { it.habitType == HabitType.COUNTER }
    }
    val hasTimerHabit = remember(allUserHabits) {
        allUserHabits.any { it.habitType == HabitType.TIMER }
    }
    val hasReminderSet = remember(allUserHabits) {
        allUserHabits.any { !it.reminderTime.isNullOrBlank() }
    }
    val hasFullDayFlow = remember(habits) {
        val times = habits.map { it.timeOfDay }.toSet()
        times.contains(TimeOfDay.MORNING) && times.contains(TimeOfDay.AFTERNOON) && times.contains(TimeOfDay.EVENING)
    }
    val claimedQuestsCount = remember(freezeQuests) {
        freezeQuests.count { it.isClaimed }
    }

    fun tr(sk: String, cs: String, en: String): String = when (lang) {
        "sk" -> sk
        "cs" -> cs
        else -> en
    }

    val achievements = listOf(
        // Original 12 milestones
        AchievementItem(
            id = "ach_1",
            icon = "🚀",
            title = context.getString(habitflow.adnoryx.eu.R.string.ach_1_title),
            desc = context.getString(habitflow.adnoryx.eu.R.string.ach_1_desc),
            currentProgress = totalXp.coerceAtMost(1),
            targetProgress = 1,
            isUnlocked = totalXp > 0
        ),
        AchievementItem(
            id = "ach_2",
            icon = "⚡",
            title = context.getString(habitflow.adnoryx.eu.R.string.ach_2_title),
            desc = context.getString(habitflow.adnoryx.eu.R.string.ach_2_desc),
            currentProgress = totalXp.coerceAtMost(50),
            targetProgress = 50,
            isUnlocked = totalXp >= 50
        ),
        AchievementItem(
            id = "ach_3",
            icon = "🌟",
            title = context.getString(habitflow.adnoryx.eu.R.string.ach_3_title),
            desc = context.getString(habitflow.adnoryx.eu.R.string.ach_3_desc),
            currentProgress = levelInfo.level.coerceAtMost(2),
            targetProgress = 2,
            isUnlocked = levelInfo.level >= 2
        ),
        AchievementItem(
            id = "ach_4",
            icon = "🔥",
            title = context.getString(habitflow.adnoryx.eu.R.string.ach_4_title),
            desc = context.getString(habitflow.adnoryx.eu.R.string.ach_4_desc),
            currentProgress = bestStreak.coerceAtMost(2),
            targetProgress = 2,
            isUnlocked = bestStreak >= 2
        ),
        AchievementItem(
            id = "ach_5",
            icon = "🔥",
            title = context.getString(habitflow.adnoryx.eu.R.string.ach_5_title),
            desc = context.getString(habitflow.adnoryx.eu.R.string.ach_5_desc),
            currentProgress = bestStreak.coerceAtMost(3),
            targetProgress = 3,
            isUnlocked = bestStreak >= 3
        ),
        AchievementItem(
            id = "ach_6",
            icon = "❄️",
            title = context.getString(habitflow.adnoryx.eu.R.string.ach_6_title),
            desc = context.getString(habitflow.adnoryx.eu.R.string.ach_6_desc),
            currentProgress = totalFreezes.coerceAtMost(1),
            targetProgress = 1,
            isUnlocked = totalFreezes >= 1
        ),
        AchievementItem(
            id = "ach_7",
            icon = "🛡️",
            title = context.getString(habitflow.adnoryx.eu.R.string.ach_7_title),
            desc = context.getString(habitflow.adnoryx.eu.R.string.ach_7_desc),
            currentProgress = totalFreezes.coerceAtMost(3),
            targetProgress = 3,
            isUnlocked = totalFreezes >= 3
        ),
        AchievementItem(
            id = "ach_8",
            icon = "🎖️",
            title = context.getString(habitflow.adnoryx.eu.R.string.ach_8_title),
            desc = context.getString(habitflow.adnoryx.eu.R.string.ach_8_desc),
            currentProgress = levelInfo.level.coerceAtMost(5),
            targetProgress = 5,
            isUnlocked = levelInfo.level >= 5
        ),
        AchievementItem(
            id = "ach_9",
            icon = "🥈",
            title = context.getString(habitflow.adnoryx.eu.R.string.ach_9_title),
            desc = context.getString(habitflow.adnoryx.eu.R.string.ach_9_desc),
            currentProgress = levelInfo.level.coerceAtMost(10),
            targetProgress = 10,
            isUnlocked = levelInfo.level >= 10
        ),
        AchievementItem(
            id = "ach_10",
            icon = "🏆",
            title = context.getString(habitflow.adnoryx.eu.R.string.ach_10_title),
            desc = context.getString(habitflow.adnoryx.eu.R.string.ach_10_desc),
            currentProgress = levelInfo.level.coerceAtMost(25),
            targetProgress = 25,
            isUnlocked = levelInfo.level >= 25
        ),
        AchievementItem(
            id = "ach_11",
            icon = "👑",
            title = context.getString(habitflow.adnoryx.eu.R.string.ach_11_title),
            desc = context.getString(habitflow.adnoryx.eu.R.string.ach_11_desc),
            currentProgress = levelInfo.level.coerceAtMost(50),
            targetProgress = 50,
            isUnlocked = levelInfo.level >= 50
        ),
        AchievementItem(
            id = "ach_12",
            icon = "💥",
            title = context.getString(habitflow.adnoryx.eu.R.string.ach_12_title),
            desc = context.getString(habitflow.adnoryx.eu.R.string.ach_12_desc),
            currentProgress = levelInfo.level.coerceAtMost(100),
            targetProgress = 100,
            isUnlocked = levelInfo.level >= 100
        ),

        // New Streak Achievements
        AchievementItem(
            id = "streak_7",
            icon = "🗓️",
            title = tr("Týždenný bojovník", "Týdenní bojovník", "Full Week Warrior"),
            desc = tr("Dosiahni 7-dňový streak v rade.", "Dosáhni 7denního streaku v řadě.", "Reach a 7-day continuous streak."),
            currentProgress = bestStreak.coerceAtMost(7),
            targetProgress = 7,
            isUnlocked = bestStreak >= 7
        ),
        AchievementItem(
            id = "streak_14",
            icon = "🔥",
            title = tr("Dvojtýždňové tempo", "Dvoutýdenní tempo", "Two-Week Momentum"),
            desc = tr("Udrž svoj streak 14 dní bez prerušenia.", "Udrž svůj streak 14 dní bez přerušení.", "Maintain your streak for 14 days straight."),
            currentProgress = bestStreak.coerceAtMost(14),
            targetProgress = 14,
            isUnlocked = bestStreak >= 14
        ),
        AchievementItem(
            id = "streak_21",
            icon = "🧠",
            title = tr("Zvyk vybudovaný (21 dní)", "Zvyk vybudován (21 dní)", "Habit Formed (21 Days)"),
            desc = tr("21 dní v rade — z rutiny sa stáva automatický návyk.", "21 dní v řadě — z rutiny se stává automatický návyk.", "21 days in a row — your routine is now second nature."),
            currentProgress = bestStreak.coerceAtMost(21),
            targetProgress = 21,
            isUnlocked = bestStreak >= 21
        ),
        AchievementItem(
            id = "streak_30",
            icon = "🌕",
            title = tr("Mesačný majster", "Měsíční mistr", "Monthly Master"),
            desc = tr("Dosiahni 30-dňový streak.", "Dosáhni 30denního streaku.", "Achieve a 30-day streak."),
            currentProgress = bestStreak.coerceAtMost(30),
            targetProgress = 30,
            isUnlocked = bestStreak >= 30
        ),
        AchievementItem(
            id = "streak_60",
            icon = "💎",
            title = tr("Nezastaviteľný (60 dní)", "Nezastavitelný (60 dní)", "Unstoppable (60 Days)"),
            desc = tr("Udrž 60-dňový streak.", "Udrž 60denní streak.", "Reach a 60-day streak."),
            currentProgress = bestStreak.coerceAtMost(60),
            targetProgress = 60,
            isUnlocked = bestStreak >= 60
        ),
        AchievementItem(
            id = "streak_100",
            icon = "🏛️",
            title = tr("Stovkár (100 dní)", "Stovkař (100 dní)", "Centurion (100 Days)"),
            desc = tr("Prekonaj hranicu 100 dní v streaku.", "Překonej hranici 100 dní ve streaku.", "Pass the 100-day streak milestone."),
            currentProgress = bestStreak.coerceAtMost(100),
            targetProgress = 100,
            isUnlocked = bestStreak >= 100
        ),
        AchievementItem(
            id = "streak_365",
            icon = "🌞",
            title = tr("Legenda roka (365 dní)", "Legenda roku (365 dní)", "Year-Round Legend"),
            desc = tr("Celý rok konzistentného plnenia návykov!", "Celý rok konzistentního plnění návyků!", "A full 365-day year of habit consistency!"),
            currentProgress = bestStreak.coerceAtMost(365),
            targetProgress = 365,
            isUnlocked = bestStreak >= 365
        ),

        // Check-in Volume Achievements
        AchievementItem(
            id = "checkins_10",
            icon = "✅",
            title = tr("Prvá desiatka", "První desítka", "Perfect Ten"),
            desc = tr("Splň celkovo 10 návykov.", "Splň celkem 10 návyků.", "Complete 10 habit check-ins in total."),
            currentProgress = totalCompletedCheckIns.coerceAtMost(10),
            targetProgress = 10,
            isUnlocked = totalCompletedCheckIns >= 10
        ),
        AchievementItem(
            id = "checkins_25",
            icon = "🎯",
            title = tr("Pravidelný rytmus", "Pravidelný rytmus", "Steady Rhythm"),
            desc = tr("Splň celkovo 25 návykov.", "Splň celkem 25 návyků.", "Complete 25 habit check-ins in total."),
            currentProgress = totalCompletedCheckIns.coerceAtMost(25),
            targetProgress = 25,
            isUnlocked = totalCompletedCheckIns >= 25
        ),
        AchievementItem(
            id = "checkins_50",
            icon = "🏅",
            title = tr("Polstovka splnená", "Půlstovka splněna", "Fifty Check-Ins"),
            desc = tr("Splň celkovo 50 návykov.", "Splň celkem 50 návyků.", "Complete 50 habit check-ins in total."),
            currentProgress = totalCompletedCheckIns.coerceAtMost(50),
            targetProgress = 50,
            isUnlocked = totalCompletedCheckIns >= 50
        ),
        AchievementItem(
            id = "checkins_100",
            icon = "💯",
            title = tr("Klub 100 splnení", "Klub 100 splnění", "100 Check-In Club"),
            desc = tr("Splň celkovo 100 návykov.", "Splň celkem 100 návyků.", "Complete 100 habit check-ins in total."),
            currentProgress = totalCompletedCheckIns.coerceAtMost(100),
            targetProgress = 100,
            isUnlocked = totalCompletedCheckIns >= 100
        ),
        AchievementItem(
            id = "checkins_250",
            icon = "🌟",
            title = tr("Majster disciplíny (250)", "Mistr disciplíny (250)", "Discipline Master (250)"),
            desc = tr("Splň celkovo 250 návykov.", "Splň celkem 250 návyků.", "Complete 250 habit check-ins in total."),
            currentProgress = totalCompletedCheckIns.coerceAtMost(250),
            targetProgress = 250,
            isUnlocked = totalCompletedCheckIns >= 250
        ),
        AchievementItem(
            id = "checkins_500",
            icon = "🔱",
            title = tr("Titán návykov (500)", "Titán návyků (500)", "Habit Titan (500)"),
            desc = tr("Splň neuveriteľných 500 návykov.", "Splň neuvěřitelných 500 návyků.", "Complete an incredible 500 habit check-ins."),
            currentProgress = totalCompletedCheckIns.coerceAtMost(500),
            targetProgress = 500,
            isUnlocked = totalCompletedCheckIns >= 500
        ),

        // Routine Builder & Feature Achievements
        AchievementItem(
            id = "routines_3",
            icon = "🧱",
            title = tr("Architekt rutín", "Architekt rutin", "Routine Architect"),
            desc = tr("Vytvor si aspoň 3 vlastné rutiny.", "Vytvoř si alespoň 3 vlastní rutiny.", "Create at least 3 custom routines."),
            currentProgress = totalHabitsCreated.coerceAtMost(3),
            targetProgress = 3,
            isUnlocked = totalHabitsCreated >= 3
        ),
        AchievementItem(
            id = "routines_7",
            icon = "📋",
            title = tr("Plánovač dňa", "Plánovač dne", "Daily Strategist"),
            desc = tr("Vytvor si aspoň 7 rutín.", "Vytvoř si alespoň 7 rutin.", "Build a collection of 7 routines."),
            currentProgress = totalHabitsCreated.coerceAtMost(7),
            targetProgress = 7,
            isUnlocked = totalHabitsCreated >= 7
        ),
        AchievementItem(
            id = "counter_habit",
            icon = "🔢",
            title = tr("Každý krok sa počíta", "Každý krok se počítá", "Every Rep Counts"),
            desc = tr("Vytvor si počítadlový návyk (Counter).", "Vytvoř si počítadlový návyk (Counter).", "Create a measurable Counter habit."),
            currentProgress = if (hasCounterHabit) 1 else 0,
            targetProgress = 1,
            isUnlocked = hasCounterHabit
        ),
        AchievementItem(
            id = "timer_habit",
            icon = "⏱️",
            title = tr("Hlboké sústredenie", "Hluboké soustředění", "Deep Focus Timer"),
            desc = tr("Vytvor si časovaný návyk s minútami (Timer).", "Vytvoř si časovaný návyk s minutami (Timer).", "Create a timed habit using the Timer type."),
            currentProgress = if (hasTimerHabit) 1 else 0,
            targetProgress = 1,
            isUnlocked = hasTimerHabit
        ),
        AchievementItem(
            id = "reminder_set",
            icon = "⏰",
            title = tr("Presne načas", "Přesně včas", "Right on Time"),
            desc = tr("Nastav si dennú pripomienku pri ľubovoľnej rutine.", "Nastav si denní připomínku u libovolné rutiny.", "Set a daily reminder on any routine."),
            currentProgress = if (hasReminderSet) 1 else 0,
            targetProgress = 1,
            isUnlocked = hasReminderSet
        ),
        AchievementItem(
            id = "categories_3",
            icon = "🎨",
            title = tr("Všestranný rozvoj", "Všestranný rozvoj", "Well-Rounded"),
            desc = tr("Maj rutiny aspoň v 3 rôznych kategóriách.", "Měj rutiny alespoň ve 3 různých kategoriích.", "Have routines across at least 3 different categories."),
            currentProgress = distinctCategories.coerceAtMost(3),
            targetProgress = 3,
            isUnlocked = distinctCategories >= 3
        ),
        AchievementItem(
            id = "categories_all",
            icon = "🌍",
            title = tr("Renesančná duša", "Renesanční duše", "Renaissance Soul"),
            desc = tr("Pokry všetkých 6 kategórií návykov.", "Pokryj všech 6 kategorií návyků.", "Have routines in all 6 habit categories."),
            currentProgress = distinctCategories.coerceAtMost(HabitCategory.entries.size),
            targetProgress = HabitCategory.entries.size,
            isUnlocked = distinctCategories >= HabitCategory.entries.size
        ),
        AchievementItem(
            id = "full_day_flow",
            icon = "🌅",
            title = tr("Celodenný Flow", "Celodenní Flow", "Full-Day Flow"),
            desc = tr("Maj aktívnu rannú, popoludňajšiu aj večernú rutinu.", "Měj aktivní ranní, odpolední i večerní rutinu.", "Have active Morning, Afternoon, and Evening routines."),
            currentProgress = if (hasFullDayFlow) 1 else 0,
            targetProgress = 1,
            isUnlocked = hasFullDayFlow
        ),
        AchievementItem(
            id = "quest_hunter",
            icon = "🗺️",
            title = tr("Lovec Questov", "Lovec Questů", "Quest Hunter"),
            desc = tr("Vyzdvihni si odmenu z aspoň jedného Freeze Questu.", "Vyzvedni si odměnu z alespoň jednoho Freeze Questu.", "Claim your reward from at least 1 Freeze Quest."),
            currentProgress = claimedQuestsCount.coerceAtMost(1),
            targetProgress = 1,
            isUnlocked = claimedQuestsCount >= 1
        ),
        AchievementItem(
            id = "quest_master",
            icon = "🧭",
            title = tr("Majster Questov", "Mistr Questů", "Quest Master"),
            desc = tr("Dokonči a vyzdvihni si všetky 3 Freeze Questy.", "Dokonči a vyzvedni si všechny 3 Freeze Questy.", "Complete and claim all 3 Freeze Quests."),
            currentProgress = claimedQuestsCount.coerceAtMost(3),
            targetProgress = 3,
            isUnlocked = claimedQuestsCount >= 3
        ),
        AchievementItem(
            id = "freezes_5",
            icon = "🏔️",
            title = tr("Ľadová pevnosť", "Ledová pevnost", "Ice Fortress"),
            desc = tr("Maj v zásobe aspoň 5 Day Freezov naraz.", "Měj v zásobě alespoň 5 Day Freezů najednou.", "Hold a stockpile of 5 Day Freezes at once."),
            currentProgress = totalFreezes.coerceAtMost(5),
            targetProgress = 5,
            isUnlocked = totalFreezes >= 5
        ),
        AchievementItem(
            id = "active_days_15",
            icon = "📆",
            title = tr("15 aktívnych dní", "15 aktivních dnů", "15 Active Days"),
            desc = tr("Zaznamenaj splnený návyk v 15 rôznych dňoch.", "Zaznamenej splněný návyk v 15 různých dnech.", "Log completed habits on 15 different days."),
            currentProgress = distinctActiveDays.coerceAtMost(15),
            targetProgress = 15,
            isUnlocked = distinctActiveDays >= 15
        ),
        AchievementItem(
            id = "xp_500",
            icon = "✨",
            title = tr("Zberateľ XP (500 XP)", "Sběratel XP (500 XP)", "XP Collector (500 XP)"),
            desc = tr("Nazbieraj celkovo 500 XP.", "Nasbírej celkem 500 XP.", "Accumulate 500 total XP."),
            currentProgress = totalXp.coerceAtMost(500),
            targetProgress = 500,
            isUnlocked = totalXp >= 500
        ),
        AchievementItem(
            id = "xp_1500",
            icon = "⚡",
            title = tr("Generátor energie (1 500 XP)", "Generátor energie (1 500 XP)", "Powerhouse (1,500 XP)"),
            desc = tr("Nazbieraj celkovo 1 500 XP.", "Nasbírej celkem 1 500 XP.", "Accumulate 1,500 total XP."),
            currentProgress = totalXp.coerceAtMost(1500),
            targetProgress = 1500,
            isUnlocked = totalXp >= 1500
        ),
        AchievementItem(
            id = "xp_5000",
            icon = "🌌",
            title = tr("Majster XP (5 000 XP)", "Mistr XP (5 000 XP)", "XP Grandmaster (5,000 XP)"),
            desc = tr("Nazbieraj celkovo 5 000 XP.", "Nasbírej celkem 5 000 XP.", "Accumulate 5,000 total XP."),
            currentProgress = totalXp.coerceAtMost(5000),
            targetProgress = 5000,
            isUnlocked = totalXp >= 5000
        )
    )

    val unlockedCount = achievements.count { it.isUnlocked }
    val lockedCount = achievements.size - unlockedCount

    val filteredAchievements = remember(achievements, filterMode) {
        when (filterMode) {
            1 -> achievements.filter { it.isUnlocked }
            2 -> achievements.filter { !it.isUnlocked }
            else -> achievements
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(GamingBackground),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 14.dp,
            bottom = 24.dp
        )
    ) {
        item {
            Text(
                text = context.getString(habitflow.adnoryx.eu.R.string.nav_trophies),
                color = TextPrimary,
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "$unlockedCount / ${achievements.size} " + tr(
                    "odomknutých míľnikov",
                    "odemčených milníků",
                    "Unlocked Milestones"
                ),
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Level Progress Card in Warm Butter Yellow
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(26.dp))
                    .background(WarmButterYellow)
                    .border(1.dp, WarmButterBorder, RoundedCornerShape(26.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .background(GamingSurface)
                                    .border(1.dp, GamingBorder, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.MilitaryTech,
                                    contentDescription = null,
                                    tint = GamingBlue,
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Level ${levelInfo.level}",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 20.sp
                                )
                                Text(
                                    text = context.getString(
                                        habitflow.adnoryx.eu.R.string.xp_fmt,
                                        levelInfo.currentLevelXp,
                                        levelInfo.requiredXpForNextLevel
                                    ),
                                    color = WarmButterSubtext,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(CharcoalDarkCard)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "Rank #${levelInfo.level}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LinearProgressIndicator(
                        progress = { levelInfo.progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(9.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = GamingBlue,
                        trackColor = WarmButterTrack,
                        gapSize = 0.dp,
                        drawStopIndicator = {}
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 3 Stat Bento Mini-Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(22.dp))
                        .background(GamingSurface)
                        .border(1.dp, GamingBorder, RoundedCornerShape(22.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = GamingBlue)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Total XP", color = TextSecondary, fontSize = 11.sp)
                        Text("$totalXp XP", color = TextPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(22.dp))
                        .background(GamingSurface)
                        .border(1.dp, GamingBorder, RoundedCornerShape(22.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Icon(Icons.Default.Whatshot, contentDescription = null, tint = StreakFlame)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Best Streak", color = TextSecondary, fontSize = 11.sp)
                        Text("${bestStreak}d", color = TextPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(22.dp))
                        .background(GamingSurface)
                        .border(1.dp, GamingBorder, RoundedCornerShape(22.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Icon(Icons.Default.AcUnit, contentDescription = null, tint = FreezeIce)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Freezes", color = TextSecondary, fontSize = 11.sp)
                        Text("$totalFreezes", color = TextPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Coral Pink Bento Card for XP Shop
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(CoralPinkCard)
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.22f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "XP Shop",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = tr(
                                    "XP Obchod: +1 Day Freeze",
                                    "XP Obchod: +1 Day Freeze",
                                    "XP Shop: +1 Day Freeze"
                                ),
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = tr(
                                    "Vymeň 150 XP za dodatočný štít streaku",
                                    "Vyměň 150 XP za dodatečný štít streaku",
                                    "Spend 150 XP for an extra streak shield"
                                ),
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onBuyFreezeClick()
                        },
                        enabled = totalXp >= 150,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = CharcoalDarkCard,
                            contentColor = Color.White,
                            disabledContainerColor = Color.Black.copy(alpha = 0.25f),
                            disabledContentColor = Color.White.copy(alpha = 0.6f)
                        ),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "150 XP",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(22.dp))
        }

        // Header + Filter Chips
        item {
            Text(
                text = context.getString(habitflow.adnoryx.eu.R.string.achievements),
                color = TextPrimary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filterLabels = listOf(
                    0 to tr("Všetky (${achievements.size})", "Všechny (${achievements.size})", "All (${achievements.size})"),
                    1 to tr("Odomknuté ($unlockedCount)", "Odemčené ($unlockedCount)", "Unlocked ($unlockedCount)"),
                    2 to tr("Zamknuté ($lockedCount)", "Zamčené ($lockedCount)", "Locked ($lockedCount)")
                )

                filterLabels.forEach { (mode, label) ->
                    val isSelected = (filterMode == mode)
                    FilterChip(
                        selected = isSelected,
                        onClick = { filterMode = mode },
                        shape = RoundedCornerShape(50),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = CharcoalDarkCard,
                            selectedLabelColor = Color.White,
                            containerColor = GamingSurface,
                            labelColor = TextPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = GamingBorder,
                            selectedBorderColor = CharcoalDarkCard
                        ),
                        label = {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }

        items(filteredAchievements, key = { it.id }) { item ->
            val progressFraction = (item.currentProgress.toFloat() / maxOf(1, item.targetProgress).toFloat()).coerceIn(0f, 1f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(if (item.isUnlocked) GamingSurface else GamingSurfaceVariant.copy(alpha = 0.65f))
                    .border(
                        1.dp,
                        if (item.isUnlocked) SuccessGreen.copy(alpha = 0.45f) else GamingBorder,
                        RoundedCornerShape(20.dp)
                    )
                    .padding(14.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (item.isUnlocked) WarmButterYellow else GamingSurfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = item.icon,
                                fontSize = 20.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                color = if (item.isUnlocked) TextPrimary else TextMuted,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = item.desc,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        if (item.isUnlocked) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(SuccessGreen)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "UNLOCKED",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(GamingSurface)
                                    .border(1.dp, GamingBorder, RoundedCornerShape(50))
                                    .padding(horizontal = 9.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "${item.currentProgress}/${item.targetProgress}",
                                    color = TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    if (!item.isUnlocked && item.targetProgress > 1) {
                        Spacer(modifier = Modifier.height(10.dp))
                        LinearProgressIndicator(
                            progress = { progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(5.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = GamingBlue,
                            trackColor = GamingBorder,
                            gapSize = 0.dp,
                            drawStopIndicator = {}
                        )
                    }
                }
            }
        }
    }
}
