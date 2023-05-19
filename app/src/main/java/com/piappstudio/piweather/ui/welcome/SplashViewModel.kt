package com.piappstudio.piweather.ui.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import com.piappstudio.pimodel.PrefUtil
import com.piappstudio.pimodel.Resource
import com.piappstudio.pimodel.Route
import com.piappstudio.pinavigation.NavInfo
import com.piappstudio.pinavigation.NavManager
import com.piappstudio.pinetwork.PiWeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
@HiltViewModel
class SplashViewModel @Inject constructor(val prefUtil: PrefUtil,
                                          val piWeatherRepository: PiWeatherRepository, private val navManager: NavManager):ViewModel() {



    fun navigateToHome() {
        navManager.navigate(
            NavInfo(id = Route.Screen.HOME,
            navOption = NavOptions.Builder().setPopUpTo(Route.Screen.SPLASH, inclusive = true).build())
        )
    }
}
