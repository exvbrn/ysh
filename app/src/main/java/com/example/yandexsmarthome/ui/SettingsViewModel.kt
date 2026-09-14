package com.example.yandexsmarthome.ui
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yandexsmarthome.data.local.TokenManager
import com.example.yandexsmarthome.domain.YandexRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val repository: YandexRepository, private val tokenManager: TokenManager) : ViewModel() {
    fun getToken() = tokenManager.getToken()
    fun saveToken(token: String) { tokenManager.saveToken(token) }
    fun clearToken() { tokenManager.clearToken() }
    fun checkConnection(callback: (Boolean) -> Unit) { viewModelScope.launch { callback(repository.isTokenValid()) } }
}