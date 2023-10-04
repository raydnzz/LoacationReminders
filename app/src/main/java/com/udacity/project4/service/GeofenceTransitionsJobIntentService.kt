@file:Suppress("DEPRECATION")

package com.udacity.project4.service

import android.content.Context
import android.content.Intent
import androidx.core.app.JobIntentService
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.udacity.project4.data.database.entites.Location
import com.udacity.project4.domain.location.LocationUseCase
import com.udacity.project4.utils.isSuccess
import com.udacity.project4.utils.sendNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

@Suppress("DEPRECATION")
class GeofenceTransitionsJobIntentService : JobIntentService(), CoroutineScope {

    private var coroutineJob: Job = SupervisorJob()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + coroutineJob

    companion object {
        private const val JOB_ID = 573
        private const val TAG = "GeofenceTransitionsJobIntentService"

        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(
                context,
                GeofenceTransitionsJobIntentService::class.java, JOB_ID,
                intent
            )
        }
    }

    override fun onHandleWork(intent: Intent) {
        GeofencingEvent.fromIntent(intent)?.run {
            if (triggeringGeofences.isNullOrEmpty() || geofenceTransition != Geofence.GEOFENCE_TRANSITION_ENTER) return
            send(triggeringGeofences)
        }
    }

    private fun send(triggeringGeofences: List<Geofence>?) {
        triggeringGeofences?.forEach {
            val requestId = it.requestId
            val locationDataSource: LocationUseCase by inject()
            CoroutineScope(coroutineContext).launch {
                val result = locationDataSource.getLocation(requestId)
                result.isSuccess { location ->
                    sendNotification(
                        this@GeofenceTransitionsJobIntentService, Location(
                            title = location.title,
                            description = location.description,
                            locationName = location.locationName,
                            latitude = location.latitude,
                            longitude = location.longitude,
                            id = location.id
                        )
                    )
                }
            }
        }
    }
}