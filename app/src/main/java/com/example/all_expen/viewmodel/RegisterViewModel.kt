package com.example.all_expen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.all_expen.data.api.ApiClient
import com.example.all_expen.data.model.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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
                val response = ApiClient.api.register(RegisterRequest(name, email, pass))
                if (response.isSuccessful) {
                    _registerState.value = RegisterState.Success
                } else {
                    // --- ĐOẠN SỬA LẠI: Lấy thông báo lỗi từ Server ---
                    val errorBody = response.errorBody()?.string()
                    // Nếu server trả về JSON lỗi, ta hiển thị nó, hoặc hiển thị mã lỗi (VD: 400, 500)
                    val errorMessage = errorBody ?: "Lỗi server: ${response.code()}"
                    _registerState.value = RegisterState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _registerState.value = RegisterState.Error("Lỗi kết nối: ${e.message}")
            }
        }
    }
}