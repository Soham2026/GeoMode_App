package com.example.geomodeapp.di

import android.content.Context
import androidx.room.Room
import com.example.geomodeapp.model.room_database.dao.GeoModeProfileDao
import com.example.geomodeapp.model.room_database.database.GeoModeDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

object DatabaseModule{

    @Provides
    @Singleton
    fun getDatabaseInstance(
        @ApplicationContext context: Context
    ): GeoModeDataBase{
        val database = Room.databaseBuilder(
            context,
            GeoModeDataBase::class.java,
            "geoMode_database"
        ).build()

        return database
    }

    @Provides
    fun getDao(database:GeoModeDataBase): GeoModeProfileDao = database.getGeoModeProfileDao()

}