package com.example.expensetrackerapp.ui.theme

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// 1. Định nghĩa API Interface
interface ExpenseApiService {
    // Đường dẫn khớp với server Node.js: app.get('/api/transactions')
    @GET("api/transactions")
    suspend fun getTransactions(): List<ExpenseTransaction>
}

// 2. Tạo Singleton để gọi API
object RetrofitClient {
    // LƯU Ý QUAN TRỌNG VỀ ĐỊA CHỈ IP:
    // - Nếu chạy trên Máy ảo Android (Emulator): Dùng "10.0.2.2"
    // - Nếu chạy trên Điện thoại thật: Dùng IP máy tính (VD: "192.168.1.X")
    private const val BASE_URL = "http://10.0.2.2:3000/"

    val apiService: ExpenseApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create()) // Tự đổi JSON sang Object
            .build()
            .create(ExpenseApiService::class.java)
    }
}