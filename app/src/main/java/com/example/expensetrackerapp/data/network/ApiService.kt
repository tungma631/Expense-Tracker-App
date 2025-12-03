package com.example.expensetrackerapp.data.network

import com.example.expensetrackerapp.data.model.LoginRequest
import com.example.expensetrackerapp.data.model.LoginResponse
import com.example.expensetrackerapp.data.model.RegisterRequest
import com.example.expensetrackerapp.data.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("/")
    suspend fun wakeUpServer(): Response<Any>
    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}