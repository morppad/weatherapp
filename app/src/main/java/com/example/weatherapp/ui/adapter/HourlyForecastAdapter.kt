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
import com.example.weatherapp.model.HourlyForecast

class HourlyForecastAdapter : ListAdapter<HourlyForecast, HourlyForecastAdapter.HourlyForecastViewHolder>(HourlyForecastDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyForecastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_hourly_forecast, parent, false)
        return HourlyForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourlyForecastViewHolder, position: Int) {
        val forecast = getItem(position)
        holder.bind(forecast)
    }

    class HourlyForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val temperatureText: TextView = itemView.findViewById(R.id.temperature_text)
        private val timeText: TextView = itemView.findViewById(R.id.time_text)
        private val weatherIcon: ImageView = itemView.findViewById(R.id.weather_icon)

        fun bind(hourlyForecast: HourlyForecast) {
            temperatureText.text = "${hourlyForecast.temp}°C"
            timeText.text = hourlyForecast.hour
            weatherIcon.setImageResource(hourlyForecast.weatherIcon)
        }
    }

    class HourlyForecastDiffCallback : DiffUtil.ItemCallback<HourlyForecast>() {
        override fun areItemsTheSame(oldItem: HourlyForecast, newItem: HourlyForecast): Boolean {
            return oldItem.hour == newItem.hour
        }

        override fun areContentsTheSame(oldItem: HourlyForecast, newItem: HourlyForecast): Boolean {
            return oldItem == newItem
        }
    }
}
