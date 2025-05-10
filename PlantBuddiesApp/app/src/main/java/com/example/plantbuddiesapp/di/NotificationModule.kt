package com.example.plantbuddiesapp.di

import android.app.AlarmManager
import android.app.Application
import android.app.NotificationManager
import android.content.Context
import com.example.plantbuddiesapp.data.services.PlantCareNotificationService
import com.example.plantbuddiesapp.notifications.AndroidAlarmScheduler
import com.example.plantbuddiesapp.notifications.AlarmScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object NotificationModule {
    @Provides
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    fun provideNotificationManager(context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    @Provides
    fun provideNotificationScheduler(
        context: Context,
        notificationManager: NotificationManager,
        alarmManager: AlarmManager
    ): AlarmScheduler {
        return AndroidAlarmScheduler(context, notificationManager, alarmManager)
    }

    @Provides
    fun provideAlarmManager(context: Context): AlarmManager {
        return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    @Provides
    fun providePlantCareNotificationService(
        context: Context,
        notificationManager: NotificationManager,
        notificationScheduler: AndroidAlarmScheduler
    ): PlantCareNotificationService {
        return PlantCareNotificationService(context, notificationManager, notificationScheduler)
    }
}