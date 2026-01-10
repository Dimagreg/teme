package com.example.cfr_app.service

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import City

class FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()
    private val citiesCollection = db.collection("cities")

    companion object {
        private const val TAG = "FirebaseRepository"
    }

    suspend fun getAllCities(): List<City> {
        return try {
            val snapshot = citiesCollection.get().await()
            val cities = snapshot.documents.mapNotNull { doc ->
                doc.toObject(City::class.java)
            }
            Log.d(TAG, "Loaded ${cities.size} cities from Firebase")
            cities
        } catch (e: Exception) {
            Log.e(TAG, "Error loading cities", e)
            emptyList()
        }
    }

    suspend fun addCity(city: City) {
        try {
            citiesCollection.add(city).await()
            Log.d(TAG, "City added: ${city.name}")
        } catch (e: Exception) {
            Log.e(TAG, "Error adding city", e)
        }
    }
}