package com.example.geomodeapp

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.util.Log
import com.example.geomodeapp.model.data.GeoProfiles
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundModeManager @Inject constructor(@ApplicationContext context: Context) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun getCurrentMode(): Int {
        return audioManager.ringerMode
    }

    fun isDoNotDisturbEnabled(): Boolean {
        return notificationManager.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_ALL
    }

    fun setRingerModeSilent() {
        Log.d("TAG", "RingerMode: Silent ")
        audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
    }

    fun setRingerModeVibrate() {
        Log.d("TAG", "RingerMode: Vibrate ")
        audioManager.ringerMode = AudioManager.RINGER_MODE_VIBRATE
    }

    fun setRingerModeNormal() {
        Log.d("TAG", "RingerMode: Normal ")
        audioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
    }

    fun EnableDoNotDisturb() {
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            Log.d("SoundModeManager", "DND permission not granted.")
            return
        }
        Log.d("TAG", "Do Not Disturb: Enabled ")
        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_NONE)
    }

    fun DisableDoNotDisturb() {
        if (!notificationManager.isNotificationPolicyAccessGranted) {
            Log.d("SoundModeManager", "DND permission not granted.")
            return
        }
        Log.d("TAG", "Do Not Disturb: Enabled ")
        notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
    }

    fun mapIntToGeoProfile(mode: Int, isDndEnabled:Boolean = false) : GeoProfiles{
        if(isDndEnabled){
            return GeoProfiles.DND
        }else{
            return when(mode){
                AudioManager.RINGER_MODE_SILENT -> GeoProfiles.SILENT
                AudioManager.RINGER_MODE_VIBRATE -> GeoProfiles.VIBRATE
                AudioManager.RINGER_MODE_NORMAL -> GeoProfiles.NORMAL
                else -> GeoProfiles.NORMAL
            }
        }
    }

}
