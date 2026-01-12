package com.example.cfr_app.ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cfr_app.ui.theme.CFR_LIGHT_GREEN
import com.example.cfr_app.ui.theme.CFR_DARK_GREEN

@Composable
fun SideMenu(
    onCitySelectionClick: () -> Unit,
    onTrainPickerClick: () -> Unit,
    onAppInfoClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier.width(200.dp),
        drawerContainerColor = CFR_LIGHT_GREEN
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Menu",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            HorizontalDivider()

            Spacer(modifier = Modifier.height(8.dp))

            // City Selection
            MenuButton(
                text = "Routes",
                onClick = {
                    onCitySelectionClick()
                    onDismiss()
                }
            )

            // Train Picker
            MenuButton(
                text = "Trains",
                onClick = {
                    onTrainPickerClick()
                    onDismiss()
                }
            )

            // App Info button
            MenuButton(
                text = "About",
                onClick = {
                    onAppInfoClick()
                    onDismiss()
                }
            )
        }
    }
}

@Composable
private fun MenuButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CFR_DARK_GREEN)
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    }
}