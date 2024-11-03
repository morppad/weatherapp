package com.example.weatherapp.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.model.DailyForecast
import java.text.SimpleDateFormat
import java.util.*

class WeeklyForecastAdapter : ListAdapter<DailyForecast, WeeklyForecastAdapter.DailyForecastViewHolder>(DailyForecastDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyForecastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_daily_forecast, parent, false)
        return DailyForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: DailyForecastViewHolder, position: Int) {
        val forecast = getItem(position)
        holder.bind(forecast)
    }

    class DailyForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dayText: TextView = itemView.findViewById(R.id.day_text)
        private val tempMinText: TextView = itemView.findViewById(R.id.temp_min_text)
        private val tempMaxText: TextView = itemView.findViewById(R.id.temp_max_text)
        private val weatherIcon: ImageView = itemView.findViewById(R.id.weather_icon)

        fun bind(dailyForecast: DailyForecast) {
            dayText.text = formatDay(dailyForecast.date)
            tempMinText.text = "${dailyForecast.tempMin}°C"
            tempMaxText.text = "${dailyForecast.tempMax}°C"
            weatherIcon.setImageResource(getWeatherIconResource(dailyForecast.weatherIcon))
        }

        private fun formatDay(unixTime: Long): String {
            val date = Date(unixTime * 1000)
            val sdf = SimpleDateFormat("EEE", Locale.getDefault())
            return sdf.format(date)
        }

        private fun getWeatherIconResource(icon: String): Int {
            return when (icon) {
                "01d" -> R.drawable.sun_icon
                "02d", "03d", "04d" -> R.drawable.cloud_icon
                "09d", "10d" -> R.drawable.rain_icon
                else -> R.drawable.default_icon
            }
        }
    }
}

class DailyForecastDiffCallback : DiffUtil.ItemCallback<DailyForecast>() {
    override fun areItemsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
        return oldItem.date == newItem.date
    }

    override fun areContentsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
        return oldItem == newItem
    }
}
