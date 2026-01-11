package com.example.cfr_app.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cfr_app.service.FirebaseRepository
import com.example.cfr_app.service.data.Train
import kotlinx.coroutines.launch

class TrainViewModel(
    private val firebaseRepo: FirebaseRepository,
    private val savedStateHandle: SavedStateHandle
): ViewModel() {

    companion object {
        private const val KEY_SELECTED_DATE = "train_selected_date"
        private const val KEY_TRAIN_NUMBER = "train_number"
    }

    private val _selectedDate = mutableStateOf(
        savedStateHandle.get<Long?>(KEY_SELECTED_DATE) ?: System.currentTimeMillis()
    )
    val selectedDate: State<Long> = _selectedDate

    private val _selectedTrainNumber = mutableStateOf<String?>(
        savedStateHandle.get(KEY_TRAIN_NUMBER)
    )
    val selectedTrainNumber: State<String?> = _selectedTrainNumber

    private val _availableTrainNumbers = mutableStateOf<List<String>>(emptyList())
    val availableTrainNumbers: State<List<String>> = _availableTrainNumbers

    private val _trainSearchResults = mutableStateOf<List<Train>>(emptyList())
    val trainSearchResults: State<List<Train>> = _trainSearchResults

    private val _isSearchingByNumber = mutableStateOf(false)
    val isSearchingByNumber: State<Boolean> = _isSearchingByNumber

    private val _selectedTrain = mutableStateOf<Train?>(null)
    val selectedTrain: State<Train?> = _selectedTrain

    private val _hasSearched = mutableStateOf(false)
    val hasSearched: State<Boolean> = _hasSearched

    init {
        loadAllTrainNumber()
    }

    fun setDate(date: Long) {
        _selectedDate.value = date
        savedStateHandle[KEY_SELECTED_DATE] = date
    }

    fun selectTrainNumber(trainNumber: String) {
        _selectedTrainNumber.value = trainNumber
        savedStateHandle[KEY_TRAIN_NUMBER] = trainNumber
        _hasSearched.value = false
    }

    fun selectTrain(train: Train?) {
        _selectedTrain.value = train
    }

    fun loadAllTrainNumber() {
        viewModelScope.launch {
            try {
                val trainNumbers = firebaseRepo.getAllTrainNumbers()
                _availableTrainNumbers.value = trainNumbers.sorted()
                Log.d("TrainViewModel", "Loaded ${trainNumbers.size} train numbers")
            } catch (e: Exception) {
                Log.e("TrainViewModel", "Failed to load train numbers", e)
            }
        }
    }

    fun searchTrainByNumber() {
        viewModelScope.launch {
            val trainNumber = _selectedTrainNumber.value
            val date = _selectedDate.value

            if (trainNumber == null) {
                Log.w("TrainViewModel", "Cannot search: missing train number")
                return@launch
            }

            _isSearchingByNumber.value = true
            _hasSearched.value = true
            try {
                val results = firebaseRepo.searchTrainsByNumber(trainNumber, date)
                _trainSearchResults.value = results
                Log.d("TrainViewModel", "Found ${results.size} trains")
            } catch (e: Exception) {
                Log.e("TrainViewModel", "Search by number failed", e)
                _trainSearchResults.value = emptyList()
            } finally {
                _isSearchingByNumber.value = false
            }
        }
    }

    fun refreshTrainDetails() {
        viewModelScope.launch {
            val train = _selectedTrain.value ?: return@launch
            _isSearchingByNumber.value = true
            try {
                val date = train.departureTimestamp?.toDate()?.time ?: return@launch
                val results = firebaseRepo.searchTrainsByNumber(train.trainNumber, date)
                val updatedTrain = results.find { it.trainNumber == train.trainNumber }
                _selectedTrain.value = updatedTrain
                Log.d("TrainViewModel", "Train details refreshed")
            } catch (e: Exception) {
                Log.e("TrainViewModel", "Failed to refresh train details", e)
            } finally {
                _isSearchingByNumber.value = false
            }
        }
    }
}