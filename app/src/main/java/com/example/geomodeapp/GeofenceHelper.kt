package com.example.geomodeapp


import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeofenceHelper @Inject constructor() {

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
            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT or Geofence.GEOFENCE_TRANSITION_DWELL)
            .setLoiteringDelay(15000)
            .build()
    }

    fun getGeofencingRequest(geofence: Geofence): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(listOf(geofence))
        }.build()
    }

}