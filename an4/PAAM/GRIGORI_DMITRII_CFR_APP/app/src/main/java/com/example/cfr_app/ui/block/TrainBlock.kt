package com.example.cfr_app.ui.block

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cfr_app.ui.theme.CFR_LIGHT_GREEN

@Composable
fun TrainBlock(
    modifier: Modifier = Modifier,
    train: String,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .height(60.dp)
            .padding(5.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(CFR_LIGHT_GREEN)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = train,
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}