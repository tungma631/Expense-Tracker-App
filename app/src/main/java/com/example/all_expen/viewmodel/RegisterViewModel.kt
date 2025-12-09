package com.example.all_expen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.all_expen.data.api.ApiClient
import com.example.all_expen.data.model.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

sealed class RegisterState {
    object Idle : RegisterState()
    object Loading : RegisterState()
    object Success : RegisterState()
    data class Error(val message: String) : RegisterState()
}

class RegisterViewModel : ViewModel() {
    private val _registerState = MutableStateFlow<RegisterState>(RegisterState.Idle)
    val registerState: StateFlow<RegisterState> = _registerState

    fun register(name: String, email: String, pass: String) {
        viewModelScope.launch {
            _registerState.value = RegisterState.Loading
            try {
                // Gửi request (biến 'email' chứa username người dùng nhập)
                val response = ApiClient.api.register(RegisterRequest(name, email, pass))

                if (response.isSuccessful) {
                    _registerState.value = RegisterState.Success
                } else {
                    // --- XỬ LÝ LỖI ---
                    val errorBody = response.errorBody()?.string()
                    var rawMessage = "Đăng ký thất bại: ${response.code()}"

                    // 1. Cố gắng đọc nội dung JSON từ server
                    if (errorBody != null) {
                        try {
                            val jsonObject = JSONObject(errorBody)
                            if (jsonObject.has("message")) {
                                rawMessage = jsonObject.getString("message")
                            }
                        } catch (e: Exception) {
                            rawMessage = errorBody // Nếu không phải JSON thì lấy text gốc
                        }
                    }

                    // 2. Dịch thông báo sang tiếng Việt & đổi "Email" thành "Tên đăng nhập"
                    val displayMessage = if (
                        rawMessage.contains("email", ignoreCase = true) ||
                        rawMessage.contains("exists", ignoreCase = true) ||
                        rawMessage.contains("tồn tại", ignoreCase = true)
                    ) {
                        "Tên đăng nhập này đã tồn tại. Vui lòng chọn tên khác!"
                    } else {
                        rawMessage // Các lỗi khác giữ nguyên
                    }

                    _registerState.value = RegisterState.Error(displayMessage)
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("Lỗi kết nối: ${e.message}")
            }
        }
    }
}