package com.example.cfr_app.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cfr_app.service.City
import com.example.cfr_app.service.CityService
import com.example.cfr_app.service.LocationService
import kotlinx.coroutines.launch

class CityViewModel(
    private val locationService: LocationService,
    private val cityService: CityService,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val KEY_ORIGIN_CITY = "origin_city"
        private const val KEY_DESTINATION_CITY = "destination_city"
        private const val KEY_SELECTED_DATE = "selected_date"
    }

    private val _originCity = mutableStateOf(
        savedStateHandle.get<City?>(KEY_ORIGIN_CITY)
    )
    val originCity: State<City?> = _originCity

    private val _destinationCity = mutableStateOf(
        savedStateHandle.get<City?>(KEY_DESTINATION_CITY)
    )
    val destinationCity: State<City?> = _destinationCity

    private val _selectedDate = mutableStateOf(
        savedStateHandle.get<Long?>(KEY_SELECTED_DATE)
    )
    val selectedDate: State<Long?> = _selectedDate

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

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
                    _originCity.value = city
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setOriginCity(city: City?) {
        _originCity.value = city
        savedStateHandle[KEY_ORIGIN_CITY] = city
    }

    fun setDestinationCity(city: City?) {
        _destinationCity.value = city
        savedStateHandle[KEY_DESTINATION_CITY] = city
    }

    fun setDate(date: Long?) {
        _selectedDate.value = date
        savedStateHandle[KEY_SELECTED_DATE] = date
    }

    fun getAllCities(): List<City> {
        return cityService.getAllCities()
    }
}