package com.example.weatherapp

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.weatherapp.ui.WeatherViewModel
import com.example.weatherapp.util.NetworkUtils

class MainActivity : AppCompatActivity() {

    private val weatherViewModel: WeatherViewModel by viewModels()
    private lateinit var temperatureText: TextView
    private lateinit var descriptionText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Инициализация TextView для отображения погоды
        temperatureText = findViewById(R.id.temperature_text)
        descriptionText = findViewById(R.id.description_text)

        if (NetworkUtils.isNetworkAvailable(this)) {
            getWeatherData("Moscow")
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getWeatherData(city: String) {
        weatherViewModel.getWeather(city).observe(this, Observer { weather ->
            if (weather != null) {
                temperatureText.text = "${weather.main.temp}°C"
                descriptionText.text = weather.weather.firstOrNull()?.description ?: "Unknown"
            } else {
                Toast.makeText(this, "Failed to load weather data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}