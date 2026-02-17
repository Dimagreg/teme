package com.example.cfr_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cfr_app.service.FirebaseRepository

class AppInfoViewModelFactory(
    private val firebaseRepository: FirebaseRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppInfoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppInfoViewModel(firebaseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}