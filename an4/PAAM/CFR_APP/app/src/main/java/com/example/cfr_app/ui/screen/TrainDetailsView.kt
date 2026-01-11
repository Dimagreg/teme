package com.example.cfr_app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cfr_app.service.data.Train
import com.example.cfr_app.ui.block.BlockSpacer
import com.example.cfr_app.ui.theme.CFR_LIGHT_GREEN
import com.example.cfr_app.ui.theme.CFR_RESULT_DARK_GREEN_50
import com.example.cfr_app.ui.theme.CFR_DETAILS_GREEN
import com.example.cfr_app.ui.theme.CFR_RESULT_LATE_RED
import com.example.cfr_app.ui.theme.CFR_RESULT_ON_TIME_GREEN
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.*

@Preview(showBackground = true)
@Composable
fun TrainDetailsViewPreview() {
    // Create sample train data
    val sampleTrain = Train(
        trainNumber = "IR1644",
        originCity = "Bucharest",
        destinationCity = "Cluj-Napoca",
        departureTimestamp = Timestamp.now(),
        arrivalTimestamp = Timestamp(Date(System.currentTimeMillis() + 8 * 60 * 60 * 1000)), // 8 hours later
        lateTimeMinutes = 120
    )

    TrainDetailsView(
        train = sampleTrain,
        onDetailsClick = {},
        onRefreshClick = {},
        onBackClick = {}
    )
}

@Composable
fun TrainDetailsView(
    modifier: Modifier = Modifier,
    train: Train,
    onDetailsClick: () -> Unit = {},
    onRefreshClick: () -> Unit = {},
    onBackClick: () -> Unit = {}
) {
    val dateFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    val departureDate = train.departureTimestamp?.toDate()?.let { dateFormat.format(it) } ?: "N/A"
    val departureTime = train.departureTimestamp?.toDate()?.let { timeFormat.format(it) } ?: "N/A"

    val arrivalDate = train.arrivalTimestamp?.toDate()?.let { dateFormat.format(it) } ?: "N/A"
    val arrivalTime = train.arrivalTimestamp?.toDate()?.let { timeFormat.format(it) } ?: "N/A"

    val punctualityColor = if (train.lateTimeMinutes > 0) CFR_RESULT_LATE_RED else CFR_RESULT_ON_TIME_GREEN.copy(alpha = 0.8f)
    val punctualityText = if (train.lateTimeMinutes > 0) "+${train.lateTimeMinutes} min" else "on time"

    val adjustedArrivalTime = train.arrivalTimestamp?.toDate()?.let { date ->
        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.MINUTE, train.lateTimeMinutes)
        timeFormat.format(calendar.time)
    } ?: "N/A"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(containerColor = CFR_LIGHT_GREEN)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top row with Details and Refresh buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Details button
                Box(
                    modifier = Modifier
                        .height(40.dp)
                        .width(100.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CFR_RESULT_DARK_GREEN_50)
                        .clickable(onClick = onDetailsClick),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Refresh button
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CFR_RESULT_DARK_GREEN_50)
                        .clickable(onClick = onRefreshClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh",
                        tint = Color.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Train route information
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Departure block
                StationInfoBlock(
                    cityName = train.originCity,
                    date = departureDate,
                    time = departureTime,
                    label = "Departure"
                )

                Spacer(modifier = Modifier.width(2.dp))

                // Arrival block
                StationInfoBlock(
                    cityName = train.destinationCity,
                    date = arrivalDate,
                    time = arrivalTime,
                    label = "Arrival"
                )
            }

            // Late information
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Departure time
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = departureTime,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        maxLines = 1
                    )
                }

                // Punctuality indicator (center)
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = punctualityText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = punctualityColor
                    )
                }

                // Adjusted arrival time
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 50.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = adjustedArrivalTime,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = punctualityColor,
                        maxLines = 1
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                BlockSpacer(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = punctualityColor
                )
            }

            // Back button
            Spacer(modifier = Modifier.height(24.dp))
            TextButton(
                onClick = onBackClick,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CFR_RESULT_DARK_GREEN_50)
            ) {
                Text(
                    text = "Back to results",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun StationInfoBlock(
    cityName: String,
    date: String,
    time: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(140.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CFR_DETAILS_GREEN)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                fontSize = 10.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // City name
            Text(
                text = cityName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Date
            Text(
                text = date,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Time
            Text(
                text = time,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
    }
}