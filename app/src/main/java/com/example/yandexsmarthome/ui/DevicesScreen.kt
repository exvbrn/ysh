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
import com.example.yandexsmarthome.data.models.Device
import kotlinx.coroutines.delay

@Composable
fun DevicesScreen(viewModel: DevicesViewModel = hiltViewModel()) {
    val devices by viewModel.devices.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) { 
        viewModel.loadDevices()
        while (true) { 
            delay(30000)
            viewModel.loadDevices() 
        } 
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        when {
            isLoading && devices.isEmpty() -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            error != null -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Ошибка", color = MaterialTheme.colorScheme.error)
                }
            }
            devices.isEmpty() -> {
                // Минималистичный пустой экран без лишних инструкций
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Список пуст", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            else -> {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    val grouped = devices.groupBy { it.room ?: "Без комнаты" }
                    grouped.forEach { (room, roomDevices) ->
                        item { 
                            Text(text = room, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(vertical = 8.dp)) 
                        }
                        items(roomDevices) { device -> 
                            DeviceCard(device = device, onToggle = { viewModel.toggleDevice(device.id, it) }) 
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DeviceCard(device: Device, onToggle: (Boolean) -> Unit) {
    var isChecked by remember { mutableStateOf(device.capabilities?.firstOrNull { it.type == "devices.capabilities.on_off" }?.state?.value == true) }
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) { 
                Text(text = device.name, style = MaterialTheme.typography.bodyLarge)
                Text(text = if (isChecked) "Включено" else "Выключено", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) 
            }
            Switch(checked = isChecked, onCheckedChange = { newState -> 
                isChecked = newState
                onToggle(newState) 
            })
        }
    }
}