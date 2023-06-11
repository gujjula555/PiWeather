package com.piappstudio.piweather.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piappstudio.pimodel.PrefUtil
import com.piappstudio.pimodel.Resource
import com.piappstudio.pimodel.WeatherResponse
import com.piappstudio.pimodel.error.PIError
import com.piappstudio.pinavigation.ErrorInfo
import com.piappstudio.pinavigation.ErrorManager
import com.piappstudio.pinavigation.ErrorState
import com.piappstudio.pinetwork.PiWeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


data class HomeScreenState(val isLoading:Boolean = false,
                           val weatherResponse: WeatherResponse?=null,
                           val piError: PIError?=null,
                           val currentCity:String? = null)

@HiltViewModel
class HomeScreenViewModel @Inject constructor(private val prefUtil: PrefUtil,
                                              private val piWeatherRepository: PiWeatherRepository,
                                              private val errorManager: ErrorManager, val locationManager: PiLocationManager):ViewModel()  {


    private val _homeState:MutableStateFlow<HomeScreenState> = MutableStateFlow(HomeScreenState())
    val homeState:StateFlow<HomeScreenState> = _homeState

    init {
        _homeState.update { it.copy(currentCity = prefUtil.getCity()) }
        _homeState.value.currentCity?.let {
            updateWeather(it)

        }
        updateWeather(prefUtil.getCity())
    }

    fun updateWeather(city:String?) {
        Timber.d("Called update weather $city")
        if (city.isNullOrBlank()) {
            return
        }

        viewModelScope.launch  {
            piWeatherRepository.fetchWeather(cityName = city).onEach { response ->
                Timber.d(response.data.toString())
                when (response.status) {
                    Resource.Status.SUCCESS-> {
                        prefUtil.saveCity(city)
                        _homeState.update { it.copy(isLoading = false, weatherResponse = response.data )}
                    }
                    Resource.Status.LOADING-> {
                        _homeState.update { it.copy(isLoading = true) }
                    }
                    Resource.Status.ERROR -> {
                        _homeState.update { it.copy(isLoading = false, piError = response.error) }
                        errorManager.post(ErrorInfo(piError = response.error, action = {
                            updateWeather(city= city)
                        }, errorState = ErrorState.NEGATIVE))
                    }
                    else -> {

                    }
                }
            }.collect()
        }

    }

    fun updateStateName(city: String) {
        if (city.length<50) {
            _homeState.update { it.copy(currentCity = city) }
        }
    }

    fun cleanPreviousCity() {
        _homeState.update { it.copy(currentCity = null) }
    }

    fun fetchWeatherForLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch  {
            piWeatherRepository.fetchWeather(lat = latitude, long = longitude).onEach { response ->
                Timber.d(response.data.toString())
                when (response.status) {
                    Resource.Status.SUCCESS-> {
                        response.data?.name?.let { city->
                            prefUtil.saveCity(city)
                            _homeState.update { it.copy(currentCity = city) }
                        }

                        _homeState.update { it.copy(isLoading = false, weatherResponse = response.data )}
                    }
                    Resource.Status.LOADING-> {
                        _homeState.update { it.copy(isLoading = true) }
                    }
                    Resource.Status.ERROR -> {
                        _homeState.update { it.copy(isLoading = false, piError = response.error) }
                        errorManager.post(ErrorInfo(piError = response.error, action = {
                            fetchWeatherForLocation(latitude, longitude)
                        }, errorState = ErrorState.NEGATIVE))
                    }
                    else -> {

                    }
                }
            }.collect()
        }
    }
}