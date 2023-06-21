package com.piappstudio.piweather.ui.welcome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.piappstudio.piweather.R
import com.piappstudio.piui.theme.Dimen
import com.piappstudio.piui.PILottie
import timber.log.Timber

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SplashScreen(viewModel: SplashViewModel = hiltViewModel()) {


    Scaffold(
        modifier = Modifier
            .fillMaxSize()
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .wrapContentSize(align = Alignment.Center)
                    .align(
                        Alignment.Center
                    )
            ) {

                val progress = PILottie(resourceId = R.raw.splash_weather, modifier = Modifier.fillMaxSize(0.6f))
                Text(text =  stringResource(id = R.string.title_gift_register), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                Spacer(modifier = Modifier.height(Dimen.space))
                Text(text = stringResource(R.string.welcome_description), style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(Dimen.space))
                LaunchedEffect(key1 = progress) {
                    Timber.d("Progress : $progress")
                    if (progress == 1.0f) {
                        Timber.d("Animation is completed")
                        viewModel.navigateToHome()

                    }
                }
            }

            Text(text = stringResource(R.string.copy_rights),
                color = MaterialTheme.colorScheme.onPrimary, textAlign = TextAlign.Center, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Light, modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .align(
                    Alignment.BottomCenter
                )
                .padding(Dimen.doubleSpace))

        }
    }
}