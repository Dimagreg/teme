package com.example.cfr_app.service

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import City
import com.example.cfr_app.service.data.Train
import com.google.firebase.Timestamp
import java.util.Calendar

class FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()
    private val citiesCollection = db.collection("cities")
    private val trainsCollection = db.collection("trains")

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

    suspend fun searchTrains(
        originCity: String,
        destinationCity: String,
        selectedDateMillis: Long
    ): List<Train> {
        return try {
            // Get start and end of the selected day
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = selectedDateMillis
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfDay = Timestamp(calendar.time)

            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
            val endOfDay = Timestamp(calendar.time)

            Log.d(TAG, "Searching trains: $originCity -> $destinationCity on ${calendar.time}")

            val snapshot = trainsCollection
                .whereEqualTo("originCity", originCity)
                .whereEqualTo("destinationCity", destinationCity)
                .whereGreaterThanOrEqualTo("departureTimestamp", startOfDay)
                .whereLessThanOrEqualTo("departureTimestamp", endOfDay)
                .orderBy("departureTimestamp")
                .get()
                .await()

            val trains = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Train::class.java)
            }

            Log.d(TAG, "Found ${trains.size} trains")
            trains
        } catch (e: Exception) {
            Log.e(TAG, "Error searching trains", e)
            emptyList()
        }
    }
}