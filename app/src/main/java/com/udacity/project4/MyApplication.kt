package com.udacity.project4

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import com.udacity.project4.di.appModule
import com.udacity.project4.utils.Constants.CHANNEL_ID
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(androidContext = this@MyApplication)
            modules(appModule)
        }

        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val name = "Channel DownLoad"
        val description = "Channel DownLoad Description"
        val importance = NotificationManager.IMPORTANCE_MIN
        val channel = NotificationChannel(CHANNEL_ID, name, importance)
        channel.description = description
        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }
}
