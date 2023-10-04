package com.udacity.project4.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.udacity.project4.BuildConfig
import com.udacity.project4.R
import com.udacity.project4.data.database.entites.Location
import com.udacity.project4.data.repository.dto.Result
import com.udacity.project4.ui.locationdetail.LocationDetailFragmentArgs

fun <T : Any> Result<T>.isSuccess(callback: (t: T) -> Unit) {
    if (this is Result.Success) {
        callback.invoke(data)
    }
}

fun <T : Any> Result<T>.isError(callback: (s: String?) -> Unit) {
    if (this is Result.Error) {
        callback.invoke(message)
    }
}

private const val NOTIFICATION_CHANNEL_ID = BuildConfig.APPLICATION_ID + ".channel"

fun sendNotification(context: Context, location: Location) {
    val notificationManager = context
        .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // We need to create a NotificationChannel associated with our CHANNEL_ID before sending a notification.
    if (notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID) == null) {
        val name = context.getString(R.string.app_name)
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            name,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

//    build the notification object with the data to be shown
    val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(location.title)
        .setContentText(location.locationName)
        .setContentIntent(
            NavDeepLinkBuilder(context)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.locationDetailFragment)
                .setArguments(LocationDetailFragmentArgs(location = location).toBundle())
                .createPendingIntent()
        )
        .setAutoCancel(true)
        .build()

    notificationManager.notify(getUniqueId(), notification)
}

private fun getUniqueId() = ((System.currentTimeMillis() % 10000).toInt())