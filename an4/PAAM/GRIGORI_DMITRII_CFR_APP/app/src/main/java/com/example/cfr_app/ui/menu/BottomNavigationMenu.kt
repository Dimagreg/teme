package com.example.cfr_app.ui.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.cfr_app.R
import com.example.cfr_app.Routes
import com.example.cfr_app.ui.theme.CFR_DARK_GREEN
import com.example.cfr_app.ui.theme.CFR_GRAY
import com.example.cfr_app.ui.theme.CFR_LIGHT_GREEN

@Composable
fun BottomNavigationMenu(
    currentRoute: String?,
    onCityClick: () -> Unit,
    onTrainClick: () -> Unit,
    onInfoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(bottom = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(70.dp)
                .align(Alignment.Center)
                .shadow(8.dp, RoundedCornerShape(35.dp))
                .clip(RoundedCornerShape(35.dp))
                .background(CFR_LIGHT_GREEN)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // City Selection Button
            IconButton(onClick = onCityClick) {
                Icon(
                    painter = painterResource(id = R.drawable.citypicker),
                    contentDescription = "City Selection",
                    modifier = Modifier.size(26.dp),
                    tint = if (currentRoute == Routes.CITY_SELECTION)
                        CFR_DARK_GREEN
                    else
                        CFR_GRAY
                )
            }

            // Train Picker Button
            IconButton(onClick = onTrainClick) {
                Icon(
                    painter = painterResource(id = R.drawable.train),
                    contentDescription = "Train Picker",
                    modifier = Modifier.size(26.dp),
                    tint = if (currentRoute == Routes.TRAIN_PICKER)
                        CFR_DARK_GREEN
                    else
                        CFR_GRAY
                )
            }

            // App Info Button
            IconButton(onClick = onInfoClick) {
                Icon(
                    painter = painterResource(id = R.drawable.info),
                    contentDescription = "App Information",
                    modifier = Modifier.size(26.dp),
                    tint = if (currentRoute == Routes.APP_INFO)
                        CFR_DARK_GREEN
                    else
                        CFR_GRAY
                )
            }
        }
    }
}