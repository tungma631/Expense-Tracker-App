package com.example.expensetrackerapp.data.repository

import com.example.expensetrackerapp.data.model.LoginRequest
import com.example.expensetrackerapp.data.model.RegisterRequest
import com.example.expensetrackerapp.data.network.RetrofitInstance

class UserRepository {

    // Khởi tạo API từ RetrofitInstance
    private val apiService = RetrofitInstance.api

    // Hàm login gọi lên server
    suspend fun login(loginRequest: LoginRequest) = apiService.login(loginRequest)

    // Hàm register gọi lên server
    suspend fun register(registerRequest: RegisterRequest) = apiService.register(registerRequest)
}