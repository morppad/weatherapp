package com.example.weatherapp.model

import com.example.weatherapp.api.WeatherApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.weatherapp.util.Constants
import java.util.concurrent.TimeUnit

object WeatherRepository {

    val client = OkHttpClient.Builder()
        .connectTimeout(100, TimeUnit.SECONDS) // increase connection timeout
        .readTimeout(100, TimeUnit.SECONDS)    // increase read timeout
        .writeTimeout(100, TimeUnit.SECONDS)   // increase write timeout
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/data/2.5/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: WeatherApi = retrofit.create(WeatherApi::class.java)





}
