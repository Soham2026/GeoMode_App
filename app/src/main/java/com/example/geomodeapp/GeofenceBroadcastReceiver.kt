package com.example.geomodeapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val geofencingEvent = intent?.let { GeofencingEvent.fromIntent(it) }
        Log.d("TAG", "Intent received $intent : $geofencingEvent")

        geofencingEvent?.let {
            if (geofencingEvent.hasError()) {
                val errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.errorCode)
                Log.e("TAG", errorMessage)
                return
            }

            // Getting the transition type.
            val geofenceTransition = geofencingEvent.geofenceTransition
            Log.d("TAG", "onReceive: $geofenceTransition")

            if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER || geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                val triggeringGeofences = geofencingEvent.triggeringGeofences

                if (triggeringGeofences != null) {
                    for (geofence in triggeringGeofences) {
                        val locationId = geofence.requestId
                        Log.d("TAG", "onReceive: $locationId")

                        val data = workDataOf(
                            "requestId" to locationId,
                            "transitionType" to geofenceTransition
                        )

                        val workRequest = OneTimeWorkRequestBuilder<DeviceModeWorker>().setInputData(data).build()
                        if (context != null) {
                            Log.d("TAG", "Context is not null, going to start workmanager")
                            WorkManager.getInstance(context).enqueue(workRequest)
                        }

                    }
                } else {
                    Log.d("TAG", "onReceive: null")

                }
            }
        }
    }
}