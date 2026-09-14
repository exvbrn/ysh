package com.example.yandexsmarthome.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.yandexsmarthome.R
import com.example.yandexsmarthome.domain.YandexRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ScenarioWidgetReceiver : AppWidgetProvider() {
    @Inject lateinit var repository: YandexRepository

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (id in appWidgetIds) updateAppWidget(context, appWidgetManager, id)
    }

    private fun updateAppWidget(context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int) {
        val prefs = context.getSharedPreferences("widget_prefs", 0)
        if (!prefs.contains("scenario_id_$appWidgetId")) return

        val views = RemoteViews(context.packageName, R.layout.widget_scenario)

        val intent = Intent(context, ScenarioWidgetReceiver::class.java).apply {
            action = "com.example.yandexsmarthome.ACTION_RUN_SCENARIO"
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }
        val pi = PendingIntent.getBroadcast(
            context, appWidgetId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.setOnClickPendingIntent(R.id.widget_root, pi)

        appWidgetManager.updateAppWidget(appWidgetId, views)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action == "com.example.yandexsmarthome.ACTION_RUN_SCENARIO") {
            val appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
            if (appWidgetId != -1) {
                CoroutineScope(Dispatchers.IO).launch {
                    val prefs = context.getSharedPreferences("widget_prefs", 0)
                    val scenarioId = prefs.getString("scenario_id_$appWidgetId", null) ?: return@launch
                    repository.executeScenario(scenarioId)
                }
            }
        }
    }
}