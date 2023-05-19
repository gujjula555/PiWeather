/*
 *
 *  * Copyright 2023 All rights are reserved by Pi App Studio
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *  *
 *
 */
package com.piappstudio.piweather

import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.piappstudio.pimodel.Constant.EMPTY_STRING
import com.piappstudio.pimodel.Route
import com.piappstudio.pinavigation.ErrorState
import com.piappstudio.piweather.ui.common.ScreenName
import com.piappstudio.piweather.ui.home.HomeScreen
import com.piappstudio.piweather.ui.welcome.SplashScreen
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainActivity.SetUpAppNavGraph() {
    navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    HandleError(snackbarHostState)
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Surface(modifier = Modifier
            .padding(it)
            .fillMaxSize()) {
            AppNavGraph(navController = navController)
        }
    }

    // Listen for navigation change and execute the navigation
    val navInfo by navManager.routeInfo.collectAsState()
    LaunchedEffect(key1 = navInfo) {
        navInfo.id?.let {
            if (it == Route.Control.BACK) {
                // firebaseAnalytics.logEvent("Click_Back",null)

                navController.navigateUp()
                navManager.navigate(null)
                return@let
            }
            var bundle= Bundle()
            bundle.putString("link",it)
            // firebaseAnalytics.logEvent("Click_Navigate",bundle)

            navController.navigate(it, navOptions = navInfo.navOption)
            navManager.navigate(null)
        }

    }

}

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Route.Screen.SPLASH ) {
        composable(Route.Screen.SPLASH) {
            SplashScreen()
        }
        composable(Route.Screen.HOME) {
            HomeScreen()
        }
    }
}

@Composable
fun MainActivity.HandleError(snackbarHostState: SnackbarHostState) {
    val errorInfo by errorManager.errorInfo.collectAsState()
    val retry = if (errorInfo.action==null) null else stringResource(id = com.piappstudio.pimodel.R.string.retry)

    val serverFailMessage = stringResource(id = com.piappstudio.pimodel.R.string.general_error)

    var updatedErrorMessage = errorInfo.copy()
    Timber.d("Handle Error function is called")
    if (errorInfo.errorState == ErrorState.SERVER_FAIL || errorInfo.piError != null) {
        updatedErrorMessage = errorInfo.copy(message = serverFailMessage)
    }
    if (errorInfo.piError?.code == 404) {
        updatedErrorMessage = errorInfo.copy(message = stringResource(R.string.location_not_found))
    }

    LaunchedEffect(errorInfo) {

        updatedErrorMessage.message?.let { message ->
            if (updatedErrorMessage.errorState == ErrorState.POSITIVE) {
                snackbarHostState.showSnackbar(
                    message,
                    withDismissAction = false,
                    duration = SnackbarDuration.Short
                )
                errorManager.post(null)
            } else if (updatedErrorMessage.errorState != ErrorState.NONE) {
                val displayMessage = updatedErrorMessage.message
                val snackbarResult = snackbarHostState.showSnackbar(
                    message = displayMessage ?: EMPTY_STRING,
                    actionLabel = retry,
                    withDismissAction = true,
                    duration = SnackbarDuration.Indefinite
                )
                if (snackbarResult == SnackbarResult.ActionPerformed) {
                    errorManager.post(null)
                    updatedErrorMessage.action?.invoke()
                } else {
                    errorManager.post(null)
                }
            }

        }
    }


}