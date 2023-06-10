# Pi Weather 
Pi Weather is an android application created to demonstrate the coding capability and hands on experience in various framework described below

## Features:
- Designed as single activity based application
- Designed with jetpack compose using latest material3 framework and kotlin
- Uses Hilt Android for dependency injection
- Uses MVVM (Model, View, View-Model) along with MVI (Model-View-Intent)

## Key frameworks
- Retrofit to network operation
- Coil to cache images
- AppCompact Permission for runtime permission
- Lottie for splash animations
- Hilt for dependency injection
- Uses Jetpack compose BOM

## Submodules
This app is internally depends the below submodules
### PiUI 
Module that holds common ui components and extension functions.
#### Components
- PiProgressIndicator - to display progress bar in UI
- PiPermissionRequired - custom logic to display runtime permission
### PiNetwork
This module is responsible for managing the network calls using retrofit framework. 
It uses, hilt framework for dependency injections and exposes PiWeatherRepository class outside for further communication
### PiNavigation
This module helps to track the error message and navigation between the jetpack compose screen
- NavManager - Simple class that emit routInfo as on when user try to navigation. Most probably, the MainActivity will listen for this changes and perform the navigation using NavHostController
- ErrorManager - Simple class that emit errorInfo as on when user try to post error message to UI. MainActivity hosting all the pages on top of parent Scaffold that display this error messages in the form of Toast
### PiModel
This is core model that hold business model, business logics & other core functionalities

## Test Coverage
This app uses mockk for Junit test cases
- Network module's  repository class has 100% coverage
- PiModel's data class has 100 % coverage
- HomeViewModel has 85% of coverage
- Covered Instrumentation test cases for Home Screen