package com.example.jobnoti

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class JobNoti : Application() {

    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(
            "running_channel",
            "Running Notification",
            NotificationManager.IMPORTANCE_HIGH
        )

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}