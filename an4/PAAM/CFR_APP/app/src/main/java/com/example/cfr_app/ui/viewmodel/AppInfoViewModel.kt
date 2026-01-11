package com.example.cfr_app.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cfr_app.service.FirebaseRepository
import kotlinx.coroutines.launch

class AppInfoViewModel(
    private val firebaseRepo: FirebaseRepository
) : ViewModel() {

    private val _appInfoText = mutableStateOf("")
    val appInfoText: State<String> = _appInfoText

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    init {
        loadAppInfo()
    }

    private fun loadAppInfo() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val text = firebaseRepo.getAppInfo()
                _appInfoText.value = text
                Log.d("AppInfoViewModel", "App info loaded")
            } catch (e: Exception) {
                Log.e("AppInfoViewModel", "Failed to load app info", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}