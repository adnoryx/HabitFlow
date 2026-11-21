package habitflow.adnoryx.eu.util

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import habitflow.adnoryx.eu.MainActivity
import habitflow.adnoryx.eu.R
import habitflow.adnoryx.eu.data.local.AppDatabase
import habitflow.adnoryx.eu.data.model.Habit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

object NotificationHelper {
    const val CHANNEL_ID = "habitflow_reminders"
    private const val PREFS_NAME = "habitflow_notif_prefs"
    private const val LANG_PREFS_NAME = "habitflow_prefs"
    private const val KEY_LANG = "selected_language"
    private const val KEY_STREAK_SAVER_ENABLED = "streak_saver_enabled"
    private const val KEY_STREAK_SAVER_TIME = "streak_saver_time" // "HH:mm"
    private const val STREAK_SAVER_REQ_CODE = 999999

    fun getSavedLangCode(context: Context): String {
        val prefs = context.getSharedPreferences(LANG_PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_LANG, "en") ?: "en"
    }

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val lang = getSavedLangCode(context)
            val channelName = when (lang) {
                "sk" -> "Pripomienky návykov a Streak Saver"
                "cs" -> "Připomínky návyků a Streak Saver"
                "de" -> "Routinen-Erinnerungen & Streak-Schutz"
                "es" -> "Recordatorios de hábitos y protector de racha"
                "fr" -> "Rappels d'habitudes et sauvegarde de série"
                "pl" -> "Przypomnienia o nawykach i Strażnik Serii"
                "uk" -> "Нагадування про звички та захист серії"
                "it" -> "Promemoria abitudini e salva-serie"
                else -> "Habit Reminders & Streak Saver"
            }
            val channelDesc = when (lang) {
                "sk" -> "Denné pripomienky rutín a večerná ochrana streaku"
                "cs" -> "Denní připomínky rutin a večerní ochrana streaku"
                "de" -> "Tägliche Erinnerungen für deine Routinen und Abend-Streak-Schutz"
                "es" -> "Recordatorios diarios para tus rutinas y protección nocturna de racha"
                "fr" -> "Rappels quotidiens pour vos routines et protection de série le soir"
                "pl" -> "Codzienne przypomnienia o rutynach i wieczorna ochrona serii"
                "uk" -> "Щоденні нагадування про рутини та вечірній захист серії"
                "it" -> "Promemoria giornalieri per le tue routine e protezione serale della serie"
                else -> "Daily reminders for your routines and evening streak protection"
            }
            val channel = NotificationChannel(
                CHANNEL_ID,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = channelDesc
            }
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    fun isStreakSaverEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_STREAK_SAVER_ENABLED, true)
    }

    fun setStreakSaverEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_STREAK_SAVER_ENABLED, enabled).apply()
        if (enabled) {
            scheduleStreakSaver(context, getStreakSaverTime(context))
        } else {
            cancelStreakSaver(context)
        }
    }

    fun getStreakSaverTime(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_STREAK_SAVER_TIME, "20:30") ?: "20:30"
    }

    fun setStreakSaverTime(context: Context, time: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_STREAK_SAVER_TIME, time).apply()
        if (isStreakSaverEnabled(context)) {
            scheduleStreakSaver(context, time)
        }
    }

    fun scheduleHabitReminder(context: Context, habit: Habit) {
        val time = habit.reminderTime
        if (time.isNullOrBlank() || habit.isArchived || habit.id == 0L) {
            cancelHabitReminder(context, habit.id)
            return
        }

        val parts = time.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: return
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: return

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("TYPE", "HABIT")
            putExtra("HABIT_ID", habit.id)
            putExtra("HABIT_TITLE", habit.title)
            putExtra("HABIT_ICON", habit.icon)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            habit.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun cancelHabitReminder(context: Context, habitId: Long) {
        if (habitId == 0L) return
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            habitId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun scheduleStreakSaver(context: Context, time: String = getStreakSaverTime(context)) {
        if (!isStreakSaverEnabled(context)) return
        val parts = time.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: 20
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: 30

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra("TYPE", "STREAK_SAVER")
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            STREAK_SAVER_REQ_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        try {
            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun cancelStreakSaver(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            STREAK_SAVER_REQ_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun showNotification(context: Context, notificationId: Int, title: String, message: String) {
        createNotificationChannel(context)
        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        try {
            manager.notify(notificationId, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val habits = db.habitDao().getAllActiveHabitsList()
                    habits.forEach { habit ->
                        if (!habit.reminderTime.isNullOrBlank()) {
                            NotificationHelper.scheduleHabitReminder(context, habit)
                        }
                    }
                    if (NotificationHelper.isStreakSaverEnabled(context)) {
                        NotificationHelper.scheduleStreakSaver(context)
                    }
                } finally {
                    pendingResult.finish()
                }
            }
            return
        }

        val lang = NotificationHelper.getSavedLangCode(context)
        val type = intent.getStringExtra("TYPE") ?: "HABIT"
        if (type == "HABIT") {
            val habitId = intent.getLongExtra("HABIT_ID", 0L)
            val defaultTitle = when (lang) {
                "sk" -> "Pripomienka rutiny"
                "cs" -> "Připomínka rutiny"
                "de" -> "Routinen-Erinnerung"
                "es" -> "Recordatorio de rutina"
                "fr" -> "Rappel de routine"
                "pl" -> "Przypomnienie o rutynie"
                "uk" -> "Нагадування про рутину"
                "it" -> "Promemoria routine"
                else -> "Routine Reminder"
            }
            val title = intent.getStringExtra("HABIT_TITLE") ?: defaultTitle
            val icon = intent.getStringExtra("HABIT_ICON") ?: "⚡"

            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val today = DateUtils.getTodayString()
                    val completions = db.habitDao().getCompletionsListForDate(today)
                    val done = completions.any { it.habitId == habitId && it.isCompleted }
                    if (!done) {
                        val bodyMsg = when (lang) {
                            "sk" -> "Je čas splniť svoju rutinu a získať XP!"
                            "cs" -> "Je čas splnit svou rutinu a získat XP!"
                            "de" -> "Zeit, deine Routine abzuschließen und XP zu sammeln!"
                            "es" -> "¡Es hora de completar tu rutina y ganar XP!"
                            "fr" -> "Il est temps de compléter votre routine et de gagner des XP !"
                            "pl" -> "Czas wykonać swoją rutynę i zdobyć XP!"
                            "uk" -> "Час виконати свою рутину та отримати XP!"
                            "it" -> "È ora di completare la tua routine e guadagnare XP!"
                            else -> "Time to complete your routine and earn XP!"
                        }
                        NotificationHelper.showNotification(
                            context,
                            habitId.toInt(),
                            "$icon $title",
                            bodyMsg
                        )
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        } else if (type == "STREAK_SAVER") {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val today = DateUtils.getTodayString()
                    val activeHabits = db.habitDao().getAllActiveHabitsList()
                    val completions = db.habitDao().getCompletionsListForDate(today)
                        .filter { it.isCompleted }
                    val profile = db.userProfileDao().getUserProfile()

                    val remaining = activeHabits.size - completions.size
                    val freezeUsed = (profile?.freezesUsedToday ?: 0) > 0

                    if (activeHabits.isNotEmpty() && remaining > 0 && !freezeUsed) {
                        val streak = profile?.currentStreak ?: 0
                        val alertTitle = when (lang) {
                            "sk" -> "🔥 Pozor na svoj Streak!"
                            "cs" -> "🔥 Pozor na svůj Streak!"
                            "de" -> "🔥 Streak-Schutz-Warnung!"
                            "es" -> "🔥 ¡Alerta de protección de racha!"
                            "fr" -> "🔥 Alerte sauvegarde de série !"
                            "pl" -> "🔥 Uwaga na Twoją serię!"
                            "uk" -> "🔥 Увага! Захистіть свою серію!"
                            "it" -> "🔥 Avviso salva-serie!"
                            else -> "🔥 Streak Saver Alert!"
                        }
                        val alertBody = when (lang) {
                            "sk" -> "Dnes ti ešte zostáva splniť $remaining rutín! Dokonči ich alebo použi Day Freeze na ochranu ${streak}d streaku."
                            "cs" -> "Dnes ti ještě zbývá splnit $remaining rutin! Dokonči je nebo použij Day Freeze k ochraně ${streak}d streaku."
                            "de" -> "Du hast heute noch $remaining offene Routine(n)! Schließe sie ab oder nutze einen Tages-Freeze für deinen ${streak}T-Streak."
                            "es" -> "¡Aún te quedan $remaining rutina(s) hoy! Complétalas o usa un Congelador para proteger tu racha de ${streak}d."
                            "fr" -> "Il vous reste encore $remaining routine(s) aujourd'hui ! Complétez-les ou utilisez un Gel pour protéger votre série de ${streak}j."
                            "pl" -> "Masz dziś jeszcze $remaining rutyn(y) do wykonania! Ukończ je lub użyj Zamrożenia Dnia, by chronić serię ${streak}d."
                            "uk" -> "Сьогодні залишилося виконати ще $remaining рутин! Завершіть їх або використайте Заморозку для захисту серії ${streak}д."
                            "it" -> "Hai ancora $remaining routine oggi! Completale o usa un Congelamento per proteggere la tua serie di ${streak}g."
                            else -> "You still have $remaining routine(s) left today! Complete them or use a Day Freeze to protect your ${streak}d streak."
                        }
                        NotificationHelper.showNotification(
                            context,
                            999999,
                            alertTitle,
                            alertBody
                        )
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
