package com.islamic.tasbihcounter.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.islamic.tasbihcounter.R
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalTime

const val REMINDER_CHANNEL_ID = "dhikr_reminders"

/** Creates the notification channel used for morning/evening adhkar reminders. */
fun createReminderChannel(context: Context) {
    val channel = NotificationChannel(
        REMINDER_CHANNEL_ID,
        "Dhikr Reminders",
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply { description = "Gentle reminders for morning and evening adhkar" }
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.createNotificationChannel(channel)
}

/** Schedules (or cancels) the two daily reminder work requests. */
object ReminderScheduler {
    private const val MORNING_WORK = "reminder_morning"
    private const val EVENING_WORK = "reminder_evening"

    fun schedule(context: Context, morningHour: Int, eveningHour: Int) {
        enqueue(context, MORNING_WORK, morningHour, "Morning Adhkar",
            "Begin your day with dhikr — a moment of remembrance.")
        enqueue(context, EVENING_WORK, eveningHour, "Evening Adhkar",
            "Close your day with dhikr — seek peace in remembrance.")
    }

    fun cancel(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(MORNING_WORK)
        WorkManager.getInstance(context).cancelUniqueWork(EVENING_WORK)
    }

    private fun enqueue(context: Context, name: String, hour: Int, title: String, body: String) {
        val request = PeriodicWorkRequestBuilder<ReminderWorker>(Duration.ofDays(1))
            .setInitialDelay(initialDelayMinutes(hour), java.util.concurrent.TimeUnit.MINUTES)
            .setConstraints(Constraints.Builder().build())
            .setInputData(workDataOf(ReminderWorker.KEY_TITLE to title, ReminderWorker.KEY_BODY to body))
            .addTag(name)
            .build()
        WorkManager.getInstance(context)
            .enqueueUniquePeriodicWork(name, ExistingPeriodicWorkPolicy.UPDATE, request)
    }

    private fun initialDelayMinutes(hour: Int): Long {
        val now = LocalDateTime.now()
        var next = now.toLocalDate().atTime(LocalTime.of(hour.coerceIn(0, 23), 0))
        if (!next.isAfter(now)) next = next.plusDays(1)
        return Duration.between(now, next).toMinutes().coerceAtLeast(1)
    }
}

/** Posts a reminder notification when its scheduled time arrives. */
class ReminderWorker(
    private val appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val title = inputData.getString(KEY_TITLE) ?: "Dhikr Reminder"
        val body = inputData.getString(KEY_BODY) ?: "Time for remembrance."

        if (ContextCompat.checkSelfPermission(appContext, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            return Result.success()
        }

        val notification = NotificationCompat.Builder(appContext, REMINDER_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_dhikr)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(appContext).notify(id.hashCode(), notification)
        return Result.success()
    }

    companion object {
        const val KEY_TITLE = "title"
        const val KEY_BODY = "body"
    }
}
