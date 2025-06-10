package com.example.geomodeapp.ui.views

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun GoogleMapViewUI(currentLatitude: MutableState<Double>, currentLongitude: MutableState<Double>, circleRadius: MutableState<Float>) {

    val cameraPositionState = rememberCameraPositionState{
        position = CameraPosition.fromLatLngZoom(
            LatLng(currentLatitude.value,currentLongitude.value),19f
        )
    }
    val markerState = remember { MarkerState(position = LatLng(currentLatitude.value, currentLongitude.value)) }

    var isMapLoadedForFirstTime by remember { mutableStateOf(true) }

    LaunchedEffect(currentLatitude.value, currentLongitude.value) {
        markerState.position = LatLng(currentLatitude.value, currentLongitude.value)
    }

    LaunchedEffect(currentLatitude.value, currentLongitude.value) {
        var currentZoom = cameraPositionState.position.zoom

        if(!isMapLoadedForFirstTime){
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(
                    LatLng(currentLatitude.value, currentLongitude.value), currentZoom
                ),
                durationMs = 1000
            )
        }else{
            isMapLoadedForFirstTime = false
        }

    }
    val mapProperties = MapProperties(
        isMyLocationEnabled = true,
        isBuildingEnabled = true,
        isIndoorEnabled = true,
    )
    val mapUiSettings = MapUiSettings(
        zoomControlsEnabled = false,
        myLocationButtonEnabled = false
    )

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = mapProperties,
        uiSettings = mapUiSettings,
        onMapClick = { location ->
            currentLatitude.value = location.latitude
            currentLongitude.value = location.longitude
        }
    ){
        Marker(
            state = markerState,
            draggable = true
        )
        Circle(
            center = LatLng(currentLatitude.value, currentLongitude.value),
            radius = circleRadius.value.toDouble(),
            strokeWidth = 1f,
            strokeColor = Color.Green,
            fillColor = Color.Green.copy(0.2f)
        )

    }
}