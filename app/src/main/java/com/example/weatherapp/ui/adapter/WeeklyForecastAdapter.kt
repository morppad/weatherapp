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

class WeeklyForecastAdapter : ListAdapter<DailyForecast, WeeklyForecastAdapter.WeeklyForecastViewHolder>(WeeklyForecastDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyForecastViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_weekly_forecast, parent, false)
        return WeeklyForecastViewHolder(view)
    }

    override fun onBindViewHolder(holder: WeeklyForecastViewHolder, position: Int) {
        val forecast = getItem(position)
        holder.bind(forecast)
    }

    class WeeklyForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val dayText: TextView = itemView.findViewById(R.id.day_text)
        private val tempMinText: TextView = itemView.findViewById(R.id.temp_min_text)
        private val tempMaxText: TextView = itemView.findViewById(R.id.temp_max_text)
        private val weatherIcon: ImageView = itemView.findViewById(R.id.weather_icon)

        fun bind(dailyForecast: DailyForecast) {
            dayText.text = dailyForecast.day
            tempMinText.text = "Min: ${dailyForecast.tempMin}°C"
            tempMaxText.text = "Max: ${dailyForecast.tempMax}°C"
            weatherIcon.setImageResource(dailyForecast.weatherIcon)
        }
    }

    class WeeklyForecastDiffCallback : DiffUtil.ItemCallback<DailyForecast>() {
        override fun areItemsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
            return oldItem.day == newItem.day
        }

        override fun areContentsTheSame(oldItem: DailyForecast, newItem: DailyForecast): Boolean {
            return oldItem == newItem
        }
    }
}
