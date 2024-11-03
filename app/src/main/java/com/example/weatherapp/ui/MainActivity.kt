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

    private lateinit var hourlyForecastRecycler: RecyclerView
    private lateinit var weeklyForecastRecycler: RecyclerView
    private lateinit var hourlyAdapter: HourlyForecastAdapter
    private lateinit var weeklyAdapter: WeeklyForecastAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val weatherApi = retrofit.create(WeatherApi::class.java)

        val factory = WeatherViewModelFactory(weatherApi)
        weatherViewModel = ViewModelProvider(this, factory)[WeatherViewModel::class.java]

        // Initialize UI elements
        temperatureText = findViewById(R.id.temperature_text)
        descriptionText = findViewById(R.id.description_text)
        feelsLikeText = findViewById(R.id.feels_like_text)
        humidityText = findViewById(R.id.humidity_text)
        windSpeedText = findViewById(R.id.wind_speed_text)
        sunriseSunsetText = findViewById(R.id.sunrise_sunset_text)
        cityInput = findViewById(R.id.city_input)
        searchButton = findViewById(R.id.search_button)

        // Initialize RecyclerViews and adapters for forecasts
        hourlyForecastRecycler = findViewById(R.id.hourly_forecast_recycler)
        weeklyForecastRecycler = findViewById(R.id.weekly_forecast_recycler)

        hourlyAdapter = HourlyForecastAdapter()
        weeklyAdapter = WeeklyForecastAdapter()

        hourlyForecastRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        weeklyForecastRecycler.layoutManager = LinearLayoutManager(this)

        hourlyForecastRecycler.adapter = hourlyAdapter
        weeklyForecastRecycler.adapter = weeklyAdapter

        // Load default city weather
        if (NetworkUtils.isNetworkAvailable(this)) {
            getWeatherData("Moscow")
            getHourlyForecast(55.7522, 37.6156) // Use Moscow's coordinates as default
            getWeeklyForecast(55.7522, 37.6156) // Use Moscow's coordinates as default
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
        }

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

                // Fetch hourly and weekly forecasts using city coordinates
                getHourlyForecast(weather.coord.lat, weather.coord.lon)
                getWeeklyForecast(weather.coord.lat, weather.coord.lon)
            } else {
                Toast.makeText(this, "Failed to load weather data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getHourlyForecast(lat: Double, lon: Double) {
        weatherViewModel.getHourlyForecast(lat, lon).observe(this, Observer { hourlyData ->
            hourlyAdapter.submitList(hourlyData)
        })
    }

    private fun getWeeklyForecast(lat: Double, lon: Double) {
        weatherViewModel.getDailyForecast(lat, lon).observe(this, Observer { dailyData ->
            weeklyAdapter.submitList(dailyData)
        })
    }

    private fun formatTime(unixTime: Long): String {
        val date = java.util.Date(unixTime * 1000)
        val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        return sdf.format(date)
    }
}
