package com.example.all_expen.data.repository

import com.example.all_expen.data.api.ApiClient // Sửa import này
import com.example.all_expen.data.model.LoginRequest
import com.example.all_expen.data.model.RegisterRequest

class UserRepository {
    // Sửa RetrofitInstance.api -> ApiClient.api
    suspend fun login(request: LoginRequest) = ApiClient.api.login(request)
    suspend fun register(request: RegisterRequest) = ApiClient.api.register(request)
}