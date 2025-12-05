package com.example.all_expen.ui.guide

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.all_expen.data.api.ApiClient
import com.example.all_expen.ui.theme.PrimaryBlue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.example.all_expen.data.SessionManager
import androidx.compose.ui.platform.LocalContext



data class GuidePage(
    val title: String,
    val description: String,
    val icon: ImageVector
)

val guidePages = listOf(
    GuidePage(
        "Quản lý chi tiêu",
        "Ghi chép thu chi hàng ngày dễ dàng, giúp bạn kiểm soát dòng tiền hiệu quả.",
        Icons.Default.Home
    ),
    GuidePage(
        "Báo cáo chi tiết",
        "Xem biểu đồ thống kê trực quan để hiểu rõ thói quen tiêu dùng của bạn.",
        Icons.AutoMirrored.Filled.List
    ),
    GuidePage(
        "An toàn & Bảo mật",
        "Dữ liệu của bạn được mã hóa và bảo vệ an toàn tuyệt đối.",
        Icons.Default.Lock
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GuideScreen(navController: NavController) {
    val pagerState = rememberPagerState(pageCount = { guidePages.size })
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    // --- QUAN TRỌNG: GỌI API ĐÁNH THỨC SERVER ---
    LaunchedEffect(Unit) {
        // Chạy trên luồng IO để không làm đơ giao diện
        withContext(Dispatchers.IO) {
            try {
                // Gọi API wakeUpServer (đã định nghĩa trong ApiService)
                // Mục đích: Để Server Render khởi động trong lúc user đang đọc hướng dẫn
                val response = ApiClient.api.wakeUpServer()
                println("Wake-up call sent: ${response.code()}")
            } catch (e: Exception) {
                // Không quan trọng lỗi gì, chỉ cần gửi request đi là được
                println("Wake-up call error (ignored): ${e.message}")
            }
        }
    }
    // ----------------------------------------------

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(Color.White)
    ) {
        // --- Nút Skip (Góc trên phải) ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = {
                sessionManager.setFinishedGuide() // <--- THÊM DÒNG NÀY
                navController.navigate("login_screen") {
                    popUpTo("guide_screen") { inclusive = true }
                }
            }) {
                Text("Skip", color = Color.Gray, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // --- PHẦN CHÍNH: SLIDER (Pager) ---
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                // Vùng chứa Icon
                Box(
                    modifier = Modifier
                        .size(280.dp)
                        .background(PrimaryBlue.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = guidePages[page].icon,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(120.dp)
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Tiêu đề
                Text(
                    text = guidePages[page].title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Mô tả
                Text(
                    text = guidePages[page].description,
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp),
                    lineHeight = 24.sp
                )
            }
        }

        // --- PHẦN CHÂN TRANG (Indicators + Nút Next) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Indicators (Các chấm tròn)
            Row {
                repeat(guidePages.size) { iteration ->
                    val color =
                        if (pagerState.currentPage == iteration) PrimaryBlue else Color.LightGray
                    val width = if (pagerState.currentPage == iteration) 24.dp else 10.dp

                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(RoundedCornerShape(50))
                            .background(color)
                            .height(10.dp)
                            .width(width)
                    )
                }
            }

            // 2. Nút Next / Get Started
            Button(
                onClick = {
                    scope.launch {
                        if (pagerState.currentPage < guidePages.size - 1) {
                            // Chưa đến trang cuối -> Trượt tiếp
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        } else {
                            // Đã đến trang cuối -> Vào Login
                            sessionManager.setFinishedGuide() // <--- THÊM DÒNG NÀY
                            navController.navigate("login_screen") {
                                popUpTo("guide_screen") { inclusive = true }
                            }
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
            ) {
                if (pagerState.currentPage == guidePages.size - 1) {
                    Text("Get Started", color = Color.White, fontWeight = FontWeight.Bold)
                } else {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next",
                        tint = Color.White
                    )
                }
            }
        }
    }
}