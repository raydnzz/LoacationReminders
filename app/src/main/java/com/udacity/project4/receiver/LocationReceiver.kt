package com.udacity.project4.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.udacity.project4.service.GeofenceTransitionsJobIntentService
import com.udacity.project4.utils.Constants

class LocationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Constants.ACTION_LOCATION_REMINDER) {
            GeofenceTransitionsJobIntentService.enqueueWork(context, intent)
        }
    }
}