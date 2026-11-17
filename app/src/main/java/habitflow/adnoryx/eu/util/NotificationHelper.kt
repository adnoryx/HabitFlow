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
    private const val KEY_STREAK_SAVER_ENABLED = "streak_saver_enabled"
    private const val KEY_STREAK_SAVER_TIME = "streak_saver_time" // "HH:mm"
    private const val STREAK_SAVER_REQ_CODE = 999999

    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Habit Reminders & Streak Saver",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily reminders for your routines and evening streak protection"
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

        val type = intent.getStringExtra("TYPE") ?: "HABIT"
        if (type == "HABIT") {
            val habitId = intent.getLongExtra("HABIT_ID", 0L)
            val title = intent.getStringExtra("HABIT_TITLE") ?: "Routine Reminder"
            val icon = intent.getStringExtra("HABIT_ICON") ?: "⚡"

            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val today = DateUtils.getTodayString()
                    val completions = db.habitDao().getCompletionsListForDate(today)
                    val done = completions.any { it.habitId == habitId && it.isCompleted }
                    if (!done) {
                        NotificationHelper.showNotification(
                            context,
                            habitId.toInt(),
                            "$icon $title",
                            "Time to complete your routine and earn XP!"
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
                        NotificationHelper.showNotification(
                            context,
                            999999,
                            "🔥 Streak Saver Alert!",
                            "You still have $remaining routine(s) left today! Complete them or use a Day Freeze to protect your ${streak}d streak."
                        )
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
