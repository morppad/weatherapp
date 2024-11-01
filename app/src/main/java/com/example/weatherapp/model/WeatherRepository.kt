package com.example.weatherapp.model

import com.example.weatherapp.api.WeatherApi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.weatherapp.util.Constants
import java.util.concurrent.TimeUnit

object WeatherRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(100, TimeUnit.SECONDS)  // Increase connection timeout
        .readTimeout(100, TimeUnit.SECONDS)     // Increase read timeout
        .writeTimeout(100, TimeUnit.SECONDS)    // Increase write timeout
        .build()



    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: WeatherApi = retrofit.create(WeatherApi::class.java)




}
