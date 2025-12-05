package com.example.all_expen.data.api

import com.example.all_expen.data.model.AnalyzeResponse
import com.example.all_expen.data.model.LoginRequest
import com.example.all_expen.data.model.LoginResponse
import com.example.all_expen.data.model.QueryResponse
import com.example.all_expen.data.model.RegisterRequest
import com.example.all_expen.data.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("analyze")
    suspend fun analyze(@Body body: Map<String, String>): AnalyzeResponse

    @POST("/query")
    suspend fun query(@Body data: Map<String, String>): QueryResponse
    // Nếu bạn đã có các API khác thì giữ lại hết, chỉ cần thêm 2 hàm này.
    @GET("/")
    suspend fun wakeUpServer(): Response<Any>
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>
}