package com.example.yandexsmarthome.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.yandexsmarthome.data.local.TokenManager
import com.example.yandexsmarthome.domain.YandexRepository
import com.example.yandexsmarthome.ui.theme.YandexSmartHomeTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WidgetConfigureActivity : ComponentActivity() {
    @Inject lateinit var repository: YandexRepository
    @Inject lateinit var tokenManager: TokenManager
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appWidgetId = intent?.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
            ?: AppWidgetManager.INVALID_APPWIDGET_ID

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        setContent {
            YandexSmartHomeTheme {
                Surface {
                    ConfigureWidgetContent(appWidgetId, repository, tokenManager, this)
                }
            }
        }
    }
}

@Composable
fun ConfigureWidgetContent(
    appWidgetId: Int,
    repository: YandexRepository,
    tokenManager: TokenManager,
    activity: WidgetConfigureActivity
) {
    var devices by remember { mutableStateOf<List<com.example.yandexsmarthome.data.models.Device>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        if (tokenManager.getToken() != null) {
            repository.fetchDevices().onSuccess {
                devices = it.filter { d -> d.hasOnOff }
                isLoading = false
            }.onFailure {
                errorMessage = it.message ?: "Неизвестная ошибка"
                isLoading = false
            }
        } else {
            errorMessage = "Токен не настроен. Зайдите в настройки приложения."
            isLoading = false
        }
    }

    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        Text("Выберите устройство", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))

        when {
            isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            errorMessage != null -> Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
            devices.isEmpty() -> Text("Нет устройств с функцией Вкл/Выкл", color = MaterialTheme.colorScheme.onSurfaceVariant)
            else -> {
                LazyColumn {
                    items(devices) { device ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    // 1. Сохраняем данные
                                    val prefs = activity.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
                                    prefs.edit()
                                        .putString("device_id_$appWidgetId", device.id)
                                        .putBoolean("device_state_$appWidgetId", false) // По умолчанию выключено (серая иконка)
                                        .apply()

                                    // 2. Стандартный способ завершения настройки виджета в Android
                                    val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                                    activity.setResult(Activity.RESULT_OK, resultValue)
                                    
                                    // 3. Закрываем экран. Система автоматически вызовет onUpdate в Receiver
                                    activity.finish()
                                }
                        ) {
                            Text(text = device.name, modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }
        }
    }
}