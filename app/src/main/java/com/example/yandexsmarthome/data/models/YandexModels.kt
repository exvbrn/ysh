package com.example.yandexsmarthome.data.models

import com.google.gson.annotations.SerializedName

// Корневой ответ от /v1.0/user/info
data class UserInfoResponse(
    val status: String,
    @SerializedName("request_id") val requestId: String,
    val devices: List<Device>,
    val scenarios: List<Scenario>,
    val rooms: List<Room>? = null,
    val groups: List<Group>? = null
)

data class Device(
    val id: String,
    val name: String,
    val room: String?, // В этом эндпоинте приходит ID комнаты, для простоты пока выводим как есть
    val type: String,
    val capabilities: List<Capability>?
) {
    val hasOnOff: Boolean 
        get() = capabilities?.any { it.type == "devices.capabilities.on_off" } == true
}

data class Capability(
    val retrievable: Boolean,
    val type: String,
    val state: CapabilityState?
)

data class CapabilityState(
    val instance: String,
    val value: Any?
)

data class Scenario(
    val id: String,
    val name: String,
    @SerializedName("is_active") val isActive: Boolean
)

data class Room(
    val id: String,
    val name: String
)

data class Group(
    val id: String,
    val name: String,
    val type: String
)

// --- Модели для отправки действий (без изменений) ---
data class DeviceActionRequest(val devices: List<DeviceAction>)
data class DeviceAction(val id: String, val actions: List<Action>)
data class Action(val type: String, val state: ActionState)
data class ActionState(val instance: String, val value: Boolean)