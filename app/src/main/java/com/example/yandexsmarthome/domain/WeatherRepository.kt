package com.example.yandexsmarthome.domain
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.example.yandexsmarthome.data.api.OpenMeteoApi
import com.example.yandexsmarthome.data.models.OpenMeteoResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class WeatherRepository @Inject constructor(private val api: OpenMeteoApi, @ApplicationContext private val context: Context) {
    suspend fun getCurrentLocation(): Pair<Double, Double> = suspendCancellableCoroutine { cont ->
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            cont.resume(55.7558 to 37.6173); return@suspendCancellableCoroutine
        }
        val lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var lastLoc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) ?: lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        cont.resume(if (lastLoc != null) lastLoc.latitude to lastLoc.longitude else 55.7558 to 37.6173)
    }
    suspend fun getWeather(): Result<OpenMeteoResponse> = try {
        val (lat, lon) = getCurrentLocation()
        Result.success(api.getWeather(lat, lon))
    } catch (e: Exception) { Result.failure(e) }
}