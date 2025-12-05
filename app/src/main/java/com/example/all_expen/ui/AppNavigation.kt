package com.example.all_expen.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.all_expen.data.SessionManager // Import SessionManager
import com.example.all_expen.ui.auth.LoginScreen
import com.example.all_expen.ui.chatbot.ChatBotScreen
import com.example.all_expen.ui.guide.GuideScreen
import com.example.all_expen.ui.home.MainScreenWithNavigation
import com.example.all_expen.ui.register.RegisterScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = SessionManager(context)

    // --- LOGIC TỰ ĐỘNG CHUYỂN MÀN HÌNH ---
    val startDest = if (sessionManager.isFirstTime()) {
        "guide_screen" // Lần đầu -> Vào Hướng dẫn
    } else if (sessionManager.isLoggedIn()) {
        "home_screen"  // Đã đăng nhập -> Vào Home luôn
    } else {
        "login_screen" // Chưa đăng nhập -> Vào Login
    }

    NavHost(navController = navController, startDestination = startDest) {
        composable("guide_screen") { GuideScreen(navController) }
        composable("login_screen") { LoginScreen(navController) }
        composable("register_screen") { RegisterScreen(navController) }
        composable("home_screen") { MainScreenWithNavigation(navController) }
        composable("chat_screen") { ChatBotScreen(navController) }
    }
}