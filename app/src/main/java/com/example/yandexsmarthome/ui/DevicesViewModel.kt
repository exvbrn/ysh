package com.example.yandexsmarthome.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yandexsmarthome.data.models.Device
import com.example.yandexsmarthome.domain.YandexRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DevicesViewModel @Inject constructor(private val repository: YandexRepository) : ViewModel() {
    private val _devices = MutableStateFlow<List<Device>>(emptyList())
    val devices: StateFlow<List<Device>> = _devices
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadDevices() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.fetchDevices()
                .onSuccess { 
                    _devices.value = it 
                }
                .onFailure { 
                    _error.value = it.message ?: "Неизвестная ошибка сети" 
                }
            _isLoading.value = false
        }
    }

    fun toggleDevice(id: String, isOn: Boolean) {
        viewModelScope.launch {
            val result = repository.toggleDevice(id, isOn)
            if (result.isFailure) {
                _error.value = "Не удалось переключить устройство: ${result.exceptionOrNull()?.message}"
            }
        }
    }
}