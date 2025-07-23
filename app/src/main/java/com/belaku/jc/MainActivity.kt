package com.belaku.jc

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import com.belaku.jc.ui.theme.JCTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale


lateinit var viewModel: WeatherViewModel
lateinit var currentLoc: Location
lateinit var appContx: Context
private val cardTraffic = MutableLiveData<Boolean>()
private val locAddr = MutableLiveData<String>()
private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        appContx = applicationContext
//Initialize it where you need it
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        cardTraffic.postValue(true)
        setContent {
            JCTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ScreenSelection()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenSelection() {
    val contx = LocalContext.current

    Scaffold(
        topBar = { TopAppBar(title = { Text("Smart City") }) },

        bottomBar = {
            BottomAppBar {
                Column {
                    Row(
                        modifier = Modifier
                            //   .padding(paddingValues)
                            .fillMaxSize(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Card(modifier = Modifier
                            .weight(1f)
                            .clickable {
                                cardTraffic.postValue(true)
                                makeToast("Traffic")
                            }) {
                            Text("Traffic", fontSize = 35.sp)
                        }

                        Card(modifier = Modifier
                            .weight(1f)
                            .clickable {
                                cardTraffic.postValue(false)
                                makeToast("Weather")
                            }) {
                            Text("Weather", fontSize = 35.sp)
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    /* Handle click action */

                    //   getWeatherData()


                },
                // Additional configurations
            ) {
                // FloatingActionButton content
            }
        },
        //  floatingActionButton = { FloatingActionButton(onClick = { /* ... */ }) { /* ... */ } },
        content = { paddingValues -> // paddingValues are automatically provided by Scaffold


            val cardTv: Boolean? by cardTraffic.observeAsState()
            val textLocationAddrs: String? by locAddr.observeAsState()

            if (cardTv == true) {
                Column(modifier = Modifier.padding(paddingValues), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center ) {
                    locAddr.observeAsState()
                    getLastUserLocation()
                    textLocationAddrs?.let { Text(it) }
                }

            } else {
                Column(modifier = Modifier.padding(paddingValues)) {
                    Text("Yet2Display WEATHER")
                }
            }


            //      ContentComposable(contx, cardTraffic)


        }
    )
}

fun getWeatherData(loc: Location) {
  //  viewModel.fetchWeather(loc)
    val _weatherData = mutableStateOf<ApiState<WeatherData>>(ApiState.Loading)
    val weatherData: MutableState<ApiState<WeatherData>> = _weatherData

    makeToast(weatherData.value.toString())
}


@OptIn(ExperimentalPermissionsApi::class)
@Composable
@SuppressLint("MissingPermission")
private fun getLastUserLocation() {
    // Check if location permissions are granted
    if (areLocationPermissionsGranted()) {
        // Retrieve the last known location
       getLocation()
    } else {
        RequestPermission(permission = android.Manifest.permission.ACCESS_FINE_LOCATION)
    }
}

@SuppressLint("MissingPermission")
fun getLocation() {

    fusedLocationProviderClient.lastLocation
        .addOnSuccessListener { location ->
            location?.let {
                // If location is not null, invoke the success callback with latitude and longitude
                //   onGetLastLocationSuccess(Pair(it.latitude, it.longitude))
                processLocation(it)
            }
        }
        .addOnFailureListener { exception ->
            // If an error occurs, invoke the failure callback with the exception
            //  onGetLastLocationFailed(exception)
            makeToast("onGetLastLocationFailed - " +  exception.toString())
        }
}

@SuppressLint("PermissionLaunchedDuringComposition")
@ExperimentalPermissionsApi
@Composable
fun RequestPermission(
    permission: String,
    rationaleMessage: String = "To use this app's functionalities, you need to give us the permission.",
) {
    val permissionState = rememberPermissionState(permission)

    HandleRequest(
        permissionState = permissionState,
        deniedContent = { shouldShowRationale ->
            PermissionDeniedContent(
                rationaleMessage = rationaleMessage,
                shouldShowRationale = shouldShowRationale
            ) { permissionState.launchPermissionRequest() }
        },
        content = {
            /*   Content(
                   text = "PERMISSION GRANTED!",
                   showButton = false
               ) {}*/
        }
    )
}

@Composable
fun PermissionDeniedContent(
    rationaleMessage: String,
    shouldShowRationale: Boolean,
    content: @Composable () -> Unit
) {
    makeToast("Permit Yaar :)")
}

@ExperimentalPermissionsApi
@Composable
fun HandleRequest(
    permissionState: PermissionState,
    deniedContent: @Composable (Boolean) -> Unit,
    content: @Composable () -> Unit
) {
    when (permissionState.status) {
        is PermissionStatus.Granted -> {
            getLocation()
        }
        is PermissionStatus.Denied -> {
            deniedContent((permissionState.status as PermissionStatus.Denied).shouldShowRationale)
        }
    }
}

fun processLocation(it: Location) {

    currentLoc = it

    getWeatherData(currentLoc)

    val geocoder = Geocoder(appContx, Locale.getDefault())

    val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
    val address = addresses!![0].getAddressLine(0)
    val city = addresses!![0].locality
    val state = addresses!![0].adminArea
    val zip = addresses!![0].postalCode
    val country = addresses!![0].countryName

    makeToast(address.toString())
    locAddr.postValue(address.toString())

}

private fun areLocationPermissionsGranted(): Boolean {
    return (ActivityCompat.checkSelfPermission(
        appContx, android.Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                appContx, android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED)
}

@Composable
fun RequestLocationPermissionUsingRememberLauncherForActivityResult(
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit
) {
    // 1. Create a stateful launcher using rememberLauncherForActivityResult
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        // 2. Check if all requested permissions are granted
        val arePermissionsGranted = permissionsMap.values.reduce { acc, next ->
            acc && next
        }

        // 3. Invoke the appropriate callback based on the permission result
        if (arePermissionsGranted) {
            onPermissionGranted.invoke()
        } else {
            onPermissionDenied.invoke()
        }
    }

    // 4. Launch the permission request on composition
    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }
}


fun makeToast(s: String) {
    Toast.makeText(appContx, s, Toast.LENGTH_SHORT).show()
}




