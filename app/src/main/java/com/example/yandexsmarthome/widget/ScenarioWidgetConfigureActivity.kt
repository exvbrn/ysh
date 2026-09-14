package com.example.yandexsmarthome.widget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.yandexsmarthome.data.local.TokenManager
import com.example.yandexsmarthome.domain.YandexRepository
import com.example.yandexsmarthome.ui.theme.YandexSmartHomeTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ScenarioWidgetConfigureActivity : ComponentActivity() {
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
                    ConfigureScenarioContent(appWidgetId, repository, tokenManager, this)
                }
            }
        }
    }
}

@Composable
fun ConfigureScenarioContent(
    appWidgetId: Int,
    repository: YandexRepository,
    tokenManager: TokenManager,
    activity: ScenarioWidgetConfigureActivity
) {
    val context = LocalContext.current
    var scenarios by remember { mutableStateOf<List<com.example.yandexsmarthome.data.models.Scenario>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        if (tokenManager.getToken() != null) {
            repository.fetchScenarios().onSuccess {
                scenarios = it
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
        Text("Выберите сценарий", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))

        when {
            isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            errorMessage != null -> Text(errorMessage!!, color = MaterialTheme.colorScheme.error)
            scenarios.isEmpty() -> Text("Сценарии не найдены", color = MaterialTheme.colorScheme.onSurfaceVariant)
            else -> {
                LazyColumn {
                    items(scenarios) { scenario ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    // 1. Сохраняем ID сценария
                                    val prefs = activity.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
                                    prefs.edit()
                                        .putString("scenario_id_$appWidgetId", scenario.id)
                                        .apply()

                                    // 2. Виброотклик при выборе (используем современный типобезопасный API)
                                    val vibrator = context.getSystemService(Vibrator::class.java)
                                    if (vibrator != null) {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
                                        } else {
                                            @Suppress("DEPRECATION")
                                            vibrator.vibrate(50)
                                        }
                                    }

                                    // 3. Стандартное завершение настройки виджета
                                    val resultValue = Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                                    activity.setResult(Activity.RESULT_OK, resultValue)
                                    activity.finish()
                                }
                        ) {
                            Text(text = scenario.name, modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            }
        }
    }
}