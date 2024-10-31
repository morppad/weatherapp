package com.example.weatherapp.model

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.weatherapp.util.Constants
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

object WeatherRepository {

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)  // Тайм-аут на подключение
        .readTimeout(30, TimeUnit.SECONDS)     // Тайм-аут на чтение
        .writeTimeout(30, TimeUnit.SECONDS)    // Тайм-аут на запись
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: WeatherApi = retrofit.create(WeatherApi::class.java)

    interface WeatherApi {
        @GET("weather")
        suspend fun getCurrentWeather(
            @Query("q") city: String,
            @Query("units") units: String = "metric",
            @Query("appid") apiKey: String = Constants.API_KEY
        ): WeatherResponse
    }

}
