package com.example.weatherapp.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.weatherapp.model.WeatherRepository
import com.example.weatherapp.model.WeatherResponse
import kotlinx.coroutines.Dispatchers

class WeatherViewModel : ViewModel() {

    fun getWeather(city: String) = liveData<WeatherResponse> {  // Убедитесь, что тип данных указан верно
        try {
            val response = WeatherRepository.api.getCurrentWeather(city)
            emit(response)
        } catch (e: Exception) {
            Log.e("WeatherViewModel", "Error fetching weather data", e)
        }
    }
}

