package com.example.geomodeapp

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.geomodeapp.model.room_database.entities.GeoModeProfile
import com.google.android.gms.location.GeofencingClient
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GeofenceService : Service() {

    companion object {
        const val ACTION_GEOFENCE_EVENT = "com.example.geomodeapp.ACTION_GEOFENCE_EVENT"
        const val ACTION_REGISTER_ALL_GEOFENCES = "com.example.geomodeapp.ACTION_REGISTER_ALL_GEOFENCES"
        const val ACTION_ADD_SINGLE_GEOFENCE = "com.example.geomodeapp.ACTION_ADD_SINGLE_GEOFENCE"
        const val ACTION_REMOVE_SINGLE_GEOFENCE = "com.example.geomodeapp.ACTION_REMOVE_SINGLE_GEOFENCE"
        const val EXTRA_LOCATION_NAME = "location_name"
        const val EXTRA_LATITUDE = "latitude"
        const val EXTRA_LONGITUDE = "longitude"
        const val EXTRA_RADIUS = "radius"
    }



    @Inject
    lateinit var geofencingClient: GeofencingClient

    @Inject
    lateinit var geofenceHelper: GeofenceHelper

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private lateinit var geofencePendingIntent: PendingIntent
    private lateinit var notification:Notification

    private val NOTIFICATION_CHANNEL_ID = "GeofenceServiceChannel"
    private val NOTIFICATION_ID = 1234
    private val NOTIFICATION_CHANNEL_NAME= "GeoMode"

    override fun onCreate() {
        super.onCreate()

        val intent = Intent(this, GeofenceBroadcastReceiver::class.java).apply {
            action = ACTION_GEOFENCE_EVENT
        }

        geofencePendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("GeoMode app running")
            .setContentText("GeoMode app would look for location changes upon having active geofences")
            .setSmallIcon(R.drawable.notification_con)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()


    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        startForeground(1,notification)

        when (intent?.action) {
            ACTION_ADD_SINGLE_GEOFENCE -> {
                val id = intent.getStringExtra(EXTRA_LOCATION_NAME)
                val latitude = intent.getDoubleExtra(EXTRA_LATITUDE, 0.0)
                val longitude = intent.getDoubleExtra(EXTRA_LONGITUDE, 0.0)
                val radius = intent.getFloatExtra(EXTRA_RADIUS, 0f)

                serviceScope.launch {
                    if (id != null && latitude != 0.0 && longitude != 0.0 && radius != 0f) {
                        val geofence = geofenceHelper.buildGeofence(id,latitude,longitude,radius)
                        val geofencingRequest = geofenceHelper.getGeofencingRequest(geofence)
                        Log.d("TAG", "Pending Intent: $geofencePendingIntent")

                        if((ContextCompat.checkSelfPermission(this@GeofenceService,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED )
                            && (ContextCompat.checkSelfPermission(this@GeofenceService,Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED )){

                            geofencingClient.addGeofences(geofencingRequest,geofencePendingIntent).run {
                                addOnSuccessListener {
                                    Toast.makeText(this@GeofenceService, "Geofence added successfully", Toast.LENGTH_SHORT).show()

                                }
                                addOnFailureListener {
                                    Toast.makeText(this@GeofenceService, "Failed to add Geofence", Toast.LENGTH_SHORT).show()
                                }
                            }

                        }
                    } else {
                        Log.e("GeofenceService", "Missing data in ADD_SINGLE_GEOFENCE intent. Cannot add.")
                    }
                }

            }
            ACTION_REMOVE_SINGLE_GEOFENCE ->{
                val locationName = intent.getStringExtra(EXTRA_LOCATION_NAME)
                if (locationName != null) {
                    Log.d("GeofenceService", "Received ACTION_REMOVE_SINGLE_GEOFENCE for $locationName")
                    geofencingClient.removeGeofences(listOf(locationName)).addOnSuccessListener {
                        Toast.makeText(this, "Removed geofence: $locationName", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener{
                        Toast.makeText(this, "Failed to remove geofence: $locationName", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Log.e("GeofenceService", "No locationName in REMOVE_SINGLE_GEOFENCE intent. Cannot remove.")
                }
            }
            else -> {
                Log.e("GeofenceService", "Unknown action: ${intent?.action}")
            }
        }

        return START_STICKY
    }


}