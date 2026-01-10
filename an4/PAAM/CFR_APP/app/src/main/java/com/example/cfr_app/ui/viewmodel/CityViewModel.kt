package com.example.cfr_app.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cfr_app.service.CityService
import com.example.cfr_app.service.LocationService
import kotlinx.coroutines.launch
import City

class CityViewModel(
    private val locationService: LocationService,
    private val cityService: CityService,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_ORIGIN_NAME = "origin_name"
        private const val KEY_ORIGIN_LAT = "origin_lat"
        private const val KEY_ORIGIN_LON = "origin_lon"

        private const val KEY_DEST_NAME = "dest_name"
        private const val KEY_DEST_LAT = "dest_lat"
        private const val KEY_DEST_LON = "dest_lon"

        private const val KEY_SELECTED_DATE = "selected_date"
    }

    private val _originCity = mutableStateOf<City?>(
        restoreCity(KEY_ORIGIN_NAME, KEY_ORIGIN_LAT, KEY_ORIGIN_LON)
    )
    val originCity: State<City?> = _originCity

    private val _destinationCity = mutableStateOf<City?>(
        restoreCity(KEY_DEST_NAME, KEY_DEST_LAT, KEY_DEST_LON)
    )
    val destinationCity: State<City?> = _destinationCity

    private val _selectedDate = mutableStateOf(
        savedStateHandle.get<Long?>(KEY_SELECTED_DATE)
    )
    val selectedDate: State<Long?> = _selectedDate

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _citiesLoaded = mutableStateOf(false)
    val citiesLoaded: State<Boolean> = _citiesLoaded

    init {
        loadCitiesFromFirebase()
    }

    private fun loadCitiesFromFirebase() {
        viewModelScope.launch {
            try {
                cityService.loadCities()
                _citiesLoaded.value = true
                Log.d("CityViewModel", "Cities loaded from Firebase: ${cityService.getAllCities().size}")
            } catch (e: Exception) {
                Log.e("CityViewModel", "Failed to load cities from Firebase", e)
            }
        }
    }

    // Helper to restore City from SavedStateHandle
    private fun restoreCity(nameKey: String, latKey: String, lonKey: String): City? {
        val name = savedStateHandle.get<String>(nameKey)
        val lat = savedStateHandle.get<Double>(latKey)
        val lon = savedStateHandle.get<Double>(lonKey)

        return if (name != null && lat != null && lon != null) {
            City(name, lat, lon)
        } else null
    }

    // Helper to save City to SavedStateHandle
    private fun saveCity(city: City?, nameKey: String, latKey: String, lonKey: String) {
        savedStateHandle[nameKey] = city?.name
        savedStateHandle[latKey] = city?.latitude
        savedStateHandle[lonKey] = city?.longitude
    }

    fun setOriginCity(city: City?) {
        _originCity.value = city
        saveCity(city, KEY_ORIGIN_NAME, KEY_ORIGIN_LAT, KEY_ORIGIN_LON)
    }

    fun setDestinationCity(city: City?) {
        _destinationCity.value = city
        saveCity(city, KEY_DEST_NAME, KEY_DEST_LAT, KEY_DEST_LON)
    }

    fun setDate(date: Long?) {
        _selectedDate.value = date
        savedStateHandle[KEY_SELECTED_DATE] = date
    }

    fun findNearestCity() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val location = locationService.getCurrentLocation()
                if (location != null) {
                    val city = cityService.findNearestCity(
                        location.latitude,
                        location.longitude
                    )
                    setOriginCity(city)
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getAllCities(): List<City> {
        return cityService.getAllCities()
    }
}