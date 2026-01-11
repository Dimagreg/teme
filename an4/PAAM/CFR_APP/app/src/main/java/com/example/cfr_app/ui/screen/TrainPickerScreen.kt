package com.example.cfr_app.ui.screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cfr_app.ui.block.TrainBlock
import com.example.cfr_app.ui.block.TrainNumberSelectionDialog
import com.example.cfr_app.ui.button.DatePicker
import com.example.cfr_app.ui.button.Search
import com.example.cfr_app.ui.theme.CFR_LIGHT_GREEN
import com.example.cfr_app.ui.viewmodel.TrainViewModel
import com.example.cfr_app.ui.block.TrainRouteBlock
import java.util.Date

@Composable
fun TrainPickerScreen(
    viewModel: TrainViewModel,
    modifier: Modifier = Modifier
) {
    val selectedDate by viewModel.selectedDate
    val selectedTrainNumber by viewModel.selectedTrainNumber
    val availableTrainNumbers by viewModel.availableTrainNumbers
    val trainSearchResults by viewModel.trainSearchResults
    val isSearchingByNumber by viewModel.isSearchingByNumber
    val selectedTrain by viewModel.selectedTrain

    var showTrainNumberDialog by remember { mutableStateOf(false) }

    if (selectedTrain != null) {
        // Show train details view
        TrainDetailsView(
            train = selectedTrain!!,
            onDetailsClick = {
                Log.d("TrainPicker", "Details clicked for train ${selectedTrain!!.trainNumber}")
            },
            onRefreshClick = {
                viewModel.refreshTrainDetails()
            },
            onBackClick = {
                viewModel.selectTrain(null)
            }
        )
    } else {
        Column(
            modifier = modifier
                .fillMaxWidth(0.8f)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .clip(RoundedCornerShape(25.dp))
                    .background(CFR_LIGHT_GREEN)
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Find my train",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Train number selector
            TrainBlock(
                modifier = Modifier.fillMaxWidth(0.6f),
                train = selectedTrainNumber ?: "Select train",
                onClick = { showTrainNumberDialog = true }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Date and Search row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DatePicker(
                    modifier = Modifier.weight(1f),
                    selectedDate = selectedDate,
                    onDateSelected = { timestamp ->
                        timestamp?.let {
                            Log.d("TrainPicker", "Selected date: ${Date(it)}")
                            viewModel.setDate(it)
                        }
                    }
                )

                Spacer(modifier = Modifier.width(16.dp))

                Search(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        viewModel.searchTrainByNumber()
                    }
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Results
            if (isSearchingByNumber) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (trainSearchResults.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    items(trainSearchResults) { train ->
                        TrainRouteBlock(
                            train = train,
                            onClick = {
                                viewModel.selectTrain(train)
                            }
                        )
                    }
                }
            } else if (selectedTrainNumber != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No trains found for this date")
                }
            }
        }
    }

    // Train number selection dialog
    if (showTrainNumberDialog) {
        TrainNumberSelectionDialog(
            trainNumbers = availableTrainNumbers,
            onDismiss = { showTrainNumberDialog = false },
            onTrainNumberSelected = { trainNumber ->
                viewModel.selectTrainNumber(trainNumber)
                showTrainNumberDialog = false
            }
        )
    }
}