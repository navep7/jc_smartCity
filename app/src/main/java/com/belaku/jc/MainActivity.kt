package com.belaku.jc

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.belaku.jc.ui.theme.JCTheme

private val cardTraffic = MutableLiveData<Boolean>()

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        cardTraffic.postValue(false)
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
                                makeToast(contx, "Traffic")
                            }) {
                            Text("Traffic", fontSize = 35.sp)
                        }

                        Card(modifier = Modifier
                            .weight(1f)
                            .clickable {
                                cardTraffic.postValue(false)
                                makeToast(contx, "Weather")
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

                    getWeatherData()


                },
                // Additional configurations
            ) {
                // FloatingActionButton content
            }
        },
        //  floatingActionButton = { FloatingActionButton(onClick = { /* ... */ }) { /* ... */ } },
        content = { paddingValues -> // paddingValues are automatically provided by Scaffold







            val cardTv: Boolean? by cardTraffic.observeAsState()

            if (cardTv == true) {
                makeToast(contx, "show Traffic")
                Column(modifier = Modifier.padding(paddingValues)) {
                    Text("Yet2Display TRAFFIC")
                }

            } else {
                makeToast(contx, "show Weather")
                Column(modifier = Modifier.padding(paddingValues)) {
                    Text("Yet2Display WEATHER")
                }
            }



      //      ContentComposable(contx, cardTraffic)


        }
    )
}

@Composable
fun ContentComposable(contx: Context, cardT: LiveData<Boolean>) {

    val cardTv: Boolean? by cardT.observeAsState()


    if (cardTv == true)
        MapComposable(contx)
    else WeatherComposable(contx)

}

@Composable
fun WeatherComposable(c: Context) {
    makeToast(c, "WeatherComposable")

    val state = remember {
        MutableTransitionState(true).apply {
            // Start the animation immediately.
            targetState = true
        }
    }

    AnimatedVisibility(visibleState = state) {
        Text(text = "Hello, world!")
    }



}

@Composable
fun MapComposable(c: Context) {
    makeToast(c, "MapComposable")


}

fun makeToast(c: Context, s: String) {
    Toast.makeText(c, s, Toast.LENGTH_SHORT).show()
}

@Composable
fun ContentB() {
    TODO("Not yet implemented")
}

@Composable
fun MakeToast(c: Context, s: String) {
    Toast.makeText(c, s, Toast.LENGTH_LONG).show()
}



