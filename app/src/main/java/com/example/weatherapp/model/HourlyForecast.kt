package com.example.weatherapp.model

data class HourlyForecast(
    val hour: String,
    val temp: Double,
    val weatherIcon: Int
)
