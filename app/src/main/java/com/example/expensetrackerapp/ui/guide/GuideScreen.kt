package com.example.expensetrackerapp.ui.guide

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.expensetrackerapp.ui.theme.PrimaryBlue
import kotlinx.coroutines.launch

// 1. Dữ liệu cho từng trang hướng dẫn
data class GuidePage(
    val title: String,
    val description: String,
    val icon: ImageVector // Dùng Icon có sẵn để demo, bạn có thể đổi thành R.drawable.anh_cua_ban
)

val guidePages = listOf(
    GuidePage(
        "Quản lý chi tiêu",
        "Ghi chép thu chi hàng ngày dễ dàng, giúp bạn kiểm soát dòng tiền hiệu quả.",
        Icons.Default.AccountBalanceWallet
    ),
    GuidePage(
        "Báo cáo chi tiết",
        "Xem biểu đồ thống kê trực quan để hiểu rõ thói quen tiêu dùng của bạn.",
        Icons.Default.Analytics
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
    val scope = rememberCoroutineScope() // Dùng để điều khiển việc trượt trang bằng code

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(Color.White) // Nền trắng sạch sẽ
    ) {
        // --- Nút Skip (Góc trên phải) ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = {
                // Bấm Skip thì nhảy thẳng vào Login
                navController.navigate("login_screen") {
                    popUpTo("guide_screen") { inclusive = true }
                }
            }) {
                Text("Skip", color = Color.Gray, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))


        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {

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


                Text(
                    text = guidePages[page].title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))


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


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row {
                repeat(guidePages.size) { iteration ->
                    val color = if (pagerState.currentPage == iteration) PrimaryBlue else Color.LightGray
                    val width = if (pagerState.currentPage == iteration) 24.dp else 10.dp

                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .clip(RoundedCornerShape(50)) // Bo tròn
                            .background(color)
                            .height(10.dp)
                            .width(width) // Chấm hiện tại sẽ dài hơn
                    )
                }
            }


            Button(
                onClick = {
                    scope.launch {
                        if (pagerState.currentPage < guidePages.size - 1) {
                            // Nếu chưa phải trang cuối -> Trượt sang trang tiếp theo
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        } else {
                            // Nếu là trang cuối -> Chuyển sang Login
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