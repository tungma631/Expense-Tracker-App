package com.example.all_expen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.all_expen.data.api.ApiClient
import com.example.all_expen.data.model.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.all_expen.data.model.CurrentUser

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val response = ApiClient.api.login(LoginRequest(email, pass))
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    // 🔥 LƯU THÔNG TIN USER VÀO BỘ NHỚ TẠM
                    CurrentUser.id = body.userId
                    CurrentUser.name = body.name
                    CurrentUser.email = body.email

                    _loginState.value = LoginState.Success
                } else {
                    _loginState.value = LoginState.Error("Sai tên đăng nhập hoặc mật khẩu")
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Lỗi kết nối: ${e.message}")
            }
        }
    }
}