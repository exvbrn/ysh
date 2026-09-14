package com.example.yandexsmarthome.domain

import com.example.yandexsmarthome.data.api.YandexApi
import com.example.yandexsmarthome.data.local.TokenManager
import com.example.yandexsmarthome.data.models.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YandexRepository @Inject constructor(
    private val api: YandexApi, 
    private val tokenManager: TokenManager
) {
    suspend fun fetchDevices(): Result<List<Device>> = try { 
        val response = api.getUserInfo()
        if (response.status == "ok") {
            // 1. Создаём словарь (мапу): ID комнаты -> Название комнаты
            val roomNamesMap = response.rooms?.associate { it.id to it.name } ?: emptyMap()
            
            // 2. Заменяем ID комнаты на её реальное название в каждом устройстве
            val mappedDevices = response.devices.map { device ->
                device.copy(
                    room = roomNamesMap[device.room] ?: device.room ?: "Без комнаты"
                )
            }
            
            Result.success(mappedDevices) 
        } else {
            Result.failure(Exception("API вернул статус: ${response.status}"))
        }
    } catch (e: Exception) { 
        Result.failure(e) 
    }

    suspend fun fetchScenarios(): Result<List<Scenario>> = try { 
        val response = api.getUserInfo()
        if (response.status == "ok") {
            Result.success(response.scenarios) 
        } else {
            Result.failure(Exception("API вернул статус: ${response.status}"))
        }
    } catch (e: Exception) { 
        Result.failure(e) 
    }

    suspend fun toggleDevice(deviceId: String, isOn: Boolean): Result<Unit> = try {
        val request = com.example.yandexsmarthome.data.models.DeviceActionRequest(
            devices = listOf(
                com.example.yandexsmarthome.data.models.DeviceAction(
                    id = deviceId,
                    actions = listOf(
                        com.example.yandexsmarthome.data.models.Action(
                            type = "devices.capabilities.on_off",
                            state = com.example.yandexsmarthome.data.models.ActionState(instance = "on", value = isOn)
                        )
                    )
                )
            )
        )
        api.controlDevice(request)
        Result.success(Unit)
    } catch (e: Exception) { 
        Result.failure(e) 
    }

    suspend fun executeScenario(scenarioId: String): Result<Unit> = try { 
        api.runScenario(scenarioId, mapOf("name" to "execute"))
        Result.success(Unit) 
    } catch (e: Exception) { 
        Result.failure(e) 
    }

    fun isTokenValid(): Boolean = !tokenManager.getToken().isNullOrBlank()
}