package com.example.weatherapp.util

import android.content.Context
import android.location.Geocoder
import android.location.Location
import java.util.Locale

object LocationUtils {
    fun getCityName(context: Context, location: Location): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        return addresses?.firstOrNull()?.locality ?: "Unknown"
    }
}
