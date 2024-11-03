package com.example.weatherapp.model

import com.example.weatherapp.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Main response for current weather
data class WeatherResponse(
    val coord: Coord,
    val weather: List<Weather>,
    val main: Main,
    val wind: Wind,
    val sys: Sys
)

// Response for hourly forecast data
data class HourlyForecastResponse(
    val list: List<HourlyData>,
    val city: City
)

// Response for weekly forecast data
data class WeeklyForecastResponse(
    val list: List<DailyData>,
    val city: City
)

// Model for individual hourly forecast data
data class HourlyData(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>,
    val clouds: Clouds,
    val wind: Wind,
    val visibility: Int,
    val pop: Double,
    val rain: Rain?
)

// Model for individual daily forecast data
data class DailyData(
    val dt: Long,
    val main: Main,
    val weather: List<Weather>
)

// General data classes
data class Main(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val humidity: Int
)

data class Weather(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

data class Clouds(
    val all: Int
)

data class Wind(
    val speed: Double,
    val deg: Int,
    val gust: Double?
)

data class Rain(
    val `1h`: Double?
)

data class Sys(
    val country: String,
    val sunrise: Long,
    val sunset: Long
)

data class Coord(
    val lat: Double,
    val lon: Double
)

data class City(
    val id: Int,
    val name: String,
    val coord: Coord,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
)

// Data model for simplified hourly forecast used in UI
data class HourlyForecast(
    val hour: String,
    val temp: Double,
    val weatherIcon: Int
)

// Data model for simplified daily forecast used in UI
data class DailyForecast(
    val date: Long,
    val tempMin: Double,
    val tempMax: Double,
    val weatherDescription: String,
    val weatherIcon: String
)

// Utility functions
fun formatTime(unixTime: Long): String {
    val date = Date(unixTime * 1000)
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(date)
}

fun formatDate(unixTime: Long): String {
    val date = Date(unixTime * 1000)
    val sdf = SimpleDateFormat("EEE, MMM d", Locale.getDefault())
    return sdf.format(date)
}

fun getWeatherIcon(weatherCondition: String): Int {
    return when (weatherCondition) {
        "Clear" -> R.drawable.sun_icon
        "Clouds" -> R.drawable.cloud_icon
        "Rain" -> R.drawable.rain_icon
        else -> R.drawable.default_icon
    }
}
