package com.example.weatherapp.util

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task

object LocationUtils {
    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(context: Context, callback: (Location?) -> Unit) {
        val fusedLocationClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                callback(location)
            }
            .addOnFailureListener { e ->
                Log.e("LocationUtils", "Error getting location: ", e)
                callback(null)
            }
    }
}
