package com.example.cfr_app.service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import android.util.Log

class LocationService(private val context: Context) {
    private val TAG = "LocationService"
    private val fusedLocationClient: FusedLocationProviderClient = 
        LocationServices.getFusedLocationProviderClient(context)
    
    suspend fun getCurrentLocation(): Location? {
        Log.d(TAG, "Requesting fresh location...")
        
        return suspendCancellableCoroutine { continuation ->
            if (ActivityCompat.checkSelfPermission(
                context, 
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
                Log.w(TAG, "Location permission not granted")
                continuation.resume(null)
                return@suspendCancellableCoroutine
            }

            val cancellationToken = CancellationTokenSource()

            fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationToken.token
            )
                .addOnSuccessListener { location ->
                    if (location != null) {
                        Log.i(TAG, "Fresh location: ${location.latitude}, ${location.longitude}")
                    } else {
                        Log.w(TAG, "Location is null")
                    }
                    continuation.resume(location)
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Failed to get location", exception)
                    continuation.resume(null)
                }

            continuation.invokeOnCancellation {
                cancellationToken.cancel()
            }
        }
    }
}