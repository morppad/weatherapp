package com.example.weatherapp.model

data class WeeklyForecastResponse(
    val daily: List<DailyData>
)

data class DailyData(
    val dt: Long,
    val temp: Temperature,
    val weather: List<Weather>
)

data class Temperature(
    val min: Double,
    val max: Double
)

data class Weather(
    val main: String,
    val description: String
)
