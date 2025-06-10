package com.example.geomodeapp.ui.views

import android.Manifest
import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavHostController
import com.example.geomodeapp.MapScreenRoute
import com.example.geomodeapp.R
import com.example.geomodeapp.model.room_database.entities.GeoModeProfile
import com.example.geomodeapp.ui.viewmodels.GeoModeViewModel
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenUI(
    navController: NavHostController,
    geoModeViewModel: GeoModeViewModel
) {

    val context = LocalContext.current
    var isDeleteDialogOpen = remember { mutableStateOf(false) }
    var longPressedCard = remember { mutableStateOf("") }
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    var geoFenceList = geoModeViewModel.geoModesList.collectAsState()


    LaunchedEffect(geoFenceList.value.size){
        if(geoFenceList.value.isNotEmpty()){
            delay(
                timeMillis = 900
            )
            checkForBatteryOptimizer(context)
        }
    }


    var checkAllPermissions: () -> Unit by remember { mutableStateOf({}) }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                checkAllPermissions()
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(
                        context as Activity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    Toast.makeText(
                        context,
                        "Please grant location permissions from settings",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }
    )

    val backgroundLocationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                checkAllPermissions()
            }
        }
    )

    checkAllPermissions = checkAllPermissions@{                       // MANAGING PERMISSIONS

        val isLocationGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val isBackgroundLocationGranted =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            else true

        if (!isLocationGranted) {
            locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return@checkAllPermissions
        }

        if (!isBackgroundLocationGranted && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            backgroundLocationPermissionLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            return@checkAllPermissions
        }


        if (!notificationManager.isNotificationPolicyAccessGranted) {
            Toast.makeText(context, "Please grant notification permission", Toast.LENGTH_SHORT)
                .show()
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            context.startActivity(intent)
        } else {
            navController.navigate(MapScreenRoute)
        }

    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Image(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 100.dp),
            painter = painterResource(R.drawable.maps_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(0.7f))
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    TopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.White
                        ), modifier = Modifier.clip(
                            RoundedCornerShape(
                                bottomStart = 45.dp, bottomEnd = 45.dp
                            )
                        ), title = {
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Geo Mode ",
                                textAlign = TextAlign.Center,
                                color = Color.Black,
                                fontFamily = FontFamily.Serif,
                                fontSize = 37.sp
                            )
                        })
                },
                floatingActionButton = {
                    FloatingActionButton(
                        modifier = Modifier
                            .size(65.dp)
                            .offset(y = -15.dp),
                        containerColor = Color.White,
                        shape = RoundedCornerShape(26.dp),
                        onClick = {
                            checkAllPermissions()
                        }
                    ) {
                        Icon(
                            Icons.Rounded.Add,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(45.dp)
                        )
                    }
                },
                content = {
                    LazyColumn(
                        modifier = Modifier
                            .padding(it)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (geoFenceList.value.size == 0) {
                            item {
                                Spacer(Modifier.height(40.dp))
                                Text(
                                    text = " No geofences yet! \uD83D\uDCCD \n Tap the '+' to add your first one.",
                                    textAlign = TextAlign.Center,
                                    color = Color.Gray,
                                    fontFamily = FontFamily.Serif,
                                    fontSize = 17.sp
                                )
                            }
                        } else {
                            //checkForBatteryOptimizer(context)
                            item {
                                Spacer(Modifier.height(15.dp))
                            }
                            items(
                                count = geoFenceList.value.size,
                                itemContent = { index ->
                                    CardView(
                                        geoFenceList.value[index],
                                        onSwitchToggled = { isChecked ->
                                            geoModeViewModel.enableGeoModeForLocation(
                                                geoFenceList.value[index].locationName,
                                                isChecked
                                            )
                                        },
                                        onLongPressed = { locationName ->
                                            longPressedCard.value = locationName
                                            isDeleteDialogOpen.value = true
                                        }
                                    )
                                },
                                key = { geoFenceList.value[it].locationName }
                            )
                            item {
                                Spacer(Modifier.height(45.dp))
                            }
                        }

                    }
                }
            )
        }
    }

    if(isDeleteDialogOpen.value){
        DeleteDialog(
            longPressedCard.value,
            onCancelPressed = {
                isDeleteDialogOpen.value = false
            },
            onDeletePressed = {
                geoModeViewModel.deleteGeoModeLocation(longPressedCard.value)
                isDeleteDialogOpen.value = false
            }
        )
    }
}

