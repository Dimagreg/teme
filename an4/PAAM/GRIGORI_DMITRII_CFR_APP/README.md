# CFR Train Tracker App

An Android application for tracking Romanian Railway (CFR - Căile Ferate Române) trains, providing real-time information about train schedules, delays, and routes.

## Features

### Train Search
- **Search by Route**: Find trains between two cities on a specific date
- **Search by Train Number**: Look up specific trains by their number
- **Real-time Information**: View departure/arrival times and delay information
- **Train Details**: Access comprehensive route and schedule information

### Location-Based Services
- **Find Nearest City**: Automatically detect your current location and find the nearest train station
- **Permission Handling**: Secure location permission management
- **GPS Integration**: Uses Google Play Services Location API for accurate positioning

### Navigation
- **City Selection**: Browse and select from all available Romanian railway cities
- **Intuitive UI**: Clean, modern interface built with Jetpack Compose
- **Bottom Navigation**: Easy switching between different app sections
- **Side Menu**: Quick access to all app features

### App Information
- **About Section**: Displays app information and credits
- **Dynamic Content**: Information loaded from Firebase backend

## Technology Stack

### Android Framework
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Minimum SDK**: 24 (Android 7.0)
- **Target SDK**: 36

### Key Libraries
- **Firebase Firestore**: Backend database for cities and train data
- **Google Play Services Location**: GPS and location services
- **Navigation Compose**: Screen navigation management
- **Coroutines**: Asynchronous programming
- **Material Design 3**: Modern UI components
- **Core SplashScreen**: Branded app launch experience

## Project Structure

```
app/src/main/java/com/example/cfr_app/
├── MainActivity.kt              # Main entry point
├── Navigation.kt                # Navigation routes
├── service/                     # Business logic layer
│   ├── CityService.kt          # City-related operations
│   ├── FirebaseRepository.kt   # Firebase data access
│   ├── LocationService.kt      # GPS/location handling
│   └── data/                   # Data models
│       ├── City.kt             # City entity
│       └── Train.kt            # Train entity
└── ui/                         # User interface layer
    ├── animation/              # UI animations
    ├── block/                  # Reusable UI blocks
    ├── button/                 # Custom buttons
    ├── menu/                   # Navigation menus
    ├── screen/                 # Main screens
    │   ├── CitySelection.kt    # Route search screen
    │   ├── TrainPickerScreen.kt# Train number search
    │   ├── TrainDetailsView.kt # Train details
    │   └── AppInfoScreen.kt    # About screen
    ├── theme/                  # App theming
    └── viewmodel/              # ViewModels (MVVM)
```

## Setup Instructions

### Prerequisites
- Android Studio (latest version recommended)
- JDK 11 or higher
- Android SDK 24 or higher
- Google Play Services

### Build and Run
1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd GRIGORI_DMITRII_CFR_APP
   ```

2. Open the project in Android Studio

3. Sync Gradle files

4. Firebase is already configured inside app/google-services.json with existing collections

5. Run the app on an emulator or physical device:
   ```bash
   ./gradlew assembleDebug
   ```

## Permissions

The app requires the following permissions:
- **ACCESS_FINE_LOCATION**: For GPS-based nearest city detection
- **ACCESS_COARSE_LOCATION**: For approximate location services

## Usage

### Finding Trains by Route
1. Open the app and navigate to "City Selection" screen
2. Select your origin city (or use "Find Me" to auto-detect)
3. Select your destination city
4. Choose a date
5. Tap "Search" to view available trains
6. Tap on any train to view detailed information

### Finding Trains by Number
1. Navigate to "Train Picker" screen
2. Tap on "Select train" to choose a train number
3. Select a date
4. Tap "Search" to view train details
5. View route, timing, and delay information

## Architecture

The app follows the **MVVM (Model-View-ViewModel)** architecture pattern:

- **Model**: Data classes (`City`, `Train`) and repository layer (`FirebaseRepository`)
- **View**: Composable UI functions in the `ui/screen/` package
- **ViewModel**: State management and business logic in `ui/viewmodel/` package

### Key Components

- **LocationService**: Manages GPS functionality and location permissions
- **CityService**: Handles city-related operations and nearest city calculations
- **FirebaseRepository**: Abstracts Firestore database operations
- **ViewModels**: Manage UI state and coordinate between services and UI

## Features in Detail

### Location Detection
The app uses the Fused Location Provider API to get the user's current location and calculates the nearest train station using the Haversine formula for distance calculation.

### Date Selection
Custom date picker component allows users to select travel dates with a user-friendly interface.

### Train Delay Tracking
Real-time delay information is displayed for all trains, helping users plan their journeys effectively.

### Responsive UI
Built entirely with Jetpack Compose, the app features:
- Smooth animations
- Material Design 3 components
- Adaptive layouts

## Development

### Adding New Cities
Add city documents to the Firestore `cities` collection with:
- `name`: City name
- `latitude`: Geographic latitude
- `longitude`: Geographic longitude

### Adding Train Data
Add train documents to the Firestore `trains` collection with:
- `trainNumber`: Train identifier (e.g., "IR 1234")
- `originCity`: Departure city name
- `destinationCity`: Arrival city name
- `departureTimestamp`: Departure time (Firebase Timestamp)
- `arrivalTimestamp`: Arrival time (Firebase Timestamp)
- `lateTimeMinutes`: Delay in minutes (integer)

## Author

**Grigori Dmitrii**

---

*I love trains*
