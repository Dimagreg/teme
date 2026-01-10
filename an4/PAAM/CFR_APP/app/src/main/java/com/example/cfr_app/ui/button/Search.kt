package com.example.cfr_app.ui.button

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cfr_app.ui.theme.CFR_DARK_GREEN

@Composable
fun Search(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(40.dp)
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .clip(RoundedCornerShape(25.dp))
            .background(CFR_DARK_GREEN),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Search",
            fontSize = 14.sp
        )
    }
}