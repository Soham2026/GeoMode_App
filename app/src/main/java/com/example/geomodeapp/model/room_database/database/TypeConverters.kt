package com.example.geomodeapp.model.room_database.database

import androidx.room.TypeConverter
import com.example.geomodeapp.model.data.GeoProfiles

class TypeConverters {

    @TypeConverter
    fun fromGeoProfilesToString(geoProfiles: GeoProfiles): String = geoProfiles.name

    @TypeConverter
    fun fromStringToGeoProfiles(geoProfile:String):GeoProfiles = GeoProfiles.valueOf(geoProfile)

}