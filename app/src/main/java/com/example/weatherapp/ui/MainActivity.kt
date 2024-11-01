package com.example.weatherapp.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.api.WeatherApi
import com.example.weatherapp.ui.adapter.HourlyForecastAdapter
import com.example.weatherapp.ui.adapter.WeeklyForecastAdapter
import com.example.weatherapp.util.NetworkUtils
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var weatherViewModel: WeatherViewModel
    private lateinit var hourlyAdapter: HourlyForecastAdapter
    private lateinit var weeklyAdapter: WeeklyForecastAdapter

    private lateinit var temperatureText: TextView
    private lateinit var descriptionText: TextView
    private lateinit var feelsLikeText: TextView
    private lateinit var humidityText: TextView
    private lateinit var windSpeedText: TextView
    private lateinit var sunriseSunsetText: TextView
    private lateinit var cityInput: EditText
    private lateinit var searchButton: Button
    private lateinit var hourlyForecastRecycler: RecyclerView
    private lateinit var weeklyForecastRecycler: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI components
        temperatureText = findViewById(R.id.temperature_text)
        descriptionText = findViewById(R.id.description_text)
        feelsLikeText = findViewById(R.id.feels_like_text)
        humidityText = findViewById(R.id.humidity_text)
        windSpeedText = findViewById(R.id.wind_speed_text)
        sunriseSunsetText = findViewById(R.id.sunrise_sunset_text)
        cityInput = findViewById(R.id.city_input)
        searchButton = findViewById(R.id.search_button)
        hourlyForecastRecycler = findViewById(R.id.hourly_forecast_recycler)
        weeklyForecastRecycler = findViewById(R.id.weekly_forecast_recycler)

        // Set up RecyclerView for hourly forecast
        hourlyAdapter = HourlyForecastAdapter()
        hourlyForecastRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        hourlyForecastRecycler.adapter = hourlyAdapter

        // Set up RecyclerView for weekly forecast
        weeklyAdapter = WeeklyForecastAdapter()
        weeklyForecastRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        weeklyForecastRecycler.adapter = weeklyAdapter

        // Initialize Retrofit and WeatherApi
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val weatherApi = retrofit.create(WeatherApi::class.java)

        // Initialize ViewModel with factory
        val factory = WeatherViewModelFactory(weatherApi)
        weatherViewModel = ViewModelProvider(this, factory)[WeatherViewModel::class.java]

        // Load default weather data for a specific city
        if (NetworkUtils.isNetworkAvailable(this)) {
            getWeatherData("Moscow") // Default city on load
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
        }

        // Set up search button click listener
        searchButton.setOnClickListener {
            val city = cityInput.text.toString()
            if (city.isNotEmpty()) {
                getWeatherData(city)
            } else {
                Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show()
            }
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

                getHourlyForecast(weather.coord.lat, weather.coord.lon)
                getWeeklyForecast(weather.coord.lat, weather.coord.lon)
            } else {
                Toast.makeText(this, "Failed to load weather data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getHourlyForecast(lat: Double, lon: Double) {
        weatherViewModel.getHourlyForecast(lat, lon).observe(this, Observer { hourlyForecastList ->
            if (hourlyForecastList.isNotEmpty()) {
                hourlyAdapter.submitList(hourlyForecastList)
            } else {
                Toast.makeText(this, "Failed to load hourly forecast", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getWeeklyForecast(lat: Double, lon: Double) {
        weatherViewModel.getWeeklyForecast(lat, lon).observe(this, Observer { weeklyForecastList ->
            if (weeklyForecastList.isNotEmpty()) {
                weeklyAdapter.submitList(weeklyForecastList)
            } else {
                Toast.makeText(this, "Failed to load weekly forecast", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun formatTime(unixTime: Long): String {
        val date = java.util.Date(unixTime * 1000)
        val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        return sdf.format(date)
    }
}
