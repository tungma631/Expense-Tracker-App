package com.example.expensetrackerapp.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.expensetrackerapp.data.network.RetrofitInstance
import com.example.expensetrackerapp.data.model.RegisterRequest // Đảm bảo bạn đã tạo model này
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import retrofit2.HttpException

// Định nghĩa các trạng thái của màn hình
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
                // --- KẾT NỐI API Ở ĐÂY ---
                // Giả sử API của bạn trả về success code 200
                val response = RetrofitInstance.api.register(RegisterRequest(name, email, pass))

                if (response.isSuccessful) {
                    _registerState.value = RegisterState.Success
                } else {

                    val errorBody = response.errorBody()?.string() ?: "Registration failed"
                    _registerState.value = RegisterState.Error(errorBody)
                }

            } catch (e: IOException) {

                _registerState.value = RegisterState.Error("No internet connection. Please check your network.")

            } catch (e: HttpException) {

                _registerState.value = RegisterState.Error("Server error: ${e.message}")

            } catch (e: Exception) {

                _registerState.value = RegisterState.Error("An unknown error occurred: ${e.localizedMessage}")
            }
        }
    }
}