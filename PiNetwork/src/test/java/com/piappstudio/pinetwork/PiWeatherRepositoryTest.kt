package com.piappstudio.pinetwork

import android.content.Context
import com.piappstudio.pimodel.Resource
import com.piappstudio.pimodel.WeatherResponse
import com.piappstudio.pinetwork.di.isNetworkAvailable
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.net.SocketException


class PiWeatherRepositoryTest {

    private lateinit var piWeatherRepository: PiWeatherRepository

    private val mockIWeather = mockk<IWeather>(relaxed = true)
    val mockContext = mockk<Context>(relaxed = true)

    @Before
    fun setUp() {

        piWeatherRepository = PiWeatherRepository(context =  mockContext,
            iWeather = mockIWeather)

    }

    @After
    fun tearDown() {
        unmockkAll()
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `validate fetch weather with city name call when network is not available`() = runTest{
        coEvery { mockContext.isNetworkAvailable() } returns false
        coEvery { mockIWeather.fetchWeather(any(), any()) } returns Response.success(WeatherResponse())
        // The first index could be loading and index 2 should be actual result
        val result = piWeatherRepository.fetchWeather("texas").take(2).toList()
        assertEquals(Resource.Status.ERROR, result[1].status)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `validate fetch weather with city name success scenario`() = runTest{
        coEvery { mockContext.isNetworkAvailable() } returns true
        coEvery { mockIWeather.fetchWeather(any(), any()) } returns Response.success(WeatherResponse())
        // The first index could be loading and index 2 should be actual result
        val result = piWeatherRepository.fetchWeather("texas").take(2).toList()
        assertEquals(Resource.Status.SUCCESS, result[1].status)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `validate fetch weather with city name 404 scenario`() = runTest{
        coEvery { mockContext.isNetworkAvailable() } returns true
        coEvery { mockIWeather.fetchWeather(any(), any()) } returns Response.error(404, mockk(relaxed = true))
        // The first index could be loading and index 2 should be actual result
        val result = piWeatherRepository.fetchWeather("texas").take(2).toList()
        assertEquals(404, result[1].error?.code)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `validate fetch weather with geo location success scenario`() = runTest{
        coEvery { mockContext.isNetworkAvailable() } returns true
        coEvery { mockIWeather.fetchWeather(any(), any(), any()) } returns Response.success(WeatherResponse())
        // The first index could be loading and index 2 should be actual result
        val result = piWeatherRepository.fetchWeather(10.0, 20.9).take(2).toList()
        assertEquals(Resource.Status.SUCCESS, result[1].status)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `validate fetch weather with city throws exception`() = runTest{
        coEvery { mockContext.isNetworkAvailable() } throws  SocketException("")
        coEvery { mockIWeather.fetchWeather(any(), any()) } returns Response.success(WeatherResponse())
        // The first index could be loading and index 2 should be actual result
        val result = piWeatherRepository.fetchWeather("texas").take(2).toList()
        assertEquals(Resource.Status.ERROR, result[1].status)
    }



}