package com.example.expensetrackerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.expensetrackerapp.ui.navigation.NavGraph
import com.example.expensetrackerapp.ui.theme.ExpenseTrackerAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ExpenseTrackerAppTheme {
                // Tạo bộ điều khiển điều hướng
                val navController = rememberNavController()

                // Gọi NavGraph để bắt đầu ứng dụng
                NavGraph(navController = navController)
            }
        }
    }
}