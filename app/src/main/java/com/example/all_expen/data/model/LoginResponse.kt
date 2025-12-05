package com.example.all_expen.data.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("token")
    val token: String,

    @SerializedName("user_id")
    val userId: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("email")
    val email: String
)