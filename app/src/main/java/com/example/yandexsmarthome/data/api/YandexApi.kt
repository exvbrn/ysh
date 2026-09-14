package com.example.yandexsmarthome.data.api

import com.example.yandexsmarthome.data.models.*
import retrofit2.http.*

interface YandexApi {
    // ЕДИНСТВЕННЫЙ правильный эндпоинт для получения данных в пользовательских приложениях
    @GET("v1.0/user/info") 
    suspend fun getUserInfo(): UserInfoResponse

    @POST("v1.0/devices/actions") 
    suspend fun controlDevice(@Body request: DeviceActionRequest): Map<String, Any>

    @POST("v1.0/scenarios/{id}/actions") 
    suspend fun runScenario(@Path("id") scenarioId: String, @Body request: Map<String, String>): Map<String, Any>
}