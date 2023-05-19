package com.piappstudio.pinetwork

import com.piappstudio.pimodel.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface IWeather {
    @GET("weather")
    /** To get weather information based on city*/
    suspend fun fetchWeather(@Query("q") cityName:String, @Query("appid") appId:String): Response<WeatherResponse?>
    @GET("weather")
    /** To get weather information based on city*/
    suspend fun fetchWeather(@Query("lat") lat:Double, @Query("lon") long:Double, @Query("appid") appId:String): Response<WeatherResponse?>
}