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

package com.piappstudio.piweather.ui.home

import android.content.Context
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.gson.Gson
import com.piappstudio.pimodel.Constant
import com.piappstudio.pimodel.WeatherResponse
import com.piappstudio.piui.worker.PiLocationWorker


fun HomeScreenViewModel.startCoroutineWorker(context: Context) {
    val workManager = WorkManager.getInstance(context)
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.UNMETERED)
        .build()

    val workRequest = OneTimeWorkRequestBuilder<PiLocationWorker>()
        .setConstraints(constraints).addTag(Constant.LOCATION_COROUTINE_WORKER)
        .build()

    workManager.enqueue(workRequest)

    val gson = Gson()

    workManager.getWorkInfoByIdLiveData(workRequest.id).observeForever {

       val response = it.progress.getString(Constant.WEATHER_RESPONSE)
        response?.let {strResponse->
           val weatherResponse = gson.fromJson(strResponse, WeatherResponse::class.java)
            updateWeatherResponse(weatherResponse)
        }

    }

}

fun HomeScreenViewModel.stopCoroutineWorker(context: Context) {
    WorkManager.getInstance(context).cancelAllWorkByTag(Constant.LOCATION_COROUTINE_WORKER)
}