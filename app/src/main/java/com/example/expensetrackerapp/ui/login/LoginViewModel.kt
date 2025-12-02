package com.example.expensetrackerapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetrackerapp.data.model.LoginRequest
import com.example.expensetrackerapp.data.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

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
                // Gọi API
                val response = RetrofitInstance.api.login(LoginRequest(email, pass))

                if (response.isSuccessful) {
                    // Ở đây bạn nên lưu Token nếu API trả về (sẽ làm sau)
                    _loginState.value = LoginState.Success
                } else {
                    _loginState.value = LoginState.Error("Login failed: Check email or password")
                }
            } catch (e: IOException) {
                _loginState.value = LoginState.Error("No internet connection")
            } catch (e: Exception) {
                _loginState.value = LoginState.Error("Error: ${e.message}")
            }
        }
    }
}