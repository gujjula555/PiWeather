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

package com.piappstudio.piweather

import com.piappstudio.pimodel.PrefUtil
import com.piappstudio.pimodel.Resource
import com.piappstudio.pimodel.WeatherResponse
import com.piappstudio.pimodel.error.PIError
import com.piappstudio.pinavigation.ErrorManager
import com.piappstudio.pinetwork.PiWeatherRepository
import com.piappstudio.piweather.ui.home.HomeScreenViewModel
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class HomeScreenViewModelTest {
    private lateinit var homeScreenViewModel: HomeScreenViewModel
    private val prefUtil = mockk<PrefUtil> (relaxed = true)
    private val repository = mockk<PiWeatherRepository>(relaxed = true)
    private val errorManager:ErrorManager = ErrorManager()
    private val locationManager:PiLocationManager = mockk(relaxed = true)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(TestCoroutineDispatcher())
    }

    @Test
    fun `validate default initialization`() {
        homeScreenViewModel = HomeScreenViewModel(prefUtil = prefUtil, piWeatherRepository =  repository, errorManager = errorManager, locationManager = locationManager)
        val homeScreenState = homeScreenViewModel.homeState.value
        assertEquals(null, homeScreenState.weatherResponse)
        assertEquals(false, homeScreenState.isLoading)
        assertEquals(null, homeScreenState.piError)
    }

    @Test
    fun `validate when user has previous location`() {
       coEvery { prefUtil.getCity() } returns "Texas"
       coEvery { repository.fetchWeather(any()) } returns flow { emit(Resource.success(
           WeatherResponse()
       )) }

        homeScreenViewModel = HomeScreenViewModel(prefUtil = prefUtil, piWeatherRepository =  repository, errorManager = errorManager, locationManager = locationManager)
        assertNotNull(homeScreenViewModel.homeState.value.weatherResponse)
        assertEquals("Texas", homeScreenViewModel.homeState.value.currentCity)

    }

    @Test
    fun `validate update weather method with different scenario`() {
        homeScreenViewModel = HomeScreenViewModel(prefUtil = prefUtil, piWeatherRepository =  repository, errorManager = errorManager, locationManager = locationManager)
        coEvery { repository.fetchWeather(any()) } returns flow { emit(Resource.success(
            WeatherResponse()
        )) }
        homeScreenViewModel.updateWeather("Texas")
        assertNotNull(homeScreenViewModel.homeState.value.weatherResponse)
        coEvery { repository.fetchWeather(any()) } returns flow { emit(Resource.error(
            error = PIError(404)
        )) }
        homeScreenViewModel.updateWeather("Texas")
        assertNotNull(homeScreenViewModel.homeState.value.piError)

    }

    @Test
    fun `validate fetch weatherForLocation method with different scenario`() {
        homeScreenViewModel = HomeScreenViewModel(prefUtil = prefUtil, piWeatherRepository =  repository, errorManager = errorManager, locationManager = locationManager)
        coEvery { repository.fetchWeather(any(), any()) } returns flow { emit(Resource.success(
            WeatherResponse()
        )) }
        homeScreenViewModel.fetchWeatherForLocation(20.0, 40.0)
        assertNotNull(homeScreenViewModel.homeState.value.weatherResponse)
        coEvery { repository.fetchWeather(any(), any()) } returns flow { emit(Resource.error(
            error = PIError(404)
        )) }
        homeScreenViewModel.fetchWeatherForLocation(20.0, 40.0)
        assertNotNull(homeScreenViewModel.homeState.value.piError)
    }

    @Test
    fun `validate update city`() {
        homeScreenViewModel = HomeScreenViewModel(prefUtil = prefUtil, piWeatherRepository =  repository, errorManager = errorManager, locationManager = locationManager)

        homeScreenViewModel.updateStateName("TX")
        assertEquals("TX", homeScreenViewModel.homeState.value.currentCity)
        homeScreenViewModel.cleanPreviousCity()
        assertEquals(null, homeScreenViewModel.homeState.value.currentCity)
    }
    @After
    fun tearDown() {
        unmockkAll()

    }
}