package com.example.cfr_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cfr_app.service.CityService
import com.example.cfr_app.service.FirebaseRepository
import com.example.cfr_app.service.LocationService
import com.example.cfr_app.ui.menu.BottomNavigationMenu
import com.example.cfr_app.ui.menu.SideMenu
import com.example.cfr_app.ui.menu.TopMenuBar
import com.example.cfr_app.ui.screen.AppInfoScreen
import com.example.cfr_app.ui.screen.CitySelection
import com.example.cfr_app.ui.screen.TrainPickerScreen
import com.example.cfr_app.ui.theme.CFR_APPTheme
import com.example.cfr_app.ui.viewmodel.AppInfoViewModel
import com.example.cfr_app.ui.viewmodel.AppInfoViewModelFactory
import com.example.cfr_app.ui.viewmodel.CityViewModel
import com.example.cfr_app.ui.viewmodel.CityViewModelFactory
import com.example.cfr_app.ui.viewmodel.TrainViewModel
import com.example.cfr_app.ui.viewmodel.TrainViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CFR_APPTheme {
                val navController = rememberNavController()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                // Track current route
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Create services and viewModels
                val locationService = remember { LocationService(this) }
                val firebaseRepo = remember { FirebaseRepository() }
                val cityService = remember { CityService(firebaseRepo) }

                val cityViewModel: CityViewModel = viewModel(
                    factory = CityViewModelFactory(
                        locationService = locationService,
                        cityService = cityService,
                        firebaseRepo = firebaseRepo
                    )
                )
                val trainViewModel: TrainViewModel = viewModel(
                    factory = TrainViewModelFactory(
                        firebaseRepository = firebaseRepo
                    )
                )
                val appInfoViewModel: AppInfoViewModel = viewModel(
                    factory = AppInfoViewModelFactory(
                        firebaseRepository = firebaseRepo
                    )
                )

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        SideMenu(
                            onCitySelectionClick = {
                                navController.navigate(Routes.CITY_SELECTION) {
                                    popUpTo(Routes.CITY_SELECTION) { inclusive = true}
                                }
                            },
                            onTrainPickerClick = {
                                navController.navigate(Routes.TRAIN_PICKER)
                            },
                            onAppInfoClick = {
                                navController.navigate(Routes.APP_INFO)
                            },
                            onDismiss = {
                                scope.launch {
                                    drawerState.close()
                                }
                            }
                        )
                    },
                    gesturesEnabled = true
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            TopMenuBar(
                                onMenuClick = {
                                    scope.launch {
                                        drawerState.open()
                                    }
                                }
                            )
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
                                    CitySelection(viewModel = cityViewModel)
                                }
                            }
                            composable(Routes.TRAIN_PICKER) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(top = 32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    TrainPickerScreen(viewModel = trainViewModel)
                                }
                            }
                            composable(Routes.APP_INFO) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(top = 32.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    AppInfoScreen(viewModel = appInfoViewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}