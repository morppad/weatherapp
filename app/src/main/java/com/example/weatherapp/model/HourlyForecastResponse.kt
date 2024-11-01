package com.example.weatherapp.model

data class HourlyForecastResponse(
    val hourly: List<HourlyData>
)

data class HourlyData(
    val dt: Long,
    val temp: Double,
    val weather: List<Weather>
)
