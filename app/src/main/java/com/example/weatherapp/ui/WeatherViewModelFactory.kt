package com.example.weatherapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.api.WeatherApi

class WeatherViewModelFactory(private val weatherApi: WeatherApi) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            return WeatherViewModel(weatherApi) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

