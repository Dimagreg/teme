package com.example.cfr_app.service

import android.location.Location
import android.util.Log

class CityService {
    private val TAG = "CityService"

    private val cities = listOf(
        City("Bucharest", 44.4268, 26.1025),
        City("Cluj-Napoca", 46.7712, 23.6236),
        City("Timisoara", 45.7489, 21.2087)
        // Add more cities
    )

    private fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0] / 1000 // Convert meters to km
    }

    fun findNearestCity(latitude: Double, longitude: Double): City? {
        val nearestCity = cities.minByOrNull { city ->
            val distance = calculateDistance(latitude, longitude, city.lat, city.lon)
            Log.d(TAG, "Distance to ${city.name}: $distance km")
            distance
        }

        Log.i(TAG, "Nearest city: ${nearestCity?.name}")
        return nearestCity
    }

    fun getAllCities(): List<City> = cities
}

data class City(val name: String, val lat: Double, val lon: Double)