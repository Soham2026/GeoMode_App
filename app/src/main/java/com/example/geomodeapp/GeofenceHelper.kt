package com.example.geomodeapp

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeofenceHelper @Inject constructor(@ApplicationContext private val context: Context) {

    private val ACTION_GEOFENCE_EVENT = "com.example.geomodeapp.ACTION_GEOFENCE_EVENT"
    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java).apply {
            action = ACTION_GEOFENCE_EVENT
        }
        PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
    }

    fun buildGeofence(
        locationName: String,
        latitude: Double,
        longitude: Double,
        radiusInMeters: Float
    ): Geofence {
        return Geofence.Builder()
            .setRequestId(locationName)
            .setCircularRegion(
                latitude, longitude, radiusInMeters
            )
            .setExpirationDuration(Geofence.NEVER_EXPIRE)
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
            .build()
    }

    fun getGeofencingRequest(geofence: Geofence): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(listOf(geofence))
        }.build()
    }

    fun getPendingIntent(): PendingIntent {
        return geofencePendingIntent
    }
}