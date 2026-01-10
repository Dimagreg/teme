package com.example.cfr_app.service

import City
import android.location.Location
import android.util.Log

class CityService(
    private val firebaseRepo: FirebaseRepository
) {
    companion object{
        private val TAG = "CityService"
    }

    private var cachedCities: List<City> = emptyList()

    suspend fun loadCities() {
        cachedCities = firebaseRepo.getAllCities()
        Log.d(TAG, "Cities cached: ${cachedCities.size}")
    }

    fun getAllCities(): List<City> = cachedCities

    private fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Float {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0] / 1000 // Convert meters to km
    }

    fun findNearestCity(latitude: Double, longitude: Double): City? {
        val nearestCity = cachedCities.minByOrNull { city ->
            val distance = calculateDistance(latitude, longitude, city.latitude, city.longitude)
            Log.d(TAG, "Distance to ${city.name}: $distance km")
            distance
        }

        Log.i(TAG, "Nearest city: ${nearestCity?.name}")
        return nearestCity
    }
}