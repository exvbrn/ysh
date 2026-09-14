package com.example.yandexsmarthome.data.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

object WeatherCodeMapper {
    data class WeatherInfo(val description: String, val icon: ImageVector)

    fun getInfo(code: Int, isDay: Boolean = true): WeatherInfo = when (code) {
        0 -> WeatherInfo("Ясно", if (isDay) Icons.Default.WbSunny else Icons.Default.Bedtime)
        1, 2 -> WeatherInfo("Малооблачно", Icons.Default.WbCloudy)
        3 -> WeatherInfo("Пасмурно", Icons.Default.Cloud)
        45, 48 -> WeatherInfo("Туман", Icons.Default.Cloud)
        in 51..57 -> WeatherInfo("Морось", Icons.Default.WaterDrop)
        in 61..67 -> WeatherInfo("Дождь", Icons.Default.WaterDrop) // Исправлено: Rainy -> WaterDrop
        in 71..77 -> WeatherInfo("Снег", Icons.Default.AcUnit)
        in 80..82 -> WeatherInfo("Ливень", Icons.Default.Thunderstorm)
        95, 96, 99 -> WeatherInfo("Гроза", Icons.Default.FlashOn)
        else -> WeatherInfo("Неизвестно", Icons.Default.Help)
    }

    fun formatTime(isoTime: String?): String {
        if (isoTime.isNullOrBlank()) return "--:--"
        return try {
            isoTime.substringAfter("T").substring(0, 5)
        } catch (e: Exception) {
            "--:--"
        }
    }
}