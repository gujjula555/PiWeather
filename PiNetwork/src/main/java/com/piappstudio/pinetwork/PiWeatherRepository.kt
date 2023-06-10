package com.piappstudio.pinetwork

import android.content.Context
import com.piappstudio.pimodel.Resource
import com.piappstudio.pimodel.WeatherResponse
import com.piappstudio.pimodel.error.ErrorCode
import com.piappstudio.pimodel.error.PIError
import com.piappstudio.pinetwork.di.isNetworkAvailable
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton


private const val appId = "45e4e8b5f521a8bf54544c5a19f06c20"

@Singleton
class PiWeatherRepository @Inject constructor(
    private val iWeather: IWeather,
    @ApplicationContext val context: Context
) {

    suspend fun fetchWeather(cityName: String): Flow<Resource<WeatherResponse?>> {
        return makeSafeApiCall {
            withContext(Dispatchers.IO) {
                iWeather.fetchWeather(cityName, appId)
            }
        }
    }

    suspend fun fetchWeather(lat: Double, long: Double): Flow<Resource<WeatherResponse?>> {
        return makeSafeApiCall {
            withContext(Dispatchers.IO) {
                iWeather.fetchWeather(lat, long, appId)
            }

        }
    }

    private suspend fun <T> makeSafeApiCall(api: suspend () -> Response<T?>) = flow {
        emit(Resource.loading())
        if (context.isNetworkAvailable()) {
            val response = api.invoke()
            if (response.isSuccessful) {
                emit(Resource.success(response.body()))
            } else {
                emit(Resource.error(null, error = PIError(response.code())))
            }
        } else {
            emit(Resource.error(error = PIError(code = ErrorCode.NETWORK_NOT_AVAILABLE)))
        }
    }.catch { ex ->
        emit(Resource.error(error = PIError(code = ErrorCode.NETWORK_CONNECTION_FAILED)))
        Timber.e(ex)
    }
}