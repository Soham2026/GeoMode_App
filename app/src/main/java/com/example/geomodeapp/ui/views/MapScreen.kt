package com.example.geomodeapp.ui.views

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavHostController
import com.example.geomodeapp.model.data.GeoProfiles
import com.example.geomodeapp.model.room_database.entities.GeoModeProfile
import com.example.geomodeapp.ui.viewmodels.GeoModeViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreenUI(navController: NavHostController, geoModeViewModel: GeoModeViewModel) {

    val context = LocalContext.current
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
    var currentLatitude = remember { mutableStateOf(0.0) }
    var currentLongitude = remember { mutableStateOf(0.0) }
    var circleRadius = remember { mutableStateOf(100f) }
    var shouldShowSaveDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        getCurrentLocation(fusedLocationClient) { location ->
            if (location != null) {
                currentLatitude.value = location.latitude
                currentLongitude.value = location.longitude
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent,

        floatingActionButton = {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                FloatingActionButton(                    // INCREASE THE RADIUS OF THE CIRCLE
                    modifier = Modifier
                        .size(40.dp)
                        .offset(y = -95.dp),
                    containerColor = Color.White,
                    shape = CircleShape,
                    onClick = {
                        if (circleRadius.value + 25 <= 1000) {
                            circleRadius.value += 25
                        } else {
                            circleRadius.value = 1000f
                        }

                    }
                ) {
                    Icon(
                        Icons.Rounded.KeyboardArrowUp,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(40.dp)
                    )
                }

                FloatingActionButton(                   // DECREASE THE RADIUS OF THE CIRCLE
                    modifier = Modifier
                        .size(40.dp)
                        .offset(y = -75.dp),
                    containerColor = Color.White,
                    shape = CircleShape,
                    onClick = {
                        if (circleRadius.value - 25 >= 5) {
                            circleRadius.value -= 25
                        } else {
                            circleRadius.value = 5f
                        }
                    }
                ) {
                    Icon(
                        Icons.Rounded.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(40.dp)
                    )
                }

                FloatingActionButton(                    // TO GET TO THE CURRENT LOCATION
                    modifier = Modifier
                        .size(55.dp)
                        .offset(y = -50.dp),
                    containerColor = Color.White,
                    shape = CircleShape,
                    onClick = {
                        getCurrentLocation(fusedLocationClient) { location ->
                            if (location != null) {
                                currentLatitude.value = location.latitude
                                currentLongitude.value = location.longitude
                            }
                        }
                    }
                ) {
                    Icon(
                        Icons.Rounded.LocationOn,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(45.dp)
                    )
                }

                FloatingActionButton(                    // TO SAVE THE LOCATION DETAILS AND REGISTER A GEOFENCE
                    modifier = Modifier
                        .size(55.dp)
                        .offset(y = -30.dp),
                    containerColor = Color.White,
                    shape = CircleShape,
                    onClick = {
                        shouldShowSaveDialog = true
                    }
                ) {
                    Icon(
                        Icons.Rounded.Done,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(45.dp)
                    )
                }
            }

        },
        content = {
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                GoogleMapViewUI(currentLatitude, currentLongitude, circleRadius)
                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.98f)
                        .padding(top = 40.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .size(40.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.White
                        ),
                        onClick = {
                            navController.popBackStack()
                        }
                    ) {
                        Icon(
                            Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(45.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(0.85f))

                    TextField(
                        modifier = Modifier
                            .offset(x = (-15).dp)
                            .shadow(10.dp, RoundedCornerShape(30.dp)),
                        value = "",
                        onValueChange = {},
                        leadingIcon = {
                            Icon(
                                Icons.Rounded.Search,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        },
                        placeholder = {
                            Text(
                                text = "Search",
                                color = Color.LightGray,
                                fontFamily = FontFamily.Serif
                            )
                        },
                        shape = RoundedCornerShape(30.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.Black
                        )
                    )

                }


            }
        }
    )

    if (shouldShowSaveDialog == true) {
        SavingDialog(
            context,
            latitude = currentLatitude.value,
            longitude = currentLongitude.value,
            radius = circleRadius.value,
            onCancelPressed = {
                shouldShowSaveDialog = false
            },
            onSavePressed = { zoneName , desiredMode ->
                val markedGeoMode = GeoModeProfile(
                    locationName = zoneName,
                    latitude = currentLatitude.value,
                    longitude = currentLongitude.value,
                    radius = circleRadius.value,
                    isLocationEnabled = true,
                    desiredMode = desiredMode
                )
                shouldShowSaveDialog = false
                geoModeViewModel.addNewGeoModeLocation(markedGeoMode)   // SAVING THE GEOFENCE LOCATION
                navController.popBackStack()
            }
        )
    }
}

@SuppressLint("MissingPermission")
fun getCurrentLocation(
    fusedLocationClient: FusedLocationProviderClient,
    onLocationFetched: (location: Location?) -> Unit
) {
    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
        onLocationFetched(location)
    }
}


@Composable
fun SavingDialog(
    context: Context,
    latitude: Double = 0.0,
    longitude: Double = 0.0,
    radius: Float = 0f,
    onCancelPressed: () -> Unit = {},
    onSavePressed: (locationName:String,desiredMode:GeoProfiles) -> Unit
) {

    val pagesCount = 2;
    val pagerState = rememberPagerState(initialPage = 0, pageCount = { pagesCount })
    val coroutineScope = rememberCoroutineScope()
    var zoneName by remember { mutableStateOf("") }
    val deviceModeList = listOf(GeoProfiles.SILENT,GeoProfiles.VIBRATE,GeoProfiles.DND,GeoProfiles.NORMAL)
    var selectedChoice by remember { mutableStateOf(-1) }
    val map = mapOf(
        GeoProfiles.SILENT to "SILENT",
        GeoProfiles.VIBRATE to "VIBRATE",
        GeoProfiles.DND to "DO NOT DISTURB",
        GeoProfiles.NORMAL to "NORMAL"
    )

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
                .fillMaxWidth(0.95f)
                .height(450.dp)
                .padding(top = 9.dp, start = 11.dp, end = 13.dp, bottom = 10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
            ) { page ->

                when (page) {
                    0 -> {
                        Column(
                            modifier = Modifier
                                .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Set Geofence",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif,
                                color = Color.White,
                            )
                            Spacer(Modifier.height(5.dp))
                            Text(
                                text = "Label :",
                                fontSize = 20.sp,
                                fontFamily = FontFamily.Serif,
                                color = Color.White,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 10.dp),
                                textAlign = TextAlign.Start
                            )
                            Spacer(Modifier.height(5.dp))
                            TextField(
                                modifier = Modifier
                                    .shadow(10.dp, RoundedCornerShape(23.dp)),
                                value = zoneName,
                                onValueChange = {
                                    zoneName = it
                                },
                                placeholder = {
                                    Text(
                                        text = "Assign a label to this zone",
                                        color = Color.LightGray,
                                        fontSize = 17.sp,
                                        fontFamily = FontFamily.Serif,
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                },
                                textStyle = TextStyle(
                                    fontSize = 17.sp,
                                    fontFamily = FontFamily.Serif,
                                    color = Color.Black

                                ),
                                shape = RoundedCornerShape(15.dp),
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.White,
                                    unfocusedContainerColor = Color.White,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    cursorColor = Color.Black
                                )
                            )

                            Spacer(Modifier.height(7.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Latitude :",
                                    fontSize = 20.sp,
                                    fontFamily = FontFamily.Serif,
                                    color = Color.White,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 10.dp)
                                        .weight(0.3f),
                                    textAlign = TextAlign.Start
                                )
                                TextField(
                                    modifier = Modifier
                                        .shadow(10.dp, RoundedCornerShape(23.dp))
                                        .weight(0.6f),
                                    value = "$latitude",
                                    onValueChange = {},
                                    textStyle = TextStyle(
                                        fontSize = 17.sp,
                                        fontFamily = FontFamily.Serif,
                                        color = Color.Black

                                    ),
                                    shape = RoundedCornerShape(15.dp),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        cursorColor = Color.White
                                    ),
                                    readOnly = true
                                )
                            }

                            Spacer(Modifier.height(7.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Longitude :",
                                    fontSize = 19.sp,
                                    fontFamily = FontFamily.Serif,
                                    color = Color.White,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 10.dp)
                                        .weight(0.3f),
                                    textAlign = TextAlign.Start
                                )
                                TextField(
                                    modifier = Modifier
                                        .shadow(10.dp, RoundedCornerShape(23.dp))
                                        .weight(0.6f),
                                    value = "$longitude",
                                    onValueChange = {},
                                    textStyle = TextStyle(
                                        fontSize = 17.sp,
                                        fontFamily = FontFamily.Serif,
                                        color = Color.Black

                                    ),
                                    shape = RoundedCornerShape(15.dp),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        cursorColor = Color.White
                                    ),
                                    readOnly = true
                                )
                            }

                            Spacer(Modifier.height(7.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Radius :",
                                    fontSize = 20.sp,
                                    fontFamily = FontFamily.Serif,
                                    color = Color.White,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 10.dp)
                                        .weight(0.3f),
                                    textAlign = TextAlign.Start
                                )
                                TextField(
                                    modifier = Modifier
                                        .shadow(10.dp, RoundedCornerShape(15.dp))
                                        .weight(0.6f),
                                    value = "$radius",
                                    onValueChange = {},
                                    textStyle = TextStyle(
                                        fontSize = 17.sp,
                                        fontFamily = FontFamily.Serif,
                                        color = Color.Black

                                    ),
                                    shape = RoundedCornerShape(23.dp),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.White,
                                        unfocusedContainerColor = Color.White,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        cursorColor = Color.White
                                    ),
                                    readOnly = true
                                )
                            }
                            Spacer(Modifier.height(12.dp))

                        }
                    }
                    1 ->{
                        Column( modifier = Modifier
                            .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ){
                            Text(
                                text = "Set device mode for the Geolocation",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif,
                                softWrap = true,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(15.dp))
                            
                            deviceModeList.forEachIndexed { index, geoProfiles ->
                                Row (
                                    modifier = Modifier.fillMaxWidth().padding(start = 10.dp),
                                    horizontalArrangement = Arrangement.Start,
                                    verticalAlignment = Alignment.CenterVertically
                                ){
                                    RadioButton(
                                        selected = selectedChoice == index,
                                        onClick = {
                                            selectedChoice = index
                                        },
                                        colors = RadioButtonDefaults.colors(
                                            selectedColor = Color(0xFFD08A15)
                                        )
                                    )
                                    Text(
                                        text ="${map[deviceModeList[index]]}",
                                        fontSize = 20.sp,
                                        fontFamily = FontFamily.Serif,
                                        color = Color.White
                                    )

                                }
                            }

                        }
                    }
                }

            }

            Row(
                modifier = Modifier.padding(top = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pagesCount) {
                    val color = if (pagerState.currentPage == it) Color.White else Color.Black
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(13.dp)
                            .clip(shape = CircleShape)
                            .border(0.9.dp, Color.White, CircleShape)
                            .background(color, CircleShape)
                    )
                }
            }
            Spacer(Modifier.height(9.dp))
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
                        if(pagerState.currentPage == 0){
                            onCancelPressed()
                        }else{
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(0)
                            }
                        }

                    }
                ) {
                    Text(
                        text = if(pagerState.currentPage == 0) "Cancel" else "Back",
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
                        if(pagerState.currentPage == 0){
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(1)
                            }
                        }else{
                            if(zoneName != ""){
                                if(selectedChoice != -1){
                                    onSavePressed(zoneName,deviceModeList[selectedChoice])
                                }else{
                                    Toast.makeText(context,"Please select a device mode",Toast.LENGTH_SHORT).show()
                                }

                            }else{
                                Toast.makeText(context,"Please enter a name",Toast.LENGTH_SHORT).show()
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(0)
                                }
                            }
                        }
                    }
                ) {
                    Text(
                        text = if(pagerState.currentPage == 0) "Next" else "Save",
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