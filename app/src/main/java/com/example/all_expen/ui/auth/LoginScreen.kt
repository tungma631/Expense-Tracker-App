package com.example.all_expen.ui.auth

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
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
    val sessionManager = SessionManager(context) // Khởi tạo

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(loginState) {
        when (loginState) {
            is LoginState.Success -> {
                // 🔥 SỬA ĐOẠN NÀY:
                // Lấy thông tin từ CurrentUser (đã được ViewModel cập nhật)
                val userId = CurrentUser.id ?: 0
                val userName = CurrentUser.name ?: "User"
                val userEmail = CurrentUser.email ?: ""

                // Lưu ID vào bộ nhớ máy vĩnh viễn
                sessionManager.saveLoginSession(userId, userName, userEmail)

                Toast.makeText(context, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                navController.navigate("home_screen") {
                    popUpTo("login_screen") { inclusive = true }
                }
            }
            is LoginState.Error -> {
                Toast.makeText(context, (loginState as LoginState.Error).message, Toast.LENGTH_SHORT).show()
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
        Text("Welcome Back", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            value = email, onValueChange = { email = it },
            label = { Text("Email") }, modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Email, null, tint = PrimaryBlue) }
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text("Password") }, modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            leadingIcon = { Icon(Icons.Default.Lock, null, tint = PrimaryBlue) }
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { viewModel.login(email, password) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            if (loginState is LoginState.Loading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text("LOGIN")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row {
            Text("Don't have an account? ", color = Color.Gray)
            Text("Sign Up", color = PrimaryBlue, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { navController.navigate("register_screen") })
        }
    }
}