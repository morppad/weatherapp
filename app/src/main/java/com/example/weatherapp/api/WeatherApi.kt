package com.example.weatherapp.api

import com.example.weatherapp.model.HourlyForecastResponse
import com.example.weatherapp.model.WeatherResponse
import com.example.weatherapp.model.WeeklyForecastResponse
import com.example.weatherapp.util.Constants
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("weather")
    suspend fun getCurrentWeather(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse

    @GET("onecall")
    suspend fun getHourlyForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("exclude") exclude: String = "current,daily,minutely",
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String
    ): HourlyForecastResponse

    @GET("onecall")
    suspend fun getWeeklyForecast(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("exclude") exclude: String = "current,minutely,hourly",
        @Query("units") units: String = "metric",
        @Query("appid") apiKey: String
    ): WeeklyForecastResponse
}
