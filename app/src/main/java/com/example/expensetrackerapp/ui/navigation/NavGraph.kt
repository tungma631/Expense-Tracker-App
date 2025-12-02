package com.example.expensetrackerapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

// Import tất cả các màn hình bạn đã tạo
import com.example.expensetrackerapp.ui.guide.GuideScreen
import com.example.expensetrackerapp.ui.login.LoginScreen
import com.example.expensetrackerapp.ui.register.RegisterScreen
import com.example.expensetrackerapp.ui.home.HomeScreen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "guide_screen" // Mở App lên sẽ vào màn hình Guide đầu tiên
    ) {

        // 1. Màn hình Hướng dẫn (Guide / Onboarding)
        composable("guide_screen") {
            GuideScreen(navController = navController)
        }

        // 2. Màn hình Đăng nhập (Login)
        composable("login_screen") {
            LoginScreen(navController = navController)
        }

        // 3. Màn hình Đăng ký (Register)
        composable("register_screen") {
            RegisterScreen(navController = navController)
        }

        // 4. Màn hình Trang chủ (Home)
        // Đây là nơi sẽ đến sau khi Login thành công
        composable("home_screen") {
            HomeScreen(navController = navController)
        }
    }
}