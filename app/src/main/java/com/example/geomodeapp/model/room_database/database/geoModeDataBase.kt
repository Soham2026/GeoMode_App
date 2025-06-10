package com.example.geomodeapp.model.room_database.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.geomodeapp.model.room_database.dao.GeoModeProfileDao
import com.example.geomodeapp.model.room_database.entities.GeoModeProfile

@Database(entities = [GeoModeProfile::class],version = 1)
@TypeConverters(com.example.geomodeapp.model.room_database.database.TypeConverters ::class)
abstract class GeoModeDataBase:RoomDatabase() {
    abstract fun getGeoModeProfileDao(): GeoModeProfileDao
}