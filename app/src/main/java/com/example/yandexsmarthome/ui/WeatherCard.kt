package com.example.yandexsmarthome.ui
import android.Manifest
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.yandexsmarthome.data.utils.WeatherCodeMapper
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun WeatherCard(modifier: Modifier = Modifier, viewModel: WeatherViewModel = hiltViewModel()) {
    val locationPermissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
    val weather by viewModel.weather.collectAsState()
    val error by viewModel.error.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    LaunchedEffect(locationPermissionState.status.isGranted) { if (locationPermissionState.status.isGranted) viewModel.loadWeather() }
    Card(modifier = modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, d MMMM", Locale("ru"))).replaceFirstChar { it.uppercase() }, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))
            when {
                !locationPermissionState.status.isGranted -> { Row(verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.LocationOff, null); Spacer(Modifier.width(8.dp)); Column { Text("Нужна геолокация"); TextButton(onClick = { locationPermissionState.launchPermissionRequest() }) { Text("Разрешить") } } } }
                isLoading && weather == null -> CircularProgressIndicator()
                error != null && weather == null -> { Text("Ошибка: $error", color = MaterialTheme.colorScheme.error); TextButton(onClick = { viewModel.loadWeather() }) { Text("Повторить") } }
                weather != null -> {
                    val current = weather!!.current; val daily = weather!!.daily; val info = WeatherCodeMapper.getInfo(current?.weatherCode ?: 0, current?.isDay == 1)
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) { Icon(info.icon, info.description, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary); Spacer(Modifier.width(12.dp)); Column { Text("${current?.temperature?.toInt() ?: 0}°C", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold); Text(info.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) } }
                    Spacer(Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth()) { Column(modifier = Modifier.weight(1f)) { Text("Рассвет", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant); Text(WeatherCodeMapper.formatTime(daily?.todaySunrise), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium) }; Column(modifier = Modifier.weight(1f)) { Text("Закат", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.End); Text(WeatherCodeMapper.formatTime(daily?.todaySunset), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, modifier = Modifier.fillMaxWidth(), textAlign = androidx.compose.ui.text.style.TextAlign.End) } }
                }
            }
        }
    }
}