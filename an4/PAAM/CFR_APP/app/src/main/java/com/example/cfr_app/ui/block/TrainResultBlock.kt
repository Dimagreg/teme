package com.example.cfr_app.ui.block

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cfr_app.service.data.Train
import com.example.cfr_app.ui.theme.CFR_LIGHT_GREEN
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun TrainResultBlock(
    train: Train,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

    val departureTime = train.departureTimestamp?.toDate()?.let { date ->
        timeFormat.format(date)
    } ?: "N/A"

    val arrivalTime = train.arrivalTimestamp?.toDate()?.let { date ->
        timeFormat.format(date)
    } ?: "N/A"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CFR_LIGHT_GREEN)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Train number
            if (train.trainNumber.isNotEmpty()) {
                Text(
                    text = "Train ${train.trainNumber}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Departure info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = train.originCity,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = departureTime,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Arrival info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = train.destinationCity,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = arrivalTime,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Delay indicator
            if (train.lateTimeMinutes > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Delay: ${train.lateTimeMinutes} min",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Red
                )
            }
        }
    }
}