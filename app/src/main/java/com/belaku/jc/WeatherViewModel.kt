package com.belaku.jc

import android.location.Location
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class WeatherViewModel @Inject constructor(private val apiService: WeatherService) : ViewModel() {
    val openWeatherApiKey: String = "9fa8e101240ab18615e3133b051e767e"
    private val _weatherData = mutableStateOf<ApiState<WeatherData>>(ApiState.Loading)
    val weatherData: MutableState<ApiState<WeatherData>> = _weatherData

    fun fetchWeather(loc: Location) {
        viewModelScope.launch {
            try {
                val response = apiService.getWeather(loc.latitude.toString(), loc.longitude.toString(), openWeatherApiKey)
                _weatherData.value = ApiState.Success(response)
            } catch (e: Exception) {
                _weatherData.value = ApiState.Error("Failed to fetch data")
            }
        }
    }
}