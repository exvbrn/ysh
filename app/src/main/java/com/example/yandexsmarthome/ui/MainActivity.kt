package com.example.yandexsmarthome.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp // <-- ДОБАВЛЕН ЭТОТ ИМПОРТ
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.yandexsmarthome.ui.theme.YandexSmartHomeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YandexSmartHomeTheme {
                Surface(color = MaterialTheme.colorScheme.background) { 
                    AppNavigation() 
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "main") {
        composable("main") { MainScreen(navController) }
        composable("settings") { SettingsScreen(navController) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(navController: androidx.navigation.NavController) {
    var selectedTab by remember { mutableStateOf(0) }
    
    Scaffold(
        topBar = { 
            TopAppBar(
                title = { Text("Умный Дом") }, 
                actions = { 
                    IconButton(onClick = { navController.navigate("settings") }) { 
                        Icon(Icons.Default.Settings, "Настройки") 
                    } 
                }
            ) 
        },
        bottomBar = { 
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0, 
                    onClick = { selectedTab = 0 }, 
                    icon = { Icon(Icons.Default.Home, "Устройства") }, 
                    label = { Text("Устройства") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1, 
                    onClick = { selectedTab = 1 }, 
                    icon = { Icon(Icons.Default.PlayArrow, "Сценарии") }, 
                    label = { Text("Сценарии") }
                )
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            WeatherCard(modifier = Modifier.padding(16.dp))
            when (selectedTab) { 
                0 -> DevicesScreen()
                1 -> ScenariosScreen() 
            }
        }
    }
}