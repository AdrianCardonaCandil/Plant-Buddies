package com.example.plantbuddiesapp.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.plantbuddiesapp.R


class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.getStringExtra("notification_id") ?: ""
        val title = intent.getStringExtra("notification_title") ?: ""
        val content = intent.getStringExtra("notification_content") ?: ""
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, "plant-care-channel")
            .setSmallIcon(R.drawable.plants_empty_photo)
            .setContentTitle(title)
            .setContentText(content)
            .build()
        notificationManager.notify(id.hashCode(), notification)
    }
}