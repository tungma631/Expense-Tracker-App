package com.example.all_expen.ui.register

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
import com.example.all_expen.viewmodel.RegisterViewModel
import com.example.all_expen.viewmodel.RegisterState
import com.example.all_expen.utils.ValidationUtils

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = viewModel()
) {
    val registerState by viewModel.registerState.collectAsState()
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }


    var email by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }

    var password by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf<String?>(null) }

    var confirmPassword by remember { mutableStateOf("") }


    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(registerState) {
        when (registerState) {
            is RegisterState.Success -> {
                Toast.makeText(context, "Đăng ký thành công! Vui lòng đăng nhập.", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }
            is RegisterState.Error -> {
                val errorMsg = (registerState as RegisterState.Error).message

                if (errorMsg.contains("Tên đăng nhập đã có người sử dụng", ignoreCase = true)) {
                    emailError = errorMsg
                } else {
                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
                }
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Tạo tài khoản", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
        Spacer(modifier = Modifier.height(30.dp))

        // Họ và tên
        OutlinedTextField(
            value = name, onValueChange = { name = it },
            label = { Text("Họ và tên") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Person, null, tint = PrimaryBlue) }
        )
        Spacer(modifier = Modifier.height(10.dp))


        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = ValidationUtils.getUsernameError(it)
            },
            label = { Text("Tên đăng nhập") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.AccountCircle, null, tint = PrimaryBlue) },
            isError = emailError != null,
            supportingText = {
                if (emailError != null) Text(text = emailError!!, color = Color.Red)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )
        Spacer(modifier = Modifier.height(4.dp))


        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                passwordError = ValidationUtils.getPasswordError(it)
            },
            label = { Text("Mật khẩu") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Lock, null, tint = PrimaryBlue) },
            isError = passwordError != null,
            supportingText = {
                if (passwordError != null) Text(text = passwordError!!, color = Color.Red)
            },
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Hiện mật khẩu")
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(4.dp))

        // Nhập lại mật khẩu (Kiểm tra khớp + Nút mắt)
        OutlinedTextField(
            value = confirmPassword, onValueChange = { confirmPassword = it },
            label = { Text("Nhập lại mật khẩu") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Lock, null, tint = PrimaryBlue) },
            isError = confirmPassword.isNotEmpty() && confirmPassword != password,
            supportingText = {
                if (confirmPassword.isNotEmpty() && confirmPassword != password) {
                    Text("Mật khẩu không khớp", color = Color.Red)
                }
            },
            trailingIcon = {
                val image = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(imageVector = image, contentDescription = "Hiện mật khẩu")
                }
            },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(24.dp))


        val isValid = emailError == null && passwordError == null &&
                email.isNotEmpty() && password.isNotEmpty() &&
                password == confirmPassword

        Button(
            onClick = { viewModel.register(name, email, password) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            enabled = isValid
        ) {
            if (registerState is RegisterState.Loading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text("ĐĂNG KÝ")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Quay lại đăng nhập ",
            color = PrimaryBlue,
            modifier = Modifier.clickable { navController.popBackStack() }
        )
    }
}