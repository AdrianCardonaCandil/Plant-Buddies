package com.example.plantbuddiesapp.notifications

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.plantbuddiesapp.data.services.PlantCareNotificationService
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class AndroidAlarmScheduler @Inject constructor(
    private val context: Context,
    private val notificationManager: NotificationManager,
    private val alarmManager: AlarmManager
) : AlarmScheduler {

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                PlantCareNotificationService.CHANNEL_ID,
                "Plant Care",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Notifications for plant care"
            notificationManager.createNotificationChannel(channel)
            println("Notification channel created")
        }
    }

    override fun schedule(request: AlarmRequest) {
        val pendingIntent = createPendingIntent(request)

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            request.time.atZone(ZoneId.systemDefault()).toEpochSecond() * 1000,
            pendingIntent
        )
    }

    override fun cancel(requestId: String) {
        alarmManager.cancel(
            PendingIntent.getBroadcast(
                context,
                requestId.hashCode(),
                Intent(context, AlarmReceiver::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        )
    }

    private fun createPendingIntent(request: AlarmRequest): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("notification_id", request.id)
            putExtra("notification_title", request.title)
            putExtra("notification_content", request.content)
        }
        return PendingIntent.getBroadcast(context, request.id.hashCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }
}