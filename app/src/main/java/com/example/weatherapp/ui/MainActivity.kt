package com.example.weatherapp.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.api.WeatherApi
import com.example.weatherapp.model.HourlyForecast
import com.example.weatherapp.model.WeatherResponse
import com.example.weatherapp.model.getWeatherIcon
import com.example.weatherapp.ui.adapter.HourlyForecastAdapter
import com.example.weatherapp.util.Constants
import com.example.weatherapp.util.NetworkUtils
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.weatherapp.ui.WeatherViewModel

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
    private lateinit var hourlyForecastAdapter: HourlyForecastAdapter
    private lateinit var hourlyForecastRecyclerView: RecyclerView
    private lateinit var switchTempUnitButton: Button
    private var isCelsius = true
    private var baseTemperatureCelsius: Double? = null
    private var isFahrenheit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        initializeApiAndViewModel()
        setupButtonListeners()

        // Подключаем наблюдателя для обновления почасового прогноза
        weatherViewModel.hourlyForecast.observe(this, Observer { hourlyData ->
            if (hourlyData.isNotEmpty()) {
                hourlyForecastAdapter.submitList(hourlyData)
            } else {
                Log.e("WeatherApp", "Hourly data is empty")
            }
        })

        // Дефолтные данные при загрузке
        if (NetworkUtils.isNetworkAvailable(this)) {
            getWeatherData("Moscow")
        } else {
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
        }
    }
    private fun initializeViews() {
        // Инициализация всех компонентов UI
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
        switchTempUnitButton = findViewById(R.id.switch_temp_unit_button)  // Кнопка переключения температуры

        // Инициализация RecyclerView для почасового прогноза
        hourlyForecastRecyclerView = findViewById(R.id.hourly_forecast_recycler)
        hourlyForecastAdapter = HourlyForecastAdapter()
        hourlyForecastRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            adapter = hourlyForecastAdapter
        }
    }


    private fun initializeApiAndViewModel() {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val weatherApi = retrofit.create(WeatherApi::class.java)

        val factory = WeatherViewModelFactory(weatherApi)
        weatherViewModel = ViewModelProvider(this, factory)[WeatherViewModel::class.java]
    }

    private fun setupButtonListeners() {
        searchButton.setOnClickListener {
            val city = cityInput.text.toString()
            if (city.isNotBlank()) {
                getWeatherData(city)
            } else {
                Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show()
            }
        }

        changeCityButton.setOnClickListener {
            toggleSearchLayout(true)
        }

        switchTempUnitButton.setOnClickListener {
            isFahrenheit = !isFahrenheit
            updateTemperatureDisplay()
        }
    }
    private fun convertToFahrenheit(celsius: Double): Double {
        return celsius * 9 / 5 + 32
    }
    private fun updateTemperatureDisplay() {
        baseTemperatureCelsius?.let { baseTemp ->
            val displayTemp = if (isFahrenheit) {
                // Convert Celsius to Fahrenheit
                baseTemp * 9 / 5 + 32
            } else {
                // Display in Celsius
                baseTemp
            }

            temperatureText.text = String.format("%.1f°%s", displayTemp, if (isFahrenheit) "F" else "C")
        }
    }

    private fun formatTemperature(temp: Double): String {
        val convertedTemp = if (isCelsius) temp else convertToFahrenheit(temp)
        return String.format("%.1f°%s", convertedTemp, if (isCelsius) "C" else "F")
    }


    private fun toggleSearchLayout(showSearch: Boolean) {
        searchLayout.visibility = if (showSearch) View.VISIBLE else View.GONE
        cityInfoLayout.visibility = if (showSearch) View.GONE else View.VISIBLE
    }

    private fun updateHourlyForecastUI(hourlyData: List<HourlyForecast>) {
        // Устанавливаем адаптер с новыми данными
        hourlyForecastAdapter.submitList(hourlyData)
        hourlyForecastRecyclerView.adapter = hourlyForecastAdapter
        hourlyForecastAdapter.notifyDataSetChanged()  // Принудительное обновление
    }


    private fun getWeatherData(city: String) {
        weatherViewModel.getWeather(city).observe(this, Observer { weather ->
            if (weather != null) {
                updateWeatherUI(weather)
                toggleSearchLayout(false)
                cityNameText.text = city.capitalize()

                // Получаем координаты из текущей погоды
                val lat = weather.coord.lat
                val lon = weather.coord.lon

                // Получаем почасовой прогноз и обновляем интерфейс
                weatherViewModel.getHourlyForecast(lat, lon).observe(this) { hourlyData ->
                    if (hourlyData.isNotEmpty()) {
                        updateHourlyForecastUI(hourlyData)
                        Log.d("WeatherApp", "Hourly forecast updated with ${hourlyData.size} items")  // Логирование размера данных
                    } else {
                        Log.e("WeatherApp", "No hourly forecast data received")
                    }
                }
            } else {
                Toast.makeText(this, "Не удалось загрузить данные о погоде", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun getHourlyForecast(lat: Double, lon: Double) {
        hourlyForecastRecyclerView.adapter = hourlyForecastAdapter
        hourlyForecastAdapter.submitList(emptyList()) // Очищаем старые данные перед обновлением
        weatherViewModel.getHourlyForecast(lat, lon).observe(this) { hourlyData ->
            if (hourlyData.isNotEmpty()) {
                Log.d("WeatherApp", "Обновление данных для почасового прогноза")  // Лог успешного обновления
                Log.d("WeatherApp", "Hourly data size: ${hourlyData.size}")
                hourlyForecastAdapter.submitList(hourlyData) // Добавляем данные в адаптер
                hourlyForecastRecyclerView.adapter = hourlyForecastAdapter // Перепривязываем адаптер
            } else {
                Log.e("WeatherApp", "Hourly data is empty")
            }
        }
    }


//    private fun getHourlyForecast(lat: Double, lon: Double) {
//        weatherViewModel.getHourlyForecast(lat, lon).observe(this, Observer { hourlyData ->
//            if (hourlyData.isNotEmpty()) {
//                hourlyForecastAdapter.submitList(hourlyData)
//            } else {
//                Log.e("WeatherApp", "Hourly data is empty")
//            }
//        })

    private fun updateWeatherUI(weather: WeatherResponse) {
        baseTemperatureCelsius = weather.main.temp
        temperatureText.text = "${weather.main.temp}°C"
        descriptionText.text = weather.weather.firstOrNull()?.description ?: "Unknown"
        feelsLikeText.text = "Feels like ${weather.main.feels_like}°C"
        humidityText.text = "Humidity: ${weather.main.humidity}%"
        windSpeedText.text = "Wind speed: ${weather.wind.speed} m/s"
        sunriseSunsetText.text = "Sunrise: ${formatTime(weather.sys.sunrise)} Sunset: ${formatTime(weather.sys.sunset)}"
    }


    private fun formatTime(unixTime: Long): String {
        val date = java.util.Date(unixTime * 1000)
        val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        return sdf.format(date)
    }
}
