package com.example.cfr_app.ui.menu

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.cfr_app.R
import com.example.cfr_app.ui.theme.CFR_DARK_GREEN
import com.example.cfr_app.ui.theme.CFR_LIGHT_GREEN

@Composable
fun TopMenuBar(
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(CFR_LIGHT_GREEN),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.cfr_main_logo),
            contentDescription = "CFR Logo",
            modifier = Modifier.size(140.dp)
        )

        IconButton(
            onClick = onMenuClick,
            modifier = Modifier.offset(y = (-10).dp)
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                modifier = Modifier.size(40.dp),
                tint = CFR_DARK_GREEN
            )
        }
    }
}