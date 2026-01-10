package com.example.cfr_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cfr_app.service.CityService
import com.example.cfr_app.service.LocationService
import com.example.cfr_app.ui.menu.BottomNavigationMenu
import com.example.cfr_app.ui.menu.TopMenuBar
import com.example.cfr_app.ui.screen.AppInfoScreen
import com.example.cfr_app.ui.screen.CitySelection
import com.example.cfr_app.ui.screen.TrainPickerScreen
import com.example.cfr_app.ui.theme.CFR_APPTheme
import com.example.cfr_app.ui.viewmodel.CityViewModel
import com.example.cfr_app.ui.viewmodel.CityViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CFR_APPTheme {
                val navController = rememberNavController()

                // Track current route
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Create services and viewModel
                val locationService = remember { LocationService(this) }
                val cityService = remember { CityService() }
                val viewModel: CityViewModel = viewModel(
                    factory = CityViewModelFactory(
                        locationService = locationService,
                        cityService = cityService
                    )
                )

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopMenuBar(onMenuClick = { })
                    },
                    bottomBar = {
                        BottomNavigationMenu(
                            currentRoute = currentRoute,
                            onCityClick = {
                                navController.navigate(Routes.CITY_SELECTION) {
                                    // Clear back stack when city selected again
                                    popUpTo(Routes.CITY_SELECTION) { inclusive = true }
                                }
                            },
                            onTrainClick = {
                                navController.navigate(Routes.TRAIN_PICKER)
                            },
                            onInfoClick = {
                                navController.navigate(Routes.APP_INFO)
                            }
                        )
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Routes.CITY_SELECTION,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Routes.CITY_SELECTION) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 32.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CitySelection(viewModel = viewModel)
                            }
                        }
                        composable(Routes.TRAIN_PICKER) {
                            TrainPickerScreen()
                        }
                        composable(Routes.APP_INFO) {
                            AppInfoScreen()
                        }
                    }
                }
            }
        }
    }
}