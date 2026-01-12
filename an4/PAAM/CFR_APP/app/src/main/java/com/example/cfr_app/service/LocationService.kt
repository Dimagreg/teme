package com.example.cfr_app.service

import android.Manifest
import android.annotation.SuppressLint
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
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult

class LocationService(private val context: Context) {
    private val TAG = "LocationService"
    private val fusedLocationClient: FusedLocationProviderClient = 
        LocationServices.getFusedLocationProviderClient(context)
    private var lastLocation: Location? = null
    private var lastUpdateTime: Long = 0
    private var locationCallback: LocationCallback? = null
    
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

    @SuppressLint("MissingPermission")
    fun observeSpeed(onSpeedChanged: (Float) -> Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "Location permission not granted")
            return
        }

        // Initialize with last known location
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                lastLocation = it
                lastUpdateTime = System.currentTimeMillis()
            }
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { currentLocation ->
                    val currentTime = System.currentTimeMillis()

                    lastLocation?.let { prevLocation ->
                        val distance = prevLocation.distanceTo(currentLocation)
                        val timeDiff = (currentTime - lastUpdateTime) / 1000f

                        if (timeDiff > 0) {
                            val calculatedSpeed = distance / timeDiff
                            Log.d(TAG, "Speed changed: $calculatedSpeed")
                            onSpeedChanged(calculatedSpeed)
                        }
                    }

                    lastLocation = currentLocation
                    lastUpdateTime = currentTime
                }
            }
        }

        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_BALANCED_POWER_ACCURACY, 5000L)
            .setMinUpdateIntervalMillis(3000L)
            .build()

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback!!,
            null
        ).addOnFailureListener { exception ->
            Log.e(TAG, "Failed to start location updates", exception)
        }
    }

    fun stopObservingSpeed() {
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
        }
        locationCallback = null
    }
}