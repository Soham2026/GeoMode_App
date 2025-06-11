package com.example.geomodeapp

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.geomodeapp.model.data.GeoProfiles
import com.example.geomodeapp.model.repository.GeoModeRepository
import com.google.android.gms.location.Geofence
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class DeviceModeWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val geoModeRepository: GeoModeRepository,
    private val soundModeManager: SoundModeManager
) : CoroutineWorker(appContext, params)
{

    override suspend fun doWork(): Result {

        val requestId = inputData.getString("requestId")?: return Result.failure()
        val transitionType = inputData.getInt("transitionType", -1)
        Log.d("TAG"," From WorkManager -> requestId: $requestId, transitionType: $transitionType")

        try {
            when(transitionType){
                Geofence.GEOFENCE_TRANSITION_ENTER ->{
                    Log.d("TAG"," From WorkManager -> Entered geofence: $requestId")

                    val desiredMode= geoModeRepository.readDesiredMode(requestId)
                    val currentModeCode = soundModeManager.getCurrentMode()
                    val isDoNotDisturbEnabled = soundModeManager.isDoNotDisturbEnabled()
                    val currentMode = soundModeManager.mapIntToGeoProfile(currentModeCode,isDoNotDisturbEnabled)

                    geoModeRepository.updatePreviousMode(requestId,currentMode)

                    when(desiredMode){
                        GeoProfiles.SILENT ->{
                            soundModeManager.setRingerModeSilent()
                        }
                        GeoProfiles.VIBRATE ->{
                            soundModeManager.setRingerModeVibrate()
                        }
                        GeoProfiles.NORMAL -> {
                            soundModeManager.setRingerModeNormal()
                        }
                        GeoProfiles.DND -> {
                            soundModeManager.EnableDoNotDisturb()
                        }
                        else -> {
                            soundModeManager.setRingerModeNormal()
                        }
                    }
                }
                Geofence.GEOFENCE_TRANSITION_EXIT -> {
                    Log.d("TAG"," From WorkManager -> Exited geofence: $requestId")
                    val previousMode = geoModeRepository.readPreviousMode(requestId)

                    when(previousMode){
                        GeoProfiles.SILENT ->{
                            soundModeManager.setRingerModeSilent()
                            if(soundModeManager.isDoNotDisturbEnabled()){
                                soundModeManager.DisableDoNotDisturb()
                            }
                        }
                        GeoProfiles.VIBRATE -> {
                            soundModeManager.setRingerModeVibrate()
                            if(soundModeManager.isDoNotDisturbEnabled()){
                                soundModeManager.DisableDoNotDisturb()
                            }
                        }
                        GeoProfiles.NORMAL ->{
                            soundModeManager.setRingerModeNormal()
                            if(soundModeManager.isDoNotDisturbEnabled()){
                                soundModeManager.DisableDoNotDisturb()
                            }
                        }
                        GeoProfiles.DND -> {
                            soundModeManager.DisableDoNotDisturb()
                        }
                        else -> {
                            soundModeManager.setRingerModeNormal()
                        }
                    }

                }
                else -> {
                    Log.d("TAG","Invalid transition type $transitionType")
                    return Result.failure()
                }
            }
            return Result.success()
        }catch (e: Exception){
            return Result.failure()
        }


    }
}