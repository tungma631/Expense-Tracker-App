package com.example.all_expen.ui.register

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
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
// 👇 ĐÃ SỬA IMPORT ĐÚNG
import com.example.all_expen.ui.theme.PrimaryBlue
import com.example.all_expen.viewmodel.RegisterViewModel
import com.example.all_expen.viewmodel.RegisterState

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = viewModel()
) {
    val registerState by viewModel.registerState.collectAsState()
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    LaunchedEffect(registerState) {
        when (registerState) {
            is RegisterState.Success -> {
                Toast.makeText(context, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                navController.popBackStack()
            }
            is RegisterState.Error -> {
                Toast.makeText(context, (registerState as RegisterState.Error).message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Create Account", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            value = name, onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Person, null, tint = PrimaryBlue) }
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = email, onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Email, null, tint = PrimaryBlue) }
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = password, onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            leadingIcon = { Icon(Icons.Default.Lock, null, tint = PrimaryBlue) }
        )
        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = confirmPassword, onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            leadingIcon = { Icon(Icons.Default.Lock, null, tint = PrimaryBlue) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (password == confirmPassword) {
                    viewModel.register(name, email, password)
                } else {
                    Toast.makeText(context, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
        ) {
            if (registerState is RegisterState.Loading) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Text("REGISTER")
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Back to Login",
            color = PrimaryBlue,
            modifier = Modifier.clickable { navController.popBackStack() }
        )
    }
}