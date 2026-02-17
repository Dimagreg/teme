package com.example.cfr_app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.cfr_app.service.FirebaseRepository

class TrainViewModelFactory(
        private val firebaseRepository: FirebaseRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(
            modelClass: Class<T>,
    extras: CreationExtras
    ): T {
        val savedStateHandle = extras.createSavedStateHandle()

        if (modelClass.isAssignableFrom(TrainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TrainViewModel(
                    firebaseRepo = firebaseRepository,
                    savedStateHandle = savedStateHandle
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}