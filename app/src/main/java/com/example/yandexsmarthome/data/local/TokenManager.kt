package com.example.yandexsmarthome.data.local
import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(context: Context) {
    private val masterKey = MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
    private val sharedPreferences = EncryptedSharedPreferences.create(context, "secure_prefs", masterKey, EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV, EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
    fun saveToken(token: String) { sharedPreferences.edit().putString("oauth_token", token).apply() }
    fun getToken(): String? = sharedPreferences.getString("oauth_token", null)
    fun clearToken() { sharedPreferences.edit().remove("oauth_token").apply() }
}