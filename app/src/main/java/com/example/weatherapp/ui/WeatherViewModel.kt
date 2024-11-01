package com.example.weatherapp.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.example.weatherapp.api.WeatherApi
import com.example.weatherapp.model.DailyForecast
import com.example.weatherapp.model.HourlyForecast
import com.example.weatherapp.model.WeatherResponse
import com.example.weatherapp.model.WeeklyForecastResponse
import kotlinx.coroutines.Dispatchers
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.weatherapp.R
import com.example.weatherapp.util.Constants

class WeatherViewModel(private val weatherApi: WeatherApi) : ViewModel() {

    fun getWeather(city: String): LiveData<WeatherResponse?> = liveData(Dispatchers.IO) {
        try {
            Log.d("WeatherApp", "Requesting weather data for city: $city")
            val response = weatherApi.getCurrentWeather(city, apiKey = Constants.API_KEY)
            Log.d("WeatherApp", "Successful response: $response")
            emit(response)
        } catch (e: Exception) {
            Log.e("WeatherApp", "Error fetching weather data: ${e.message}", e)
            emit(null)
        }
    }

    fun getHourlyForecast(lat: Double, lon: Double): LiveData<List<HourlyForecast>> = liveData(Dispatchers.IO) {
        try {
            val response = weatherApi.getHourlyForecast(lat, lon, apiKey = Constants.API_KEY)
            val hourlyForecastList = response.hourly.map { hourlyData ->
                HourlyForecast(
                    hour = formatTime(hourlyData.dt),
                    temp = hourlyData.temp,
                    weatherIcon = getWeatherIcon(hourlyData.weather.firstOrNull()?.main ?: "Clear")
                )
            }
            emit(hourlyForecastList)
        } catch (e: Exception) {
            emit(emptyList()) // Return empty list on error
        }
    }


    fun getWeeklyForecast(lat: Double, lon: Double): LiveData<List<DailyForecast>> = liveData(Dispatchers.IO) {
        try {
            val response = weatherApi.getWeeklyForecast(lat, lon, apiKey = Constants.API_KEY)
            val dailyForecastList = response.daily.map { dailyData ->
                DailyForecast(
                    day = formatTime(dailyData.dt),
                    tempMin = dailyData.temp.min,
                    tempMax = dailyData.temp.max,
                    weatherIcon = getWeatherIcon(dailyData.weather.firstOrNull()?.main ?: "Clear")
                )
            }
            emit(dailyForecastList)
        } catch (e: Exception) {
            emit(emptyList()) // Return empty list on error
        }
    }

    private fun formatTime(unixTime: Long): String {
        val date = Date(unixTime * 1000)
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(date)
    }

    private fun formatDay(unixTime: Long): String {
        val date = Date(unixTime * 1000)
        val sdf = SimpleDateFormat("EEE", Locale.getDefault())
        return sdf.format(date)
    }

    private fun getWeatherIcon(weatherCondition: String): Int {
        return when (weatherCondition) {
            "Clear" -> R.drawable.sun_icon
            "Clouds" -> R.drawable.cloud_icon
            "Rain" -> R.drawable.rain_icon
            else -> R.drawable.default_icon
        }
    }
}
