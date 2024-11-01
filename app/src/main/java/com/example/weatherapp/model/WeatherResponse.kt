package com.example.weatherapp.model

data class WeatherResponse(
    val coord: Coord,
    val weather: List<Weather>,
    val main: Main,
    val wind: Wind,
    val sys: Sys
)

data class Coord(
    val lat: Double,
    val lon: Double
)


data class Main(
    val temp: Double,
    val feels_like: Double,
    val humidity: Int
)

data class Wind(
    val speed: Double
)

data class Sys(
    val sunrise: Long,
    val sunset: Long
)
