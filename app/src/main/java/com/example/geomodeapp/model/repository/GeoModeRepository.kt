package com.example.geomodeapp.model.repository

import com.example.geomodeapp.model.data.GeoProfiles
import com.example.geomodeapp.model.room_database.dao.GeoModeProfileDao
import com.example.geomodeapp.model.room_database.entities.GeoModeProfile
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeoModeRepository @Inject constructor(
    private val geoModeProfileDao: GeoModeProfileDao
) {

    fun getAllGeoModes(): Flow<List<GeoModeProfile>> {
        return geoModeProfileDao.getAllGeoModes()
    }

    suspend fun getLocationDetails(locationName: String): GeoModeProfile{
        return geoModeProfileDao.getLocationDetails(locationName)
    }

    suspend fun addNewGeoModeLocation(geoModeProfile: GeoModeProfile){
        geoModeProfileDao.addNewGeoModeForLocation(geoModeProfile)
    }

    suspend fun deleteGeoModeLocation(locationName: String){
        geoModeProfileDao.deleteGeoModeForLocation(locationName)
    }

    suspend fun enableGeoModeForLocation(locationName: String, isLocationEnabled: Boolean){
        geoModeProfileDao.enableGeoModeForLocation(locationName, isLocationEnabled)
    }

    suspend fun isGeoModeEnabled(locationName: String): Boolean{
        return geoModeProfileDao.isGeoModeEnabled(locationName)
    }

    suspend fun readPreviousMode(locationName: String): GeoProfiles?{
        return geoModeProfileDao.readPreviousMode(locationName)
    }

    suspend fun readDesiredMode(locationName: String) : GeoProfiles{
        return geoModeProfileDao.readDesiredMode(locationName)
    }

    suspend fun updatePreviousMode(locationName: String, previousMode: GeoProfiles){
        geoModeProfileDao.updatePreviousMode(locationName, previousMode)
    }

}