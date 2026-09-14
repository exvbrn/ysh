package com.example.yandexsmarthome.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yandexsmarthome.data.models.Scenario
import com.example.yandexsmarthome.domain.YandexRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScenariosViewModel @Inject constructor(private val repository: YandexRepository) : ViewModel() {
    private val _scenarios = MutableStateFlow<List<Scenario>>(emptyList())
    val scenarios: StateFlow<List<Scenario>> = _scenarios
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadScenarios() {
        viewModelScope.launch {
            repository.fetchScenarios()
                .onSuccess { _scenarios.value = it; _error.value = null }
                .onFailure { _error.value = it.message }
        }
    }

    fun runScenario(id: String) {
        viewModelScope.launch {
            val result = repository.executeScenario(id)
            if (result.isFailure) {
                _error.value = "Ошибка запуска: ${result.exceptionOrNull()?.message}"
            }
        }
    }
}