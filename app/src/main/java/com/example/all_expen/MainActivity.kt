package com.example.all_expen

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.all_expen.ui.AppNavigation
import com.example.all_expen.data.SessionManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Khôi phục phiên đăng nhập (Để App biết là User cũ hay mới)
        val sessionManager = SessionManager(applicationContext)
        sessionManager.restoreUserSession()

        // 2. Thiết lập nội dung giao diện
        setContent {
            // Gọi File điều hướng tổng (AppNavigation sẽ quyết định hiện màn nào)
            AppNavigation()
        }
    }
}