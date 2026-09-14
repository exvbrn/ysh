package com.example.yandexsmarthome.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.widget.RemoteViews
import com.example.yandexsmarthome.R
import com.example.yandexsmarthome.domain.YandexRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DeviceWidgetReceiver : AppWidgetProvider() {
    @Inject lateinit var repository: YandexRepository

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) updateAppWidget(context, appWidgetManager, id)
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val prefs = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
        // Если данных нет, просто выходим (виджет будет пустым, но не крашнется)
        if (!prefs.contains("device_id_$appWidgetId")) return

        val views = android.widget.RemoteViews(context.packageName, com.example.yandexsmarthome.R.layout.widget_device)
        val isOn = prefs.getBoolean("device_state_$appWidgetId", false)

        val yellowColor = android.graphics.Color.parseColor("#FFCC00")
        views.setInt(com.example.yandexsmarthome.R.id.widget_icon, "setColorFilter", if (isOn) yellowColor else android.graphics.Color.GRAY)

        val intent = Intent(context, DeviceWidgetReceiver::class.java).apply {
            action = "com.example.yandexsmarthome.ACTION_TOGGLE_DEVICE"
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        val pi = android.app.PendingIntent.getBroadcast(
            context, appWidgetId, intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(com.example.yandexsmarthome.R.id.widget_root, pi)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == "com.example.yandexsmarthome.ACTION_TOGGLE_DEVICE") {
            val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
            if (appWidgetId != -1) {
                CoroutineScope(Dispatchers.IO).launch {
                    val prefs = context.getSharedPreferences("widget_prefs", 0)
                    val deviceId = prefs.getString("device_id_$appWidgetId", null) ?: return@launch
                    val newState = !prefs.getBoolean("device_state_$appWidgetId", false)

                    repository.toggleDevice(deviceId, newState).onSuccess {
                        prefs.edit().putBoolean("device_state_$appWidgetId", newState).apply()
                        updateAppWidget(context, AppWidgetManager.getInstance(context), appWidgetId)
                    }
                }
            }
        }
    }
}