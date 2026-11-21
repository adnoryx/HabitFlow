package habitflow.adnoryx.eu.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextOverflow
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

    fun tr9(
        en: String,
        sk: String,
        cs: String,
        de: String,
        es: String,
        fr: String,
        pl: String,
        uk: String,
        it: String
    ): String = when (lang) {
        "sk" -> sk
        "cs" -> cs
        "de" -> de
        "es" -> es
        "fr" -> fr
        "pl" -> pl
        "uk" -> uk
        "it" -> it
        else -> en
    }

    val achievements = listOf(
        // Original 12 milestones (from localized strings.xml)
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

        // Streak Achievements
        AchievementItem(
            id = "streak_7",
            icon = "🗓️",
            title = tr9(
                "Full Week Warrior", "Týždenný bojovník", "Týdenní bojovník",
                "Wochen-Krieger", "Guerrero semanal", "Guerrier de la semaine",
                "Tygodniowy wojownik", "Тижневий воїн", "Guerriero della settimana"
            ),
            desc = tr9(
                "Reach a 7-day continuous streak.", "Dosiahni 7-dňový streak v rade.", "Dosáhni 7denního streaku v řadě.",
                "Erreiche einen 7-Tage-Streak in Folge.", "Consigue una racha continua de 7 días.", "Atteignez une série continue de 7 jours.",
                "Osiągnij 7-dniową serię z rzędu.", "Досягніть безперервної серії в 7 днів.", "Raggiungi una serie continua di 7 giorni."
            ),
            currentProgress = bestStreak.coerceAtMost(7),
            targetProgress = 7,
            isUnlocked = bestStreak >= 7
        ),
        AchievementItem(
            id = "streak_14",
            icon = "🔥",
            title = tr9(
                "Two-Week Momentum", "Dvojtýždňové tempo", "Dvoutýdenní tempo",
                "Zwei-Wochen-Schwung", "Impulso de dos semanas", "Élan de deux semaines",
                "Dwutygodniowe tempo", "Двотижневий темп", "Slancio di due settimane"
            ),
            desc = tr9(
                "Maintain your streak for 14 days straight.", "Udrž svoj streak 14 dní bez prerušenia.", "Udrž svůj streak 14 dní bez přerušení.",
                "Halte deinen Streak 14 Tage am Stück.", "Mantén tu racha durante 14 días seguidos.", "Maintenez votre série pendant 14 jours d'affilée.",
                "Utrzymaj serię przez 14 dni bez przerwy.", "Утримуйте серію 14 днів поспіль.", "Mantieni la tua serie per 14 giorni consecutivi."
            ),
            currentProgress = bestStreak.coerceAtMost(14),
            targetProgress = 14,
            isUnlocked = bestStreak >= 14
        ),
        AchievementItem(
            id = "streak_21",
            icon = "🧠",
            title = tr9(
                "Habit Formed (21 Days)", "Zvyk vybudovaný (21 dní)", "Zvyk vybudován (21 dní)",
                "Gewohnheit geformt (21 Tage)", "Hábito formado (21 días)", "Habitude ancrée (21 jours)",
                "Nawyk wyrobiony (21 dni)", "Звичку сформовано (21 день)", "Abitudine formata (21 gg)"
            ),
            desc = tr9(
                "21 days in a row — your routine is now second nature.", "21 dní v rade — z rutiny sa stáva automatický návyk.", "21 dní v řadě — z rutiny se stává automatický návyk.",
                "21 Tage in Folge — deine Routine ist jetzt automatisch.", "21 días seguidos — tu rutina ya es automática.", "21 jours de suite — votre routine est devenue naturelle.",
                "21 dni z rzędu — rutyna staje się drugą naturą.", "21 день поспіль — рутина стала автоматичною звичкою.", "21 giorni di fila — la tua routine è ormai naturale."
            ),
            currentProgress = bestStreak.coerceAtMost(21),
            targetProgress = 21,
            isUnlocked = bestStreak >= 21
        ),
        AchievementItem(
            id = "streak_30",
            icon = "🌕",
            title = tr9(
                "Monthly Master", "Mesačný majster", "Měsíční mistr",
                "Monats-Meister", "Maestro mensual", "Maître du mois",
                "Mistrz miesiąca", "Майстер місяця", "Maestro mensile"
            ),
            desc = tr9(
                "Achieve a 30-day streak.", "Dosiahni 30-dňový streak.", "Dosáhni 30denního streaku.",
                "Erreiche einen 30-Tage-Streak.", "Consigue una racha de 30 días.", "Atteignez une série de 30 jours.",
                "Osiągnij 30-dniową serię.", "Досягніть 30-денної серії.", "Raggiungi una serie di 30 giorni."
            ),
            currentProgress = bestStreak.coerceAtMost(30),
            targetProgress = 30,
            isUnlocked = bestStreak >= 30
        ),
        AchievementItem(
            id = "streak_60",
            icon = "💎",
            title = tr9(
                "Unstoppable (60 Days)", "Nezastaviteľný (60 dní)", "Nezastavitelný (60 dní)",
                "Unaufhaltsam (60 Tage)", "Imparable (60 días)", "Inarrêtable (60 jours)",
                "Niepowstrzymany (60 dni)", "Нестримний (60 днів)", "Inarrestabile (60 giorni)"
            ),
            desc = tr9(
                "Reach a 60-day streak.", "Udrž 60-dňový streak.", "Udrž 60denní streak.",
                "Erreiche einen 60-Tage-Streak.", "Alcanza una racha de 60 días.", "Atteignez une série de 60 jours.",
                "Utrzymaj 60-dniową serię.", "Досягніть 60-денної серії.", "Raggiungi una serie di 60 giorni."
            ),
            currentProgress = bestStreak.coerceAtMost(60),
            targetProgress = 60,
            isUnlocked = bestStreak >= 60
        ),
        AchievementItem(
            id = "streak_100",
            icon = "🏛️",
            title = tr9(
                "Centurion (100 Days)", "Stovkár (100 dní)", "Stovkař (100 dní)",
                "Zenturio (100 Tage)", "Centurión (100 días)", "Centurion (100 jours)",
                "Centurion (100 dni)", "Центуріон (100 днів)", "Centurione (100 giorni)"
            ),
            desc = tr9(
                "Pass the 100-day streak milestone.", "Prekonaj hranicu 100 dní v streaku.", "Překonej hranici 100 dní ve streaku.",
                "Überschreite den 100-Tage-Streak-Meilenstein.", "Supera el hito de 100 días de racha.", "Franchissez le cap des 100 jours de série.",
                "Przekrocz granicę 100 dni w serii.", "Подолайте рубіж у 100 днів серії.", "Supera il traguardo dei 100 giorni di serie."
            ),
            currentProgress = bestStreak.coerceAtMost(100),
            targetProgress = 100,
            isUnlocked = bestStreak >= 100
        ),
        AchievementItem(
            id = "streak_365",
            icon = "🌞",
            title = tr9(
                "Year-Round Legend", "Legenda roka (365 dní)", "Legenda roku (365 dní)",
                "Jahres-Legende (365 Tage)", "Leyenda del año (365 días)", "Légende de l'année (365 j)",
                "Legenda roku (365 dni)", "Легенда року (365 днів)", "Leggenda dell'anno (365 gg)"
            ),
            desc = tr9(
                "A full 365-day year of habit consistency!", "Celý rok konzistentného plnenia návykov!", "Celý rok konzistentního plnění návyků!",
                "Ein ganzes Jahr (365 Tage) voller Konstanz!", "¡Un año completo de 365 días de constancia!", "Une année complète de 365 jours de régularité !",
                "Cały rok (365 dni) regularnego wykonywania nawyków!", "Цілий рік (365 днів) стабільного виконання звичок!", "Un anno intero di 365 giorni di costanza!"
            ),
            currentProgress = bestStreak.coerceAtMost(365),
            targetProgress = 365,
            isUnlocked = bestStreak >= 365
        ),

        // Check-in Volume Achievements
        AchievementItem(
            id = "checkins_10",
            icon = "✅",
            title = tr9(
                "Perfect Ten", "Prvá desiatka", "První desítka",
                "Perfekte Zehn", "Diez perfecto", "Dix parfait",
                "Pierwsza dziesiątka", "Перша десятка", "Dieci perfetto"
            ),
            desc = tr9(
                "Complete 10 habit check-ins in total.", "Splň celkovo 10 návykov.", "Splň celkem 10 návyků.",
                "Schließe insgesamt 10 Routinen ab.", "Completa 10 registros de hábitos en total.", "Complétez 10 habitudes au total.",
                "Ukończ łącznie 10 nawyków.", "Виконайте загалом 10 звичок.", "Completa 10 abitudini in totale."
            ),
            currentProgress = totalCompletedCheckIns.coerceAtMost(10),
            targetProgress = 10,
            isUnlocked = totalCompletedCheckIns >= 10
        ),
        AchievementItem(
            id = "checkins_25",
            icon = "🎯",
            title = tr9(
                "Steady Rhythm", "Pravidelný rytmus", "Pravidelný rytmus",
                "Stabiler Rhythmus", "Ritmo constante", "Rythme régulier",
                "Stały rytm", "Стабільний ритм", "Ritmo costante"
            ),
            desc = tr9(
                "Complete 25 habit check-ins in total.", "Splň celkovo 25 návykov.", "Splň celkem 25 návyků.",
                "Schließe insgesamt 25 Routinen ab.", "Completa 25 registros de hábitos en total.", "Complétez 25 habitudes au total.",
                "Ukończ łącznie 25 nawyków.", "Виконайте загалом 25 звичок.", "Completa 25 abitudini in totale."
            ),
            currentProgress = totalCompletedCheckIns.coerceAtMost(25),
            targetProgress = 25,
            isUnlocked = totalCompletedCheckIns >= 25
        ),
        AchievementItem(
            id = "checkins_50",
            icon = "🏅",
            title = tr9(
                "Fifty Check-Ins", "Polstovka splnená", "Půlstovka splněna",
                "Fünfzig Abschlüsse", "Cincuenta completados", "Cinquante validations",
                "Pięćdziesiąt ukończeń", "П'ятдесят виконань", "Cinquanta completamenti"
            ),
            desc = tr9(
                "Complete 50 habit check-ins in total.", "Splň celkovo 50 návykov.", "Splň celkem 50 návyků.",
                "Schließe insgesamt 50 Routinen ab.", "Completa 50 registros de hábitos en total.", "Complétez 50 habitudes au total.",
                "Ukończ łącznie 50 nawyków.", "Виконайте загалом 50 звичок.", "Completa 50 abitudini in totale."
            ),
            currentProgress = totalCompletedCheckIns.coerceAtMost(50),
            targetProgress = 50,
            isUnlocked = totalCompletedCheckIns >= 50
        ),
        AchievementItem(
            id = "checkins_100",
            icon = "💯",
            title = tr9(
                "100 Check-In Club", "Klub 100 splnení", "Klub 100 splnění",
                "Klub der 100 Abschlüsse", "Club de los 100", "Club des 100 validations",
                "Klub 100 ukończeń", "Клуб 100 виконань", "Club dei 100 completamenti"
            ),
            desc = tr9(
                "Complete 100 habit check-ins in total.", "Splň celkovo 100 návykov.", "Splň celkem 100 návyků.",
                "Schließe insgesamt 100 Routinen ab.", "Completa 100 registros de hábitos en total.", "Complétez 100 habitudes au total.",
                "Ukończ łącznie 100 nawyków.", "Виконайте загалом 100 звичок.", "Completa 100 abitudini in totale."
            ),
            currentProgress = totalCompletedCheckIns.coerceAtMost(100),
            targetProgress = 100,
            isUnlocked = totalCompletedCheckIns >= 100
        ),
        AchievementItem(
            id = "checkins_250",
            icon = "🌟",
            title = tr9(
                "Discipline Master (250)", "Majster disciplíny (250)", "Mistr disciplíny (250)",
                "Disziplin-Meister (250)", "Maestro de disciplina (250)", "Maître de discipline (250)",
                "Mistrz dyscypliny (250)", "Майстер дисципліни (250)", "Maestro di disciplina (250)"
            ),
            desc = tr9(
                "Complete 250 habit check-ins in total.", "Splň celkovo 250 návykov.", "Splň celkem 250 návyků.",
                "Schließe insgesamt 250 Routinen ab.", "Completa 250 registros de hábitos en total.", "Complétez 250 habitudes au total.",
                "Ukończ łącznie 250 nawyków.", "Виконайте загалом 250 звичок.", "Completa 250 abitudini in totale."
            ),
            currentProgress = totalCompletedCheckIns.coerceAtMost(250),
            targetProgress = 250,
            isUnlocked = totalCompletedCheckIns >= 250
        ),
        AchievementItem(
            id = "checkins_500",
            icon = "🔱",
            title = tr9(
                "Habit Titan (500)", "Titán návykov (500)", "Titán návyků (500)",
                "Gewohnheits-Titan (500)", "Titán de hábitos (500)", "Titan des habitudes (500)",
                "Tytan nawyków (500)", "Титан звичок (500)", "Titano delle abitudini (500)"
            ),
            desc = tr9(
                "Complete an incredible 500 habit check-ins.", "Splň neuveriteľných 500 návykov.", "Splň neuvěřitelných 500 návyků.",
                "Schließe unglaubliche 500 Routinen ab.", "Completa la increíble cifra de 500 hábitos.", "Complétez l'incroyable total de 500 habitudes.",
                "Ukończ niesamowite 500 nawyków.", "Виконайте неймовірні 500 звичок.", "Completa ben 500 abitudini in totale."
            ),
            currentProgress = totalCompletedCheckIns.coerceAtMost(500),
            targetProgress = 500,
            isUnlocked = totalCompletedCheckIns >= 500
        ),

        // Routine Builder & Feature Achievements
        AchievementItem(
            id = "routines_3",
            icon = "🧱",
            title = tr9(
                "Routine Architect", "Architekt rutín", "Architekt rutin",
                "Routinen-Architekt", "Arquitecto de rutinas", "Architecte de routines",
                "Architekt rutyn", "Архітектор рутин", "Architetto di routine"
            ),
            desc = tr9(
                "Create at least 3 custom routines.", "Vytvor si aspoň 3 vlastné rutiny.", "Vytvoř si alespoň 3 vlastní rutiny.",
                "Erstelle mindestens 3 eigene Routinen.", "Crea al menos 3 rutinas personalizadas.", "Créez au moins 3 routines personnalisées.",
                "Utwórz co najmniej 3 własne rutyny.", "Створіть щонайменше 3 власні рутини.", "Crea almeno 3 routine personalizzate."
            ),
            currentProgress = totalHabitsCreated.coerceAtMost(3),
            targetProgress = 3,
            isUnlocked = totalHabitsCreated >= 3
        ),
        AchievementItem(
            id = "routines_7",
            icon = "📋",
            title = tr9(
                "Daily Strategist", "Plánovač dňa", "Plánovač dne",
                "Tages-Stratege", "Estratega diario", "Stratège du quotidien",
                "Strateg dnia", "Стратег дня", "Stratega quotidiano"
            ),
            desc = tr9(
                "Build a collection of 7 routines.", "Vytvor si aspoň 7 rutín.", "Vytvoř si alespoň 7 rutin.",
                "Erstelle eine Sammlung von 7 Routinen.", "Crea una colección de 7 rutinas.", "Créez une collection de 7 routines.",
                "Stwórz kolekcję 7 rutyn.", "Створіть колекцію з 7 рутин.", "Crea una collezione di 7 routine."
            ),
            currentProgress = totalHabitsCreated.coerceAtMost(7),
            targetProgress = 7,
            isUnlocked = totalHabitsCreated >= 7
        ),
        AchievementItem(
            id = "counter_habit",
            icon = "🔢",
            title = tr9(
                "Every Rep Counts", "Každý krok sa počíta", "Každý krok se počítá",
                "Jeder Schritt zählt", "Cada paso cuenta", "Chaque étape compte",
                "Każdy krok się liczy", "Кожен крок важливий", "Ogni passo conta"
            ),
            desc = tr9(
                "Create a measurable Counter habit.", "Vytvor si počítadlový návyk (Counter).", "Vytvoř si počítadlový návyk (Counter).",
                "Erstelle eine messbare Zähler-Routine.", "Crea un hábito medible con Contador.", "Créez une habitude mesurable avec Compteur.",
                "Utwórz mierzalny nawyk z Licznikiem.", "Створіть вимірювану звичку з Лічильником.", "Crea un'abitudine misurabile con Contatore."
            ),
            currentProgress = if (hasCounterHabit) 1 else 0,
            targetProgress = 1,
            isUnlocked = hasCounterHabit
        ),
        AchievementItem(
            id = "timer_habit",
            icon = "⏱️",
            title = tr9(
                "Deep Focus Timer", "Hlboké sústredenie", "Hluboké soustředění",
                "Tiefer Fokus-Timer", "Enfoque profundo", "Concentration profonde",
                "Głębokie skupienie", "Глибокий фокус", "Concentrazione profonda"
            ),
            desc = tr9(
                "Create a timed habit using the Timer type.", "Vytvor si časovaný návyk s minútami (Timer).", "Vytvoř si časovaný návyk s minutami (Timer).",
                "Erstelle eine zeitbasierte Timer-Routine.", "Crea un hábito con Temporizador en minutos.", "Créez une habitude chronométrée avec Minuteur.",
                "Utwórz nawyk czasowy z Minutnikiem.", "Створіть звичку з Таймером у хвилинах.", "Crea un'abitudine a tempo con il Timer."
            ),
            currentProgress = if (hasTimerHabit) 1 else 0,
            targetProgress = 1,
            isUnlocked = hasTimerHabit
        ),
        AchievementItem(
            id = "reminder_set",
            icon = "⏰",
            title = tr9(
                "Right on Time", "Presne načas", "Přesně včas",
                "Pünktlich zur Stelle", "Justo a tiempo", "Juste à temps",
                "Punktualnie", "Точно вчасно", "Sempre puntuale"
            ),
            desc = tr9(
                "Set a daily reminder on any routine.", "Nastav si dennú pripomienku pri ľubovoľnej rutine.", "Nastav si denní připomínku u libovolné rutiny.",
                "Richte eine tägliche Erinnerung für eine Routine ein.", "Configura un recordatorio diario en cualquier rutina.", "Programmez un rappel quotidien sur une routine.",
                "Ustaw codzienne przypomnienie dla dowolnej rutyny.", "Налаштуйте щоденне нагадування для будь-якої рутини.", "Imposta un promemoria giornaliero per una routine."
            ),
            currentProgress = if (hasReminderSet) 1 else 0,
            targetProgress = 1,
            isUnlocked = hasReminderSet
        ),
        AchievementItem(
            id = "categories_3",
            icon = "🎨",
            title = tr9(
                "Well-Rounded", "Všestranný rozvoj", "Všestranný rozvoj",
                "Vielseitig aufgestellt", "Desarrollo equilibrado", "Équilibre complet",
                "Wszechstronny rozwój", "Всебічний розвиток", "Sviluppo equilibrato"
            ),
            desc = tr9(
                "Have routines across at least 3 different categories.", "Maj rutiny aspoň v 3 rôznych kategóriách.", "Měj rutiny alespoň ve 3 různých kategoriích.",
                "Habe Routinen in mindestens 3 Kategorien.", "Ten rutinas en al menos 3 categorías distintas.", "Ayez des routines dans au moins 3 catégories.",
                "Miej rutyny w co najmniej 3 różnych kategoriach.", "Майте рутини щонайменше в 3 різних категоріях.", "Abbi routine in almeno 3 categorie diverse."
            ),
            currentProgress = distinctCategories.coerceAtMost(3),
            targetProgress = 3,
            isUnlocked = distinctCategories >= 3
        ),
        AchievementItem(
            id = "categories_all",
            icon = "🌍",
            title = tr9(
                "Renaissance Soul", "Renesančná duša", "Renesanční duše",
                "Renaissance-Geist", "Alma renacentista", "Esprit de la Renaissance",
                "Człowiek renesansu", "Душа Ренесансу", "Spirito rinascimentale"
            ),
            desc = tr9(
                "Have routines in all 6 habit categories.", "Pokry všetkých 6 kategórií návykov.", "Pokryj všech 6 kategorií návyků.",
                "Decke alle 6 Gewohnheits-Kategorien ab.", "Cubre las 6 categorías de hábitos.", "Ayez des routines dans les 6 catégories.",
                "Miej rutyny we wszystkich 6 kategoriach.", "Охопіть усі 6 категорій звичок.", "Abbi routine in tutte e 6 le categorie."
            ),
            currentProgress = distinctCategories.coerceAtMost(HabitCategory.entries.size),
            targetProgress = HabitCategory.entries.size,
            isUnlocked = distinctCategories >= HabitCategory.entries.size
        ),
        AchievementItem(
            id = "full_day_flow",
            icon = "🌅",
            title = tr9(
                "Full-Day Flow", "Celodenný Flow", "Celodenní Flow",
                "Ganztages-Flow", "Flujo de todo el día", "Flow toute la journée",
                "Całodniowy Flow", "Цілоденний Flow", "Flow di tutto il giorno"
            ),
            desc = tr9(
                "Have active Morning, Afternoon, and Evening routines.", "Maj aktívnu rannú, popoludňajšiu aj večernú rutinu.", "Měj aktivní ranní, odpolední i večerní rutinu.",
                "Habe aktive Morgen-, Nachmittags- und Abendroutinen.", "Ten rutinas activas de mañana, tarde y noche.", "Ayez des routines actives matin, après-midi et soir.",
                "Miej aktywne rutyny poranne, popołudniowe i wieczorne.", "Майте активні ранкові, денні та вечірні рутини.", "Abbi routine attive di mattina, pomeriggio e sera."
            ),
            currentProgress = if (hasFullDayFlow) 1 else 0,
            targetProgress = 1,
            isUnlocked = hasFullDayFlow
        ),
        AchievementItem(
            id = "quest_hunter",
            icon = "🗺️",
            title = tr9(
                "Quest Hunter", "Lovec Questov", "Lovec Questů",
                "Quest-Jäger", "Cazador de misiones", "Chasseur de quêtes",
                "Łowca zadań", "Мисливець за квестами", "Cacciatore di missioni"
            ),
            desc = tr9(
                "Claim your reward from at least 1 Freeze Quest.", "Vyzdvihni si odmenu z aspoň jedného Freeze Questu.", "Vyzvedni si odměnu z alespoň jednoho Freeze Questu.",
                "Hole dir die Belohnung von mindestens 1 Freeze-Aufgabe.", "Reclama tu recompensa de al menos 1 Misión.", "Réclamez la récompense d'au moins 1 Quête de Gel.",
                "Odbierz nagrodę z co najmniej 1 Zadania Zamrożenia.", "Заберіть нагороду хоча б з 1 Завдання Заморозки.", "Riscatta la ricompensa di almeno 1 Missione."
            ),
            currentProgress = claimedQuestsCount.coerceAtMost(1),
            targetProgress = 1,
            isUnlocked = claimedQuestsCount >= 1
        ),
        AchievementItem(
            id = "quest_master",
            icon = "🧭",
            title = tr9(
                "Quest Master", "Majster Questov", "Mistr Questů",
                "Quest-Meister", "Maestro de misiones", "Maître des quêtes",
                "Mistrz zadań", "Майстер квестів", "Maestro delle missioni"
            ),
            desc = tr9(
                "Complete and claim all 3 Freeze Quests.", "Dokonči a vyzdvihni si všetky 3 Freeze Questy.", "Dokonči a vyzvedni si všechny 3 Freeze Questy.",
                "Schließe alle 3 Freeze-Aufgaben ab und hole sie ab.", "Completa y reclama las 3 Misiones de Congelador.", "Terminez et réclamez les 3 Quêtes de Gel.",
                "Ukończ i odbierz wszystkie 3 Zadania Zamrożenia.", "Виконайте та заберіть усі 3 Завдання Заморозки.", "Completa e riscatta tutte e 3 le Missioni."
            ),
            currentProgress = claimedQuestsCount.coerceAtMost(3),
            targetProgress = 3,
            isUnlocked = claimedQuestsCount >= 3
        ),
        AchievementItem(
            id = "freezes_5",
            icon = "🏔️",
            title = tr9(
                "Ice Fortress", "Ľadová pevnosť", "Ledová pevnost",
                "Eisfestung", "Fortaleza de hielo", "Forteresse de glace",
                "Lodowa forteca", "Крижана фортеця", "Fortezza di ghiaccio"
            ),
            desc = tr9(
                "Hold a stockpile of 5 Day Freezes at once.", "Maj v zásobe aspoň 5 Day Freezov naraz.", "Měj v zásobě alespoň 5 Day Freezů najednou.",
                "Besitze gleichzeitig einen Vorrat von 5 Tages-Freezes.", "Acumula una reserva de 5 Congeladores a la vez.", "Possédez une réserve de 5 Gels de Jour à la fois.",
                "Zgromadź zapas 5 Zamrożeń Dnia jednocześnie.", "Майте в запасі 5 Заморозок Дня одночасно.", "Accumula una scorta di 5 Congelamenti insieme."
            ),
            currentProgress = totalFreezes.coerceAtMost(5),
            targetProgress = 5,
            isUnlocked = totalFreezes >= 5
        ),
        AchievementItem(
            id = "active_days_15",
            icon = "📆",
            title = tr9(
                "15 Active Days", "15 aktívnych dní", "15 aktivních dnů",
                "15 aktive Tage", "15 días activos", "15 jours actifs",
                "15 aktywnych dni", "15 активних днів", "15 giorni attivi"
            ),
            desc = tr9(
                "Log completed habits on 15 different days.", "Zaznamenaj splnený návyk v 15 rôznych dňoch.", "Zaznamenej splněný návyk v 15 různých dnech.",
                "Schließe an 15 verschiedenen Tagen Gewohnheiten ab.", "Registra hábitos completados en 15 días distintos.", "Validez des habitudes sur 15 jours différents.",
                "Zapisz ukończone nawyki w 15 różnych dniach.", "Відмітьте виконані звички у 15 різних днях.", "Registra abitudini completate in 15 giorni diversi."
            ),
            currentProgress = distinctActiveDays.coerceAtMost(15),
            targetProgress = 15,
            isUnlocked = distinctActiveDays >= 15
        ),
        AchievementItem(
            id = "xp_500",
            icon = "✨",
            title = tr9(
                "XP Collector (500 XP)", "Zberateľ XP (500 XP)", "Sběratel XP (500 XP)",
                "XP-Sammler (500 XP)", "Coleccionista XP (500 XP)", "Collectionneur XP (500 XP)",
                "Kolekcjoner XP (500 XP)", "Колекціонер XP (500 XP)", "Collezionista XP (500 XP)"
            ),
            desc = tr9(
                "Accumulate 500 total XP.", "Nazbieraj celkovo 500 XP.", "Nasbírej celkem 500 XP.",
                "Sammle insgesamt 500 XP.", "Acumula un total de 500 XP.", "Accumulez un total de 500 XP.",
                "Zgromadź łącznie 500 XP.", "Накопичте загалом 500 XP.", "Accumula 500 XP totali."
            ),
            currentProgress = totalXp.coerceAtMost(500),
            targetProgress = 500,
            isUnlocked = totalXp >= 500
        ),
        AchievementItem(
            id = "xp_1500",
            icon = "⚡",
            title = tr9(
                "Powerhouse (1,500 XP)", "Generátor energie (1 500 XP)", "Generátor energie (1 500 XP)",
                "Kraftpaket (1.500 XP)", "Motor de energía (1.500 XP)", "Dynamo (1 500 XP)",
                "Wulkan energii (1 500 XP)", "Генератор енергії (1 500 XP)", "Centrale d'energia (1.500 XP)"
            ),
            desc = tr9(
                "Accumulate 1,500 total XP.", "Nazbieraj celkovo 1 500 XP.", "Nasbírej celkem 1 500 XP.",
                "Sammle insgesamt 1.500 XP.", "Acumula un total de 1.500 XP.", "Accumulez un total de 1 500 XP.",
                "Zgromadź łącznie 1 500 XP.", "Накопичте загалом 1 500 XP.", "Accumula 1.500 XP totali."
            ),
            currentProgress = totalXp.coerceAtMost(1500),
            targetProgress = 1500,
            isUnlocked = totalXp >= 1500
        ),
        AchievementItem(
            id = "xp_5000",
            icon = "🌌",
            title = tr9(
                "XP Grandmaster (5,000 XP)", "Majster XP (5 000 XP)", "Mistr XP (5 000 XP)",
                "XP-Großmeister (5.000 XP)", "Gran Maestro XP (5.000 XP)", "Grand Maître XP (5 000 XP)",
                "Arcymistrz XP (5 000 XP)", "Гросмейстер XP (5 000 XP)", "Gran Maestro XP (5.000 XP)"
            ),
            desc = tr9(
                "Accumulate 5,000 total XP.", "Nazbieraj celkovo 5 000 XP.", "Nasbírej celkem 5 000 XP.",
                "Sammle insgesamt 5.000 XP.", "Acumula un total de 5.000 XP.", "Accumulez un total de 5 000 XP.",
                "Zgromadź łącznie 5 000 XP.", "Накопичте загалом 5 000 XP.", "Accumula 5.000 XP totali."
            ),
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
                text = "$unlockedCount / ${achievements.size} " + tr9(
                    "Unlocked Milestones",
                    "odomknutých míľnikov",
                    "odemčených milníků",
                    "Freigeschaltete Meilensteine",
                    "Hitos desbloqueados",
                    "Jalons débloqués",
                    "odblokowanych osiągnięć",
                    "розблокованих досягнень",
                    "Traguardi sbloccati"
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
                        Text(
                            text = tr9(
                                "Total XP", "Celkovo XP", "Celkem XP",
                                "Gesamt-XP", "XP Total", "XP Total",
                                "Łącznie XP", "Усього XP", "XP Totali"
                            ),
                            color = TextSecondary,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text("$totalXp XP", color = TextPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, maxLines = 1)
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
                        Text(
                            text = tr9(
                                "Best Streak", "Max Streak", "Max Streak",
                                "Max Streak", "Mejor racha", "Record série",
                                "Rekord serii", "Рекорд серії", "Record serie"
                            ),
                            color = TextSecondary,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text("${bestStreak}d", color = TextPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, maxLines = 1)
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
                        Text(
                            text = tr9(
                                "Freezes", "Freezy", "Freezy",
                                "Freezes", "Congeladores", "Gels",
                                "Zamrożenia", "Заморозки", "Congelamenti"
                            ),
                            color = TextSecondary,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text("$totalFreezes", color = TextPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, maxLines = 1)
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
                    .padding(horizontal = 16.dp, vertical = 16.dp)
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
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.22f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "XP Shop",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = tr9(
                                    "+1 Day Freeze",
                                    "+1 Day Freeze",
                                    "+1 Day Freeze",
                                    "+1 Tages-Freeze",
                                    "+1 Congelador",
                                    "+1 Gel de Jour",
                                    "+1 Zamrożenie",
                                    "+1 Заморозка",
                                    "+1 Congelamento"
                                ),
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 15.sp,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = tr9(
                                    "XP Shop • Streak shield",
                                    "XP Obchod • Štít streaku",
                                    "XP Obchod • Štít streaku",
                                    "XP-Shop • Streak-Schild",
                                    "Tienda XP • Escudo racha",
                                    "Boutique XP • Bouclier",
                                    "Sklep XP • Tarcza serii",
                                    "XP Магазин • Щит серії",
                                    "Negozio XP • Scudo serie"
                                ),
                                color = Color.White.copy(alpha = 0.88f),
                                fontSize = 12.sp,
                                maxLines = 1,
                                softWrap = false,
                                overflow = TextOverflow.Ellipsis
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
                        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp),
                        modifier = Modifier.height(36.dp),
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
                            fontSize = 12.sp,
                            maxLines = 1,
                            softWrap = false
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
            Spacer(modifier = Modifier.height(10.dp))

            val filterLabels = listOf(
                0 to tr9(
                    "All (${achievements.size})",
                    "Všetky (${achievements.size})",
                    "Všechny (${achievements.size})",
                    "Alle (${achievements.size})",
                    "Todos (${achievements.size})",
                    "Tous (${achievements.size})",
                    "Wszystkie (${achievements.size})",
                    "Усі (${achievements.size})",
                    "Tutti (${achievements.size})"
                ),
                1 to tr9(
                    "Unlocked ($unlockedCount)",
                    "Odomknuté ($unlockedCount)",
                    "Odemčené ($unlockedCount)",
                    "Offen ($unlockedCount)",
                    "Logrados ($unlockedCount)",
                    "Débloqués ($unlockedCount)",
                    "Zdobyte ($unlockedCount)",
                    "Відкриті ($unlockedCount)",
                    "Sbloccati ($unlockedCount)"
                ),
                2 to tr9(
                    "Locked ($lockedCount)",
                    "Zamknuté ($lockedCount)",
                    "Zamčené ($lockedCount)",
                    "Gesperrt ($lockedCount)",
                    "Bloqueados ($lockedCount)",
                    "Verrouillés ($lockedCount)",
                    "Zablok. ($lockedCount)",
                    "Закриті ($lockedCount)",
                    "Bloccati ($lockedCount)"
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                filterLabels.forEach { (mode, label) ->
                    val isSelected = (filterMode == mode)
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if (isSelected) CharcoalDarkCard else GamingSurface)
                            .border(
                                1.dp,
                                if (isSelected) CharcoalDarkCard else GamingBorder,
                                RoundedCornerShape(50)
                            )
                            .clickable { filterMode = mode }
                            .padding(horizontal = 6.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            softWrap = false,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
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
                                    text = tr9(
                                        "UNLOCKED", "ODOMKNUTÉ", "ODEMČENO",
                                        "OFFEN", "LOGRADO", "DÉBLOQUÉ",
                                        "ZDOBYTE", "ВІДКРИТО", "SBLOCCATO"
                                    ),
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    softWrap = false
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
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    softWrap = false
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
