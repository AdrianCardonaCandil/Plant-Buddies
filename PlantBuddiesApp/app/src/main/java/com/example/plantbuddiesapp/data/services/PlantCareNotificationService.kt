package com.example.plantbuddiesapp.data.services

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.example.plantbuddiesapp.R
import com.example.plantbuddiesapp.notifications.AndroidAlarmScheduler
import com.example.plantbuddiesapp.notifications.AlarmRequest
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject

class PlantCareNotificationService @Inject constructor(
    private val context: Context,
    private val notificationManager: NotificationManager,
    private val alarmScheduler: AndroidAlarmScheduler
) {
    fun scheduleNotification(
        id: String,
        title: String,
        text: String,
        triggerTime: LocalDateTime
    ) {
        val zonedDateTime = triggerTime.atZone(ZoneId.systemDefault())

        println("Notification scheduled for ${zonedDateTime.toLocalDate()} at ${zonedDateTime.toLocalTime()}")

        alarmScheduler.schedule(
            AlarmRequest(
                id = id,
                title = title,
                content = text,
                time = triggerTime
            )
        )
    }

    companion object {
        const val CHANNEL_ID = "plant-care-channel"
    }
}