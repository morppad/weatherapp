package com.example.weatherapp.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.api.WeatherApi
import com.example.weatherapp.model.*
import com.example.weatherapp.util.Constants
import com.example.weatherapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

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

    private val _hourlyForecast = MutableLiveData<List<HourlyForecast>>()
    val hourlyForecast: LiveData<List<HourlyForecast>> get() = _hourlyForecast

//    fun getHourlyForecast(lat: Double, lon: Double) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val response = weatherApi.getHourlyForecast(lat, lon, apiKey = Constants.API_KEY)
//
//                val hourlyForecastList = response.list.map { hourlyData ->
//                    HourlyForecast(
//                        hour = formatTime(hourlyData.dt),
//                        temp = hourlyData.main.temp,
//                        weatherIcon = getWeatherIcon(hourlyData.weather.firstOrNull()?.main ?: "Clear")
//                    )
//                }
//
//                // Обновляем данные в _hourlyForecast
//                _hourlyForecast.postValue(hourlyForecastList)
//            } catch (e: Exception) {
//                Log.e("WeatherApp", "Error fetching hourly forecast: ${e.message}", e)
//                _hourlyForecast.postValue(emptyList())
//            }
//        }
//    }
    // В WeatherViewModel
    fun getHourlyForecast(lat: Double, lon: Double): LiveData<List<HourlyForecast>> = liveData(Dispatchers.IO) {
        try {
            val response = weatherApi.getHourlyForecast(lat, lon, apiKey = Constants.API_KEY)
            val hourlyForecastList = response.list.map { hourlyData ->
                HourlyForecast(
                    hour = formatTime(hourlyData.dt),
                    temp = hourlyData.main.temp,
                    weatherIcon = getWeatherIcon(hourlyData.weather.firstOrNull()?.main ?: "Clear")
                )
            }
            emit(hourlyForecastList)
        } catch (e: Exception) {
            Log.e("WeatherApp", "Error fetching hourly forecast: ${e.message}", e)
            emit(emptyList())
        }
    }


    fun getDailyForecast(lat: Double, lon: Double): LiveData<List<DailyForecast>> = liveData(Dispatchers.IO) {
        try {
            val response = weatherApi.getWeeklyForecast(lat, lon, apiKey = Constants.API_KEY)
            val dailyForecastList = response.list.map { dailyData ->
                DailyForecast(
                    date = dailyData.dt,
                    tempMin = dailyData.main.temp_min,
                    tempMax = dailyData.main.temp_max,
                    weatherDescription = dailyData.weather.firstOrNull()?.description ?: "Clear",
                    weatherIcon = dailyData.weather.firstOrNull()?.icon ?: "01d"
                )
            }
            emit(dailyForecastList)
        } catch (e: Exception) {
            Log.e("WeatherApp", "Error fetching daily forecast: ${e.message}", e)
            emit(emptyList())
        }
    }

    private fun formatTime(unixTime: Long): String {
        val date = Date(unixTime * 1000)
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
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