@Composable
fun CardView(
    geoFenceDetails: GeoModeProfile,
    onSwitchToggled: (Boolean) -> Unit,
    onLongPressed: (locationName: String) -> Unit
) {
    var isChecked = remember { mutableStateOf(geoFenceDetails.isLocationEnabled) }
    Card(
        modifier = Modifier
            .fillMaxWidth(0.95f)
            .height(115.dp)
            .padding(top = 7.dp, bottom = 10.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    onLongPressed(geoFenceDetails.locationName)
                }
            ),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colorStops = arrayOf(
                            0.0f to Color(0xFF030303).copy(0.95f),
                            0.4f to Color(0xFF030303).copy(0.85f),
                            1.0f to Color(0xFF573313).copy(0.9f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(1300f, 0f)
                    )
                )
                .border(0.3.dp, Color.White, RoundedCornerShape(28.dp))

        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.8f)
                    .padding(start = 30.dp, top = 8.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = geoFenceDetails.locationName,
                    textAlign = TextAlign.Center,
                    color = Color.White,
                    fontFamily = FontFamily.Serif,
                    fontSize = 20.sp,
                    softWrap = true
                )
                Spacer(Modifier.height(7.dp))
                Text(
                    text = "Mode: ${geoFenceDetails.desiredMode.toString()}",
                    textAlign = TextAlign.Center,
                    color = Color.LightGray,
                    fontFamily = FontFamily.Serif,
                    fontSize = 15.sp
                )
            }

            Switch(
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxSize(),
                checked = isChecked.value,
                onCheckedChange = {
                    isChecked.value = it
                    onSwitchToggled(it)
                },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFFC26419),
                    checkedTrackColor = Color.White,
                    uncheckedThumbColor = Color.DarkGray,
                    uncheckedTrackColor = Color.White
                )
            )

        }

    }
}

@Composable
fun DeleteDialog(locationName: String, onCancelPressed: () -> Unit, onDeletePressed:() -> Unit) {
    Dialog(
        onDismissRequest = {
            onCancelPressed()
        },
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {

        Column(
            modifier = Modifier
                .background(
                    brush = Brush.linearGradient(
                        colorStops = arrayOf(
                            0.0f to Color(0xFF030303).copy(0.9f),
                            0.4f to Color(0xFF030303).copy(0.85f),
                            1.0f to Color(0xFF573313).copy(0.9f)
                        ),
                        start = Offset(0f, 0f),
                        end = Offset(1300f, 0f)
                    ),
                    RoundedCornerShape(20.dp)
                )
                .border(0.3.dp, Color.White, RoundedCornerShape(20.dp))
                .fillMaxWidth(0.9f)
                .wrapContentHeight()
                .padding(top = 13.dp, start = 11.dp, end = 13.dp, bottom = 10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Delete geofence: $locationName?",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 3.dp, end = 3.dp),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(13.dp))
            Text(
                text = "Are you sure you want to delete this geofence ?\nThis action cannot be undone.",
                fontSize = 15.sp,
                fontWeight = FontWeight.Light,
                fontFamily = FontFamily.Serif,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 3.dp, end = 3.dp),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .padding(3.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    modifier = Modifier.weight(0.4f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xF0DE6309).copy(0.8f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    onClick = {
                        onCancelPressed()
                    }
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Serif,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(Modifier.weight(0.3f))
                Button(
                    modifier = Modifier.weight(0.4f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xF0DE6309).copy(0.8f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    onClick = {
                        onDeletePressed()
                    }
                ) {
                    Text(
                        text = "Delete",
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Serif,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

        }
    }
}


fun checkForBatteryOptimizer(context: Context){

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val packageName = context.packageName
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

        val isIgnoringOptimization = powerManager.isIgnoringBatteryOptimizations(packageName)

        if (!isIgnoringOptimization) {
            Toast.makeText(context, "Please 'Don't optimize' battery for this app.", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
            context.startActivity(intent)
        }
    }
}

