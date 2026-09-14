package com.example.yandexsmarthome.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ScenariosScreen(viewModel: ScenariosViewModel = hiltViewModel()) {
    val scenarios by viewModel.scenarios.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) { viewModel.loadScenarios() }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        when {
            error != null -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxSize()) {
                    Text("Ошибка: $error", color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { viewModel.loadScenarios() }) { Text("Повторить") }
                }
            }
            scenarios.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Сценарии не найдены.\nСоздайте их в приложении Яндекс.", style = MaterialTheme.typography.bodyLarge)
                }
            }
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(scenarios) { scenario ->
                        Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                            Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text(text = scenario.name, modifier = Modifier.weight(1f))
                                Button(onClick = { viewModel.runScenario(scenario.id) }) { Text("Запустить") }
                            }
                        }
                    }
                }
            }
        }
    }
}