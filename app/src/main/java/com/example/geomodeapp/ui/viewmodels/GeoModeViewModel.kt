package com.example.geomodeapp.ui.viewmodels

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geomodeapp.GeofenceHelper
import com.example.geomodeapp.GeofenceService
import com.example.geomodeapp.model.data.GeoProfiles
import com.example.geomodeapp.model.repository.GeoModeRepository
import com.example.geomodeapp.model.room_database.entities.GeoModeProfile
import com.google.android.gms.location.GeofencingClient
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GeoModeViewModel @Inject constructor(
    @ApplicationContext val context: Context, private val geoModeRepository: GeoModeRepository
) : ViewModel() {
    private val _geoModesList = MutableStateFlow<List<GeoModeProfile>>(emptyList())
    val geoModesList: StateFlow<List<GeoModeProfile>> = _geoModesList

    init {
        getAllGeoModes()
    }

    fun getAllGeoModes() {
        viewModelScope.launch {
            geoModeRepository.getAllGeoModes().collect() {
                _geoModesList.value = it
            }
        }
    }

    fun addNewGeoModeLocation(geoModeProfile: GeoModeProfile) {
        viewModelScope.launch {
            startGeofenceService(
                GeofenceService.ACTION_ADD_SINGLE_GEOFENCE,
                geoModeProfile.locationName,
                geoModeProfile.latitude,
                geoModeProfile.longitude,
                geoModeProfile.radius
            )
        }
        viewModelScope.launch {
            geoModeRepository.addNewGeoModeLocation(geoModeProfile)
        }
    }

    fun deleteGeoModeLocation(locationName: String, onGeofenceRemoved: () -> Unit = {}) {
        viewModelScope.launch {
            geoModeRepository.deleteGeoModeLocation(locationName)
            startGeofenceService(GeofenceService.ACTION_REMOVE_SINGLE_GEOFENCE, locationName)
        }
    }

    fun enableGeoModeForLocation(
        locationName: String,
        isLocationEnabled: Boolean,
        onGeofenceAbled: () -> Unit = {},
        onGeofenceDisabled: () -> Unit = {}
    ) {

        viewModelScope.launch {
            if (!isLocationEnabled) {
                startGeofenceService(GeofenceService.ACTION_REMOVE_SINGLE_GEOFENCE, locationName)
            } else {
                getLocationDetails(locationName) {
                    startGeofenceService(
                        GeofenceService.ACTION_ADD_SINGLE_GEOFENCE,
                        it.locationName,
                        it.latitude,
                        it.longitude,
                        it.radius
                    )
                }
            }
        }

        viewModelScope.launch {
            geoModeRepository.enableGeoModeForLocation(locationName, isLocationEnabled)
        }

    }


    fun getLocationDetails(locationName: String, onResultReceived: (GeoModeProfile) -> Unit) {
        viewModelScope.launch {
            val locationDetails = geoModeRepository.getLocationDetails(locationName)
            onResultReceived(locationDetails)
        }
    }


    private fun startGeofenceService(
        action: String,
        locationName: String? = null,
        latitude: Double? = null,
        longitude: Double? = null,
        radius: Float? = null
    ) {

        val serviceIntent = Intent(context, GeofenceService::class.java).apply {
            this.action = action
            locationName?.let { putExtra(GeofenceService.EXTRA_LOCATION_NAME, it) }

            when (action) {
                GeofenceService.ACTION_ADD_SINGLE_GEOFENCE -> {
                    latitude?.let { putExtra(GeofenceService.EXTRA_LATITUDE, it) }
                    longitude?.let { putExtra(GeofenceService.EXTRA_LONGITUDE, it) }
                    radius?.let { putExtra(GeofenceService.EXTRA_RADIUS, it) }
                }
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

}