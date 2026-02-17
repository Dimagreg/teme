package com.example.cfr_app.ui.block

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cfr_app.service.data.Train
import com.example.cfr_app.ui.theme.CFR_LIGHT_GREEN
import com.example.cfr_app.ui.theme.CFR_RESULT_DARK_GREEN_50
import com.example.cfr_app.ui.theme.CFR_RESULT_ON_TIME_GREEN
import com.example.cfr_app.ui.theme.CFR_RESULT_LATE_RED
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

    val punctualityColor = if (train.lateTimeMinutes > 0) CFR_RESULT_LATE_RED else CFR_RESULT_ON_TIME_GREEN

    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(containerColor = CFR_LIGHT_GREEN)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Train number block
            Box(
                modifier = Modifier
                    .height(30.dp)
                    .width(70.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(CFR_RESULT_DARK_GREEN_50),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = train.trainNumber,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Departure time block
            Box(
                modifier = Modifier
                    .height(30.dp)
                    .width(50.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(punctualityColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = departureTime,
                    textAlign = TextAlign.Center
                )
            }

            // Late time
            Column(
                modifier = Modifier
                    .width(40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (train.lateTimeMinutes > 0) {
                    Text(
                        text = "+${train.lateTimeMinutes}min",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp
                    )
                }
            }

            // Arrival time block
            Box(
                modifier = Modifier
                    .height(30.dp)
                    .width(50.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(punctualityColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = arrivalTime,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details button block
            Box(
                modifier = Modifier
                    .height(30.dp)
                    .width(70.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .clickable(onClick = onClick)
                    .background(CFR_RESULT_DARK_GREEN_50),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Details",
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}