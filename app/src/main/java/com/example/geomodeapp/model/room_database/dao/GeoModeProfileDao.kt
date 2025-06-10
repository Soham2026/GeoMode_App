package com.example.geomodeapp.model.room_database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.geomodeapp.model.data.GeoProfiles
import com.example.geomodeapp.model.room_database.entities.GeoModeProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface GeoModeProfileDao {

    @Insert
    suspend fun addNewGeoModeForLocation(geoModeProfile: GeoModeProfile)

    @Query("SELECT isLocationEnabled FROM GeoModeProfile WHERE locationName = :locationName")
    suspend fun isGeoModeEnabled(locationName: String): Boolean

    @Query("UPDATE GeoModeProfile SET isLocationEnabled = :isLocationEnabled WHERE locationName = :locationName")
    suspend fun enableGeoModeForLocation(locationName: String, isLocationEnabled: Boolean)

    @Query("SELECT * FROM GeoModeProfile")
    fun getAllGeoModes(): Flow<List<GeoModeProfile>>

    @Query("DELETE FROM GeoModeProfile WHERE locationName = :locationName")
    suspend fun deleteGeoModeForLocation(locationName: String)

    @Query("SELECT previousMode FROM GeoModeProfile WHERE locationName = :locationName")
    suspend fun readPreviousMode(locationName: String): GeoProfiles?

    @Query("SELECT desiredMode FROM GeoModeProfile WHERE locationName = :locationName")
    suspend fun readDesiredMode(locationName: String): GeoProfiles

    @Query("UPDATE GeoModeProfile SET previousMode = :previousMode WHERE locationName= :locationName")
    suspend fun updatePreviousMode(locationName: String, previousMode: GeoProfiles)

    @Query("SELECT * FROM GeoModeProfile WHERE locationName = :locationName")
    suspend fun getLocationDetails(locationName: String): GeoModeProfile

}