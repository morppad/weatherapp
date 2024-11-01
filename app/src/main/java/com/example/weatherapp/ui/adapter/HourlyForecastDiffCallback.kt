package com.example.weatherapp.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.weatherapp.model.HourlyForecast

class HourlyForecastDiffCallback : DiffUtil.ItemCallback<HourlyForecast>() {
    override fun areItemsTheSame(oldItem: HourlyForecast, newItem: HourlyForecast): Boolean {
        return oldItem.hour == newItem.hour // Assuming `hour` is unique for each item
    }

    override fun areContentsTheSame(oldItem: HourlyForecast, newItem: HourlyForecast): Boolean {
        return oldItem == newItem
    }
}
