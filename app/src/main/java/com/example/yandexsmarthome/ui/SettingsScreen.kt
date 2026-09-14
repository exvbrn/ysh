package com.example.yandexsmarthome.ui
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun SettingsScreen(navController: androidx.navigation.NavController, viewModel: SettingsViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var token by remember { mutableStateOf(viewModel.getToken() ?: "") }
    var isTokenVisible by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    Column(modifier = Modifier.padding(16.dp).fillMaxSize()) {
        OutlinedTextField(value = token, onValueChange = { token = it }, label = { Text("OAuth Токен") }, visualTransformation = if (isTokenVisible) androidx.compose.ui.text.input.VisualTransformation.None else androidx.compose.ui.text.input.PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), trailingIcon = { IconButton(onClick = { isTokenVisible = !isTokenVisible }) { Text(if (isTokenVisible) "Скрыть" else "Показать") } })
        TextButton(onClick = { context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://oauth.yandex.ru"))) }) { Text("Где взять токен?") }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = { viewModel.saveToken(token); viewModel.checkConnection { success -> message = if (success) "Успешно!" else "Ошибка" } }, modifier = Modifier.fillMaxWidth()) { Text("Сохранить и проверить") }
        if (message.isNotEmpty()) { Text(text = message, modifier = Modifier.padding(top = 8.dp)) }
        Spacer(modifier = Modifier.weight(1f))
        OutlinedButton(onClick = { viewModel.clearToken(); navController.popBackStack() }, modifier = Modifier.fillMaxWidth()) { Text("Выйти", color = MaterialTheme.colorScheme.error) }
    }
}