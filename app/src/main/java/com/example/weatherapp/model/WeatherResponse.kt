package com.example.weatherapp.model

data class WeatherResponse(
    val main: Main,
    val weather: List<Weather>,
    val wind: Wind,
    val sys: Sys
)

data class Main(
    val temp: Double,
    val feels_like: Double,
    val humidity: Int
)

data class Weather(
    val main: String,
    val description: String
)

data class Wind(
    val speed: Double,
    val deg: Int
)

data class Sys(
    val sunrise: Long,
    val sunset: Long
)
