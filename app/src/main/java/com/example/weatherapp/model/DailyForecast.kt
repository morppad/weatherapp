package com.example.weatherapp.model

data class  DailyForecast(
    val day: String,
    val tempMin: Double,
    val tempMax: Double,
    val weatherIcon: Int
)
