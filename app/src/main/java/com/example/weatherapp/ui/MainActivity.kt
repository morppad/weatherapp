package com.example.weatherapp.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.R
import com.example.weatherapp.api.WeatherApi
import com.example.weatherapp.util.Constants
import com.example.weatherapp.util.NetworkUtils
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var temperatureText: TextView
    private lateinit var descriptionText: TextView
    private lateinit var feelsLikeText: TextView
    private lateinit var humidityText: TextView
    private lateinit var windSpeedText: TextView
    private lateinit var sunriseSunsetText: TextView
    private lateinit var cityInput: EditText
    private lateinit var searchButton: Button
    private lateinit var searchLayout: LinearLayout
    private lateinit var cityInfoLayout: LinearLayout
    private lateinit var cityNameText: TextView
    private lateinit var changeCityButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Retrofit and WeatherApi
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val weatherApi = retrofit.create(WeatherApi::class.java)

        // Initialize ViewModel
        val factory = WeatherViewModelFactory(weatherApi)
        weatherViewModel = ViewModelProvider(this, factory)[WeatherViewModel::class.java]

        // Initialize UI components
        temperatureText = findViewById(R.id.temperature_text)
        descriptionText = findViewById(R.id.description_text)
        feelsLikeText = findViewById(R.id.feels_like_text)
        humidityText = findViewById(R.id.humidity_text)
        windSpeedText = findViewById(R.id.wind_speed_text)
        sunriseSunsetText = findViewById(R.id.sunrise_sunset_text)
        cityInput = findViewById(R.id.city_input)
        searchButton = findViewById(R.id.search_button)
        searchLayout = findViewById(R.id.search_layout)
        cityInfoLayout = findViewById(R.id.city_info_layout)
        cityNameText = findViewById(R.id.city_name_text)
        changeCityButton = findViewById(R.id.change_city_button)

        // Load initial weather data
        if (NetworkUtils.isNetworkAvailable(this)) {
            getWeatherData("Moscow")
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
        }

        // Search button functionality
        searchButton.setOnClickListener {
            val city = cityInput.text.toString()
            if (city.isNotEmpty()) {
                getWeatherData(city)
                searchLayout.visibility = View.GONE
                cityInfoLayout.visibility = View.VISIBLE
                cityNameText.text = city
            } else {
                Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show()
            }
        }

        // Change city button functionality
        changeCityButton.setOnClickListener {
            searchLayout.visibility = View.VISIBLE
            cityInfoLayout.visibility = View.GONE
        }
    }

    private fun getWeatherData(city: String) {
        weatherViewModel.getWeather(city).observe(this, Observer { weather ->
            if (weather != null) {
                temperatureText.text = "${weather.main.temp}°C"
                descriptionText.text = weather.weather.firstOrNull()?.description ?: "Unknown"
                feelsLikeText.text = "Feels like ${weather.main.feels_like}°C"
                humidityText.text = "Humidity: ${weather.main.humidity}%"
                windSpeedText.text = "Wind speed: ${weather.wind.speed} m/s"
                sunriseSunsetText.text = "Sunrise: ${formatTime(weather.sys.sunrise)} Sunset: ${formatTime(weather.sys.sunset)}"
            } else {
                Toast.makeText(this, "Failed to load weather data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun formatTime(unixTime: Long): String {
        val date = java.util.Date(unixTime * 1000)
        val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        return sdf.format(date)
    }
}
