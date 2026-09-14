package com.example.yandexsmarthome.ui
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yandexsmarthome.data.models.OpenMeteoResponse
import com.example.yandexsmarthome.domain.WeatherRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewModel @Inject constructor(private val repository: WeatherRepository) : ViewModel() {
    private val _weather = MutableStateFlow<OpenMeteoResponse?>(null)
    val weather: StateFlow<OpenMeteoResponse?> = _weather
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    fun loadWeather() { viewModelScope.launch { _isLoading.value = true; _error.value = null; repository.getWeather().onSuccess { _weather.value = it }.onFailure { _error.value = it.message }; _isLoading.value = false } }
}