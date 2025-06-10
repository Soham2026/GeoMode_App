package com.example.geomodeapp.model.room_database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.geomodeapp.model.data.GeoProfiles

@Entity
data class GeoModeProfile(
    @PrimaryKey val locationName:String,
    val latitude: Double,
    val longitude: Double,
    val radius: Float,
    val isLocationEnabled: Boolean,
    val desiredMode: GeoProfiles,
    val previousMode: GeoProfiles? = null
)
