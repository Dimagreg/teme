package com.example.cfr_app.ui.screen

import android.Manifest
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.cfr_app.ui.block.BlockSpacer
import com.example.cfr_app.ui.block.CityBlock
import com.example.cfr_app.ui.block.CitySelectionDialog
import com.example.cfr_app.ui.button.DatePicker
import com.example.cfr_app.ui.button.FindMe
import com.example.cfr_app.ui.button.Search
import com.example.cfr_app.ui.viewmodel.CityViewModel
import java.util.Date
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.ui.unit.dp
import com.example.cfr_app.ui.block.TrainResultBlock

@Composable
fun CitySelection(
    viewModel: CityViewModel,
    modifier: Modifier = Modifier
) {
    val originCity by viewModel.originCity
    val destinationCity by viewModel.destinationCity
    val selectedDate by viewModel.selectedDate
    val isLoading by viewModel.isLoading
    val citiesLoaded by viewModel.citiesLoaded // Main UI loading screen
    val trainResults by viewModel.trainResults
    val isSearchingTrains by viewModel.isSearchingTrains

    var showOriginCityDialog by remember { mutableStateOf(false) }
    var showDestinationCityDialog by remember { mutableStateOf(false) }

    // Permission launcher
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.findNearestCity()
        } else {
            Toast.makeText(context, "Location permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    if (!citiesLoaded) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier
                .fillMaxWidth(0.8f)
                .fillMaxSize()
        ) {
            // City Selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Origin city
                CityBlock(
                    modifier = Modifier.weight(3f),
                    city = originCity?.name ?: "From",
                    onClick = { showOriginCityDialog = true }
                )

                BlockSpacer(Modifier.weight(1f))

                // Destination City
                CityBlock(
                    modifier = Modifier.weight(3f),
                    city = destinationCity?.name ?: "To",
                    onClick = { showDestinationCityDialog = true }
                )
            }
            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FindMe(
                    modifier = Modifier.weight(3f),
                    onClick = {
                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                    },
                    isLoading = isLoading
                )
                DatePicker(
                    modifier = Modifier.weight(3f),
                    selectedDate = selectedDate,
                    onDateSelected = { timestamp ->
                        timestamp?.let {
                            Log.d("DatePicker", "Selected date: ${Date(it)}")
                            viewModel.setDate(it)
                        }
                    }
                )
                Search(
                    modifier = Modifier.weight(3f),
                    onClick = { viewModel.searchTrains() }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Train results
            if (isSearchingTrains) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (trainResults.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(trainResults) { train ->
                        TrainResultBlock(
                            train = train,
                            onClick = {
                                Log.d("CitySelection", "Train ${train.trainNumber} clicked")
                                // TODO: Navigate to train details
                            }
                        )
                    }
                }
            }
        }
    }

    // Select origin city
    if (showOriginCityDialog) {
        CitySelectionDialog(
            cities = viewModel.getAllCities(),
            onDismiss = { showOriginCityDialog = false },
            onCitySelected = { selectedCity ->
                viewModel.setOriginCity(selectedCity)
                showOriginCityDialog = false
            }
        )
    }

    // Select destination city
    if (showDestinationCityDialog) {
        CitySelectionDialog(
            cities = viewModel.getAllCities(),
            onDismiss = { showDestinationCityDialog = false },
            onCitySelected = { selectedCity ->
                viewModel.setDestinationCity(selectedCity)
                showDestinationCityDialog = false
            }
        )
    }
}