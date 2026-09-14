package com.example.yandexsmarthome.di
import android.content.Context
import com.example.yandexsmarthome.data.api.OpenMeteoApi
import com.example.yandexsmarthome.data.api.YandexApi
import com.example.yandexsmarthome.data.local.TokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides @Singleton fun provideTokenManager(@ApplicationContext context: Context) = TokenManager(context)
    @Provides @Singleton fun provideYandexApi(tokenManager: TokenManager): YandexApi {
        val client = OkHttpClient.Builder().addInterceptor { chain ->
            val req = chain.request().newBuilder()
            tokenManager.getToken()?.let { req.header("Authorization", "OAuth $it") }
            chain.proceed(req.build())
        }.addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }).build()
        return Retrofit.Builder().baseUrl("https://api.iot.yandex.net/").client(client).addConverterFactory(GsonConverterFactory.create()).build().create(YandexApi::class.java)
    }
    @Provides @Singleton fun provideOpenMeteoApi(): OpenMeteoApi = Retrofit.Builder().baseUrl("https://api.open-meteo.com/").addConverterFactory(GsonConverterFactory.create()).build().create(OpenMeteoApi::class.java)
}