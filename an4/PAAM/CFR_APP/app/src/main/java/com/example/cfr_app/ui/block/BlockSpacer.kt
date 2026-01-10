package com.example.cfr_app.ui.block

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.cfr_app.ui.theme.CFR_LIGHT_GREEN

@Composable
fun BlockSpacer(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(15.dp)
            .padding(5.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(CFR_LIGHT_GREEN)
    ) {
        // empty
    }
}