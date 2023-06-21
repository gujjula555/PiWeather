package com.piappstudio.piweather.ui.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotStarted
import androidx.compose.material.icons.filled.RunCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.WindPower
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.piappstudio.pimodel.Constant.EMPTY_STRING
import com.piappstudio.pimodel.Main
import com.piappstudio.pimodel.Sys
import com.piappstudio.pimodel.WeatherItem
import com.piappstudio.pimodel.WeatherResponse
import com.piappstudio.pimodel.toFahrenheit
import com.piappstudio.piui.PermissionBox
import com.piappstudio.piui.PiProgressIndicator
import com.piappstudio.piui.checkLocationPermission
import com.piappstudio.piui.piShadow
import com.piappstudio.piui.theme.Dimen
import com.piappstudio.piweather.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: HomeScreenViewModel = hiltViewModel()) {

    val homeState by viewModel.homeState.collectAsState()
    if (homeState.isLoading) {
        PiProgressIndicator()
    }
    Scaffold {

        var isBasedOnCurrentLocation by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(it)
                .padding(Dimen.space)
        ) {
            Spacer(modifier = Modifier.height(Dimen.tripleSpace))
            AddSearchView(text = homeState.currentCity ?: EMPTY_STRING, onSearchChange = { city ->
                viewModel.updateStateName(city)
            }, onClickSearch = {
                viewModel.updateWeather(homeState.currentCity)
            }) {
                isBasedOnCurrentLocation = true
            }
            if (isBasedOnCurrentLocation) {
                viewModel.cleanPreviousCity()
                AddLocationPermissionButton(viewModel) {
                    isBasedOnCurrentLocation = false
                }
            }
            Spacer(modifier = Modifier.height(Dimen.doubleSpace))
            RenderWeatherInformation(homeState.weatherResponse, viewModel = viewModel)
        }
    }
}


@Composable
fun RenderWeatherInformation(weatherResponse: WeatherResponse? = null, viewModel: HomeScreenViewModel) {


    Box(modifier = Modifier.fillMaxSize()) {
        if (weatherResponse == null) {
            Column(
                modifier = Modifier
                    .padding(Dimen.doubleSpace)
                    .align(Alignment.Center)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(R.string.acc_search),
                    Modifier
                        .size(Dimen.image_size)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.size(Dimen.space))
                Text(
                    text = stringResource(R.string.no_weather_data),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleSmall
                )

            }
        } else {
            WeatherMainScreen(weatherResponse)
        }

        AskNotificationPermission()

        val homeScreenState by viewModel.homeState.collectAsState()
        val context = LocalContext.current
        FloatingActionButton(onClick = {
                                       if (homeScreenState.isForegroundServiceStarted) {
                                           viewModel.stopService(context)
                                       } else {
                                           viewModel.startService(context)
                                       }
        }, modifier = Modifier
            .padding(Dimen.doubleSpace)
            .align(Alignment.BottomEnd)) {
            if (homeScreenState.isForegroundServiceStarted) {
                Icon(imageVector = Icons.Default.StopCircle, contentDescription = "Stop location tracking")
            } else {
                Icon(imageVector = Icons.Default.NotStarted, contentDescription = "Start location tracking" )
            }
        }

    }


}

@Composable
fun AskNotificationPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ActivityCompat.checkSelfPermission(
                LocalContext.current,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            var showDialog by remember {
                mutableStateOf(true)
            }
            if (showDialog) {

                PermissionBox(lottieResource = com.piappstudio.piui.R.raw.notifications,
                    description = stringResource(id = R.string.post_notification_description),
                    permission = Manifest.permission.POST_NOTIFICATIONS, onDismiss = {
                        showDialog = false
                    }) {
                    showDialog = false

                }
            }
        }

    }

}

@Preview
@Composable
fun PiWidget(
    modifier: Modifier = Modifier,
    imageVector: ImageVector = Icons.Default.Warning,
    title: String = "Humidity",
    actualValue: String = "35"
) {
    Card(modifier = modifier.piShadow()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimen.space)
        ) {
            Icon(
                imageVector = imageVector,
                contentDescription = title,
                modifier = Modifier
                    .size(Dimen.image_size)
                    .padding(top = Dimen.space)
            )
            Spacer(modifier = Modifier.height(Dimen.space))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = actualValue,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.outline
            )

        }

    }
}

@Preview
@Composable
fun PreviewAddSearchView() {
    AddSearchView(text = "City", onSearchChange = {}, onClickSearch = { }) {

    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun AddSearchView(
    text: String,
    onSearchChange: (text: String) -> Unit,
    onClickSearch: () -> Unit,
    onClickLocation: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(Dimen.doubleSpace), verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = text,
            onValueChange = {
                onSearchChange.invoke(it)
            },
            placeholder = {
                Text(text = stringResource(R.string.city_state_country))
            },
            keyboardActions = KeyboardActions(onSearch = {
                keyboardController?.hide()
                onClickSearch.invoke()
            }),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            modifier = Modifier
                .weight(1.0f)
                .padding(Dimen.space),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(id = R.string.search)
                )
            },
            maxLines = 1,
            trailingIcon = {
                IconButton(
                    onClick = { onClickLocation.invoke() }
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = stringResource(R.string.access_current_location)
                    )
                }
            }
        )
    }
}


@Composable
fun AddLocationPermissionButton(viewModel: HomeScreenViewModel, callback: () -> Unit) {

    if (LocalContext.current.checkLocationPermission()) {
        viewModel.requestCurrentLocation(callback)
    } else {
        PermissionBox(
            lottieResource = com.piappstudio.piui.R.raw.location,
            description = stringResource(R.string.location_description),
            permissions = listOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            requiredPermissions = listOf(android.Manifest.permission.ACCESS_COARSE_LOCATION),
            onDismiss = callback
        ) {
            if (it.isNotEmpty()) {
                viewModel.requestCurrentLocation(callback)
            }
        }
    }

}

/** To render Weather details*/

@Preview
@Composable
fun WeatherMainScreen(
    weatherResponse: WeatherResponse = WeatherResponse(
        weather = listOf(
            WeatherItem("02d", main = "Cloud", description = "Mostly cloud")
        ),
        main = Main(temp = 111.2, tempMin = 234.4, tempMax = 255.0, humidity = 60),
        name = "Texas",
        sys = Sys(country = "US")
    )
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (weatherResponse.weather?.isNotEmpty() == true) {
            val weatherItem = weatherResponse.weather?.get(0)
            // To render Weather screen
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = weatherItem?.iconUrl(),
                    modifier = Modifier.size(Dimen.image_size),
                    contentDescription = stringResource(R.string.weather_icon)
                )
                Text(
                    text = weatherItem?.main ?: EMPTY_STRING,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimen.space))

        // To render the location information
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = stringResource(id = R.string.access_current_location)
            )
            Text(
                text = "${weatherResponse.name}, ${weatherResponse.sys?.country}",
                style = MaterialTheme.typography.labelMedium,
            )
        }

        // To render main temp & feel like value
        Text(
            text = weatherResponse.main?.temp?.toFahrenheit() ?: EMPTY_STRING,
            style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(
                R.string.feels_like,
                weatherResponse.main?.feelsLike?.toFahrenheit() ?: EMPTY_STRING
            ),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.outline
        )
        Spacer(modifier = Modifier.height(Dimen.doubleSpace))
        // To render other information
        Row(
            modifier = Modifier.padding(Dimen.space),
            horizontalArrangement = Arrangement.spacedBy(Dimen.doubleSpace)
        ) {
            PiWidget(
                imageVector = Icons.Default.RunCircle,
                title = stringResource(R.string.humidity),
                actualValue = (weatherResponse.main?.humidity?.toString() ?: "0") + " %",
                modifier = Modifier.weight(1f)

            )
            PiWidget(
                imageVector = Icons.Default.WindPower,
                title = stringResource(R.string.pressure),
                actualValue = weatherResponse.main?.pressure?.toString() + stringResource(R.string.hpa),
                modifier = Modifier.weight(1f)
            )

        }


        Spacer(modifier = Modifier.height(Dimen.doubleSpace))
        Text(
            text = stringResource(R.string.temperature),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Light,
            modifier = Modifier
                .padding(Dimen.space)
                .align(Alignment.Start)
        )
        Row(
            modifier = Modifier.padding(Dimen.space),
            horizontalArrangement = Arrangement.spacedBy(Dimen.doubleSpace)
        ) {
            PiWidget(
                imageVector = Icons.Default.WbSunny,
                title = stringResource(R.string.min),
                actualValue = weatherResponse.main?.tempMin?.toFahrenheit() ?: EMPTY_STRING,
                modifier = Modifier.weight(1f)
            )
            PiWidget(
                modifier = Modifier.weight(1f),
                imageVector = Icons.Default.WbSunny,
                title = stringResource(R.string.max),
                actualValue = weatherResponse.main?.tempMax?.toFahrenheit() ?: EMPTY_STRING
            )

        }

    }
}
//#end region
