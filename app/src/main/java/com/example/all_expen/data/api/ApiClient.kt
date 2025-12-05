package com.example.all_expen.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.apply
import kotlin.jvm.java

object ApiClient {
    private const val BASE_URL = "https://expense-ai-server-render.onrender.com/"

    // 1. Cấu hình bộ theo dõi Log
    private val logging = HttpLoggingInterceptor().apply {
        // Level.BODY giúp bạn nhìn thấy toàn bộ nội dung JSON trả về
        level = HttpLoggingInterceptor.Level.BODY
    }

    // 2. Cấu hình Client
    private val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .connectTimeout(30, TimeUnit.SECONDS) // Tăng thời gian chờ lên 30s (vì server Render hay ngủ)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // 3. Gắn Client vào Retrofit
    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // <--- QUAN TRỌNG: Gắn client vào đây
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}