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

import androidx.compose.ui.res.stringResource
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.piappstudio.pimodel.PrefUtil
import com.piappstudio.pinavigation.ErrorManager
import com.piappstudio.pinetwork.PiWeatherRepository
import com.piappstudio.piui.theme.PiWeatherTheme
import com.piappstudio.piweather.ui.home.HomeScreen
import com.piappstudio.piweather.ui.home.HomeScreenViewModel
import com.piappstudio.piweather.ui.home.PiLocationManager
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import junit.framework.TestCase.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject

@HiltAndroidTest
class HomeScreenTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)
    @get:Rule
    val composeTestRule = createComposeRule()


    @Inject
    lateinit var prefUtil: PrefUtil
    @Inject
    lateinit var errorManager: ErrorManager
    @Inject
    lateinit var locationManager: PiLocationManager

    private lateinit var homeScreenViewModel: HomeScreenViewModel

    @Inject
    lateinit var repository: PiWeatherRepository

    @Before
    fun setUp() {
        hiltRule.inject()
        homeScreenViewModel = HomeScreenViewModel(prefUtil, repository, errorManager, locationManager)
    }

    /**
     * To validate default page
     * */
    @Test
    fun validateHomeScreen() {

        var noWeatherData:String = ""
        composeTestRule.setContent {
            PiWeatherTheme {
                HomeScreen(homeScreenViewModel)
            }
            noWeatherData =stringResource(R.string.no_weather_data)
        }

        //Now perform the assert operation
        // To validate the
        composeTestRule.onNodeWithContentDescription("Search").assertExists()
        composeTestRule.onNodeWithText(noWeatherData).assertExists()
    }

    @Test
    fun validateHomeScreenWithTexasCity() {
        homeScreenViewModel.updateWeather("Texas")

        composeTestRule.setContent {
            PiWeatherTheme {
                HomeScreen(homeScreenViewModel)
            }
        }
        while (homeScreenViewModel.homeState.value.isLoading) {
            print("Wait till we loading the data")
            composeTestRule.onNodeWithTag("PiProgressBar").assertIsDisplayed()
        }
        assertNotNull(homeScreenViewModel.homeState.value.weatherResponse)
        // To display city name
        composeTestRule.onNodeWithText("Texas, US").assertIsDisplayed()


    }


}