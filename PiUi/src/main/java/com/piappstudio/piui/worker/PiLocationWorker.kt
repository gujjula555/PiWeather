/*
 *
 *  *
 *  *  * Copyright 2023 All rights are reserved by Pi App Studio
 *  *  *
 *  *  * Unless required by applicable law or agreed to in writing, software
 *  *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  * See the License for the specific language governing permissions and
 *  *  * limitations under the License.
 *  *  *
 *  *
 *
 *
 */

package com.piappstudio.piui.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.google.gson.Gson
import com.piappstudio.pimodel.Constant.WEATHER_RESPONSE
import com.piappstudio.pimodel.Resource
import com.piappstudio.pimodel.toFahrenheit
import com.piappstudio.pinetwork.PiWeatherRepository
import com.piappstudio.piui.location.PiLocationManager
import com.piappstudio.piui.notification.createForegroundInfo
import com.piappstudio.piui.notification.getNotificationBuilder
import com.piappstudio.piui.notification.showPiNotification
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.catch
import timber.log.Timber


@HiltWorker
class PiLocationWorker @AssistedInject constructor(@Assisted val context: Context,
                                           @Assisted private val workerParameters: WorkerParameters
): CoroutineWorker(context, workerParameters) {


    private val notificationBuilder = getNotificationBuilder()
    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface PiLocationWorkerProviderEntryPoint {
        fun piLocationManager():PiLocationManager
        fun weatherRepository(): PiWeatherRepository

    }

    override suspend fun doWork(): Result {

        val entryPoint = EntryPointAccessors.fromApplication(context, PiLocationWorkerProviderEntryPoint::class.java)
        val piLocationManager = entryPoint.piLocationManager()
        val weatherRepository = entryPoint.weatherRepository()

        // Create foreground service
        val foregroundInfo =createForegroundInfo(notificationBuilder)
        setForeground(foregroundInfo)
        val gson = Gson()

        piLocationManager.locationUpdates(1000).catch {
            Timber.e(it)
        }.collect {
            Timber.d("Received the location: $it")

            weatherRepository.fetchWeather(lat = it.latitude, long = it.longitude).collect { response ->
                if (response.status == Resource.Status.SUCCESS) {
                    val weatherInfo = response.data
                    val message = "${weatherInfo?.name}, ${weatherInfo?.sys?.country}, \n " +
                            "${response.data?.main?.temp?.toFahrenheit()}/${weatherInfo?.main?.feelsLike?.toFahrenheit()}"
                    Timber.d("Fetch response result: $message")
                    context.showPiNotification(message, notificationBuilder)
                    setProgressAsync(Data.Builder().putString(WEATHER_RESPONSE, gson.toJson(response.data)).build())
                }
            }
            if (isStopped) {
                return@collect
            }
        }

        Timber.d("STOPPED")
       return Result.success()
    }
}