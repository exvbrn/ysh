package com.example.yandexsmarthome.data.models
import com.google.gson.annotations.SerializedName

data class OpenMeteoResponse(val current: CurrentWeather?, val daily: DailyWeather?, val latitude: Double, val longitude: Double)
data class CurrentWeather(@SerializedName("temperature_2m") val temperature: Double, @SerializedName("weather_code") val weatherCode: Int, @SerializedName("is_day") val isDay: Int)
data class DailyWeather(val time: List<String>?, @SerializedName("weather_code") val weatherCodes: List<Int>?, val sunrise: List<String>?, val sunset: List<String>?) {
    val todaySunrise: String? get() = sunrise?.firstOrNull()
    val todaySunset: String? get() = sunset?.firstOrNull()
}