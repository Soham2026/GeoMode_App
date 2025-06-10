package com.example.geomodeapp.ui.viewmodels

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.geomodeapp.GeofenceHelper
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
    @ApplicationContext val context: Context,
    private val geoModeRepository: GeoModeRepository,
    private val geofenceHelper: GeofenceHelper,
    private val geofencingClient: GeofencingClient
):ViewModel() {
    private val _geoModesList = MutableStateFlow<List<GeoModeProfile>>(emptyList())
    val geoModesList: StateFlow<List<GeoModeProfile>> = _geoModesList

    init {
        getAllGeoModes()
    }

    fun getAllGeoModes(){
        viewModelScope.launch {
            geoModeRepository.getAllGeoModes().collect(){
                _geoModesList.value=it
            }
        }
    }

    fun addNewGeoModeLocation(geoModeProfile: GeoModeProfile){
        viewModelScope.launch {
            geoModeRepository.addNewGeoModeLocation(geoModeProfile)
        }
        val id = geoModeProfile.locationName
        val latitude = geoModeProfile.latitude
        val longitude = geoModeProfile.longitude
        val radius = geoModeProfile.radius
        addGeofence(id,latitude,longitude,radius)

    }

    fun deleteGeoModeLocation(locationName: String,onGeofenceRemoved:() -> Unit={}){
        viewModelScope.launch {
            geoModeRepository.deleteGeoModeLocation(locationName)
        }
        geofencingClient.removeGeofences(listOf(locationName)).addOnSuccessListener {
            Toast.makeText(context, "Removed geofence: $locationName", Toast.LENGTH_SHORT).show()
            onGeofenceRemoved()
        }
    }

    fun enableGeoModeForLocation(locationName: String, isLocationEnabled: Boolean, onGeofenceAbled:() -> Unit={},onGeofenceDisabled:() -> Unit={}){
        viewModelScope.launch {
            geoModeRepository.enableGeoModeForLocation(locationName, isLocationEnabled)
        }

        if(!isLocationEnabled){
            geofencingClient.removeGeofences(listOf(locationName)).addOnSuccessListener {
                Toast.makeText(context, "Turned off geofence: $locationName", Toast.LENGTH_SHORT).show()
                onGeofenceDisabled()
            }
        }else{
             getLocationDetails(locationName){data ->
                addGeofence(data.locationName,data.latitude,data.longitude,data.radius,onGeofenceAbled)
            }
        }

    }


    fun getLocationDetails(locationName: String, onResultReceived: (GeoModeProfile) -> Unit){
        viewModelScope.launch {
            val locationDetails = geoModeRepository.getLocationDetails(locationName)
            onResultReceived(locationDetails)
        }
    }

    private fun addGeofence(id: String, latitude: Double, longitude: Double, radius: Float,onGeofenceAdded:() -> Unit={}){
        val geofence = geofenceHelper.buildGeofence(id,latitude,longitude,radius)
        val geofencingRequest = geofenceHelper.getGeofencingRequest(geofence)
        val pendingIntent = geofenceHelper.getPendingIntent()
        Log.d("TAG", "Pending Intent: $pendingIntent")

        if((ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED )
            && (ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED )){

            geofencingClient.addGeofences(geofencingRequest,pendingIntent).run {
                addOnSuccessListener {
                    Toast.makeText(context, "Geofence added successfully", Toast.LENGTH_SHORT).show()
                    onGeofenceAdded()
                }
                addOnFailureListener {
                    Toast.makeText(context, "Failed to add Geofence", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

}