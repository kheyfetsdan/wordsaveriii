package com.example.mysimpleapp.data.api

import android.content.Context
import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    // Константы для разных окружений
    private const val EMULATOR_URL = "http://10.0.2.2:8080"
    private const val DEVICE_URL = "http://192.168.31.182:8080"
    private const val PROD_URL = "http://wordsaveriii-e42ab50fe156.herokuapp.com"
    
    // Флаг для переключения между окружениями
    private var useEmulatorUrl = false
    
    // Таймауты для запросов
    private const val CONNECT_TIMEOUT = 15L
    private const val READ_TIMEOUT = 30L
    private const val WRITE_TIMEOUT = 15L
    
    private lateinit var sharedPreferences: SharedPreferences
    
    // Перехватчик для логирования
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    // Перехватчик для добавления токена авторизации
    private val authInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        
        // Если запрос уже содержит заголовок Authorization, используем его
        if (originalRequest.header("Authorization") != null) {
            return@Interceptor chain.proceed(originalRequest)
        }
        
        // Проверяем, инициализированы ли SharedPreferences
        if (!::sharedPreferences.isInitialized) {
            // Если нет, просто продолжаем с оригинальным запросом
            return@Interceptor chain.proceed(originalRequest)
        }
        
        // Иначе пытаемся добавить токен из SharedPreferences
        val token = sharedPreferences.getString("auth_token", null)
        val request = if (token != null) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        } else {
            originalRequest
        }
        
        chain.proceed(request)
    }
    
    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }
    
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(if (useEmulatorUrl) EMULATOR_URL else PROD_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
    
    // Инициализация клиента
    fun init(context: Context) {
        if (!::sharedPreferences.isInitialized) {
            sharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
        }
    }
    
    // Метод для переключения URL
    fun setUseEmulatorUrl(useEmulator: Boolean) {
        useEmulatorUrl = useEmulator
    }
} 