package com.example.all_expen.ui.auth

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.all_expen.ui.theme.PrimaryBlue
import com.example.all_expen.viewmodel.LoginState
import com.example.all_expen.viewmodel.LoginViewModel
import com.example.all_expen.data.SessionManager
import com.example.all_expen.data.model.CurrentUser

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = viewModel()
) {
    val loginState by viewModel.loginState.collectAsState()
    val context = LocalContext.current
    val sessionManager = SessionManager(context)

    // Biến lưu trữ input
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Biến trạng thái hiển thị mật khẩu
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                val userId = CurrentUser.id ?: 0
                val userName = CurrentUser.name ?: "User"
                val userEmail = CurrentUser.email ?: ""

                sessionManager.saveLoginSession(userId, userName, userEmail)
                Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                navController.navigate("home_screen") {
                    popUpTo("login_screen") { inclusive = true }
                }
            }
            is LoginState.Error -> {
                val msg = (loginState as LoginState.Error).message
                // Việt hóa thông báo lỗi đăng nhập nếu cần
                val displayMsg = if(msg.contains("Sai", true) || msg.contains("Invalid", true))
                    "Sai tên đăng nhập hoặc mật khẩu" else msg
                Toast.makeText(context, displayMsg, Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(80.dp), tint = PrimaryBlue)
        Spacer(modifier = Modifier.height(30.dp))

        Text("Chào mừng trở lại", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
        Spacer(modifier = Modifier.height(30.dp))

        // Ô nhập Tên đăng nhập
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Tên đăng nhập") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.AccountCircle, null, tint = PrimaryBlue) },
            // Bàn phím thường (không bắt buộc email)
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Ô nhập Mật khẩu (Có nút mắt)
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Mật khẩu") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Lock, null, tint = PrimaryBlue) },

            // Nút ẩn/hiện mật khẩu
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Hiện mật khẩu")
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // Cắt khoảng trắng trước khi gửi
                viewModel.login(email.trim(), password.trim())
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            if (loginState is LoginState.Loading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text("ĐĂNG NHẬP")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Text("Chưa có tài khoản? ", color = Color.Gray)
            Text(
                "Đăng ký ngay",
                color = PrimaryBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { navController.navigate("register_screen") }
            )
        }
    }
}