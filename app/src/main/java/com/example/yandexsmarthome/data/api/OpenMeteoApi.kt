package com.example.yandexsmarthome.data.api
import com.example.yandexsmarthome.data.models.OpenMeteoResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenMeteoApi {
    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") latitude: Double, @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,weather_code,is_day",
        @Query("daily") daily: String = "weather_code,sunrise,sunset",
        @Query("timezone") timezone: String = "auto", @Query("forecast_days") forecastDays: Int = 1
    ): OpenMeteoResponse
}