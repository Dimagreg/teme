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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cfr_app.service.data.Train
import com.example.cfr_app.ui.theme.CFR_LIGHT_GREEN
import com.example.cfr_app.ui.theme.CFR_RESULT_DARK_GREEN_50
import com.example.cfr_app.ui.theme.CFR_DETAILS_GREEN
import java.text.SimpleDateFormat
import java.util.*

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

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
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

            // Back button (optional)
            Spacer(modifier = Modifier.height(24.dp))
            TextButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("← Back to results")
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