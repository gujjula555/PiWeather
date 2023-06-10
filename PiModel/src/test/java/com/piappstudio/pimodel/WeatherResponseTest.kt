package com.piappstudio.pimodel

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test


class WeatherResponseTest {

    private val mockResponse = "{\"coord\":{\"lon\":-96.9667,\"lat\":32.9011},\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03d\"}],\"base\":\"stations\",\"main\":{\"temp\":308.38,\"feels_like\":310.49,\"temp_min\":306.25,\"temp_max\":310.16,\"pressure\":1009,\"humidity\":39},\"visibility\":10000,\"wind\":{\"speed\":3.09,\"deg\":0},\"clouds\":{\"all\":40},\"dt\":1686347641,\"sys\":{\"type\":2,\"id\":2007216,\"country\":\"US\",\"sunrise\":1686309552,\"sunset\":1686360913},\"timezone\":-18000,\"id\":4690198,\"name\":\"Farmers Branch\",\"cod\":200}"

    @Test
    fun `validate json parsing`() {
        val weatherResponse = Gson().fromJson(mockResponse, WeatherResponse::class.java)
        assertNotNull(weatherResponse)
    }

    @Test
    fun `validate icon url`() {
        val weatherResponse = WeatherItem(icon = "024")
        assertEquals("${Constant.ICON_URL}024@2x.png", weatherResponse.iconUrl())
    }

    @Test
    fun `validate to fahrenheit`() {
        val fahrenheit = 234.9.toFahrenheit()
        assertEquals("-36 ℉", fahrenheit)
    }
}