package com.example.expensetrackerapp.ui.theme

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlin.math.cos
import kotlin.math.sin
import java.text.NumberFormat
import java.util.Locale
import androidx.compose.ui.unit.times
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

// --- 1. MÀU SẮC ---
val ColorDarkBlueNav = Color(0xFF1A237E)
val ColorTealAccent = Color(0xFF26C6DA)
val ColorUnselected = Color(0xFF90A4AE)

// --- 2. MÀN HÌNH CHÍNH (CÓ MENU DƯỚI & CHATBOT) ---
@Composable
fun MainScreenWithNavigation(viewModel: HomeViewModel = viewModel()) {
    var selectedItem by remember { mutableIntStateOf(0) }

    Scaffold(
        // Nút Chatbot
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Mở Chatbot */ },
                containerColor = ColorTealAccent,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "Chatbot",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End, // Góc phải

        // Menu dưới
        bottomBar = {
            NavigationBar(
                containerColor = ColorDarkBlueNav,
                contentColor = Color.White,
                tonalElevation = 8.dp
            ) {
                val items = listOf(
                    Triple("Trang chủ", Icons.Default.Home, 0),
                    Triple("Chi tiêu", Icons.Default.ReceiptLong, 1),
                    Triple("Hướng dẫn", Icons.Default.MenuBook, 2)
                )

                items.forEach { (label, icon, index) ->
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = null) },
                        label = { Text(label) },
                        selected = selectedItem == index,
                        onClick = { selectedItem = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = ColorTealAccent,
                            selectedTextColor = ColorTealAccent,
                            unselectedIconColor = ColorUnselected,
                            unselectedTextColor = ColorUnselected,
                            indicatorColor = ColorDarkBlueNav
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color.White)
        ) {
            when (selectedItem) {
                0 -> ExpensiveCloneScreen(viewModel) // Gọi hàm biểu đồ
                1 -> ExpenseListScreen()
                2 -> GuideScreen()
            }
        }
    }
}

// --- 3. MÀN HÌNH BIỂU ĐỒ (ExpensiveCloneScreen) ---
// Sửa lỗi 'Unresolved reference: ExpensiveCloneScreen' bằng cách định nghĩa nó ngay tại đây
@Composable
fun ExpensiveCloneScreen(viewModel: HomeViewModel) {
    val chartData by viewModel.chartData.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()

    // Lấy tháng hiện tại từ ViewModel
    val currentMonth by viewModel.currentMonth.collectAsState() // <--- MỚI

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // --- 1. Thay thế TopToggle cũ hoặc đặt Selector xuống dưới ---
        TopToggle()

        // --- 2. Thêm Thanh chọn tháng vào đây ---
        MonthSelector(
            currentMonth = currentMonth,
            onPrevious = { viewModel.changeMonth(-1) }, // Lùi 1 tháng
            onNext = { viewModel.changeMonth(1) }       // Tiến 1 tháng
        )
        // ----------------------------------------

        Spacer(modifier = Modifier.height(10.dp))

        if (chartData.isNotEmpty()) {
            // Biểu đồ (Giữ nguyên)
            DonutChartWithIcons(
                data = chartData,
                totalDisplay = viewModel.formatCurrency(totalExpense)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Danh sách (Giữ nguyên)
            chartData.forEach { item ->
                ListItemWithBubbleProgress(item)
                Spacer(modifier = Modifier.height(24.dp))
            }
            Spacer(modifier = Modifier.height(100.dp))

        } else {
            // Hiển thị khi không có dữ liệu của tháng đó
            Column(
                modifier = Modifier.height(300.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Tháng này chưa tiêu gì cả!", color = Color.Gray)
            }
        }
    }
}
// --- 4. CÁC COMPOSABLE PHỤ TRỢ (Vẽ biểu đồ, List item) ---

@Composable
fun TopToggle() {
    Row(
        modifier = Modifier
            .wrapContentWidth()
            .height(40.dp)
            .background(Color(0xFFF5F5F5), RoundedCornerShape(20.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {},
            colors = ButtonDefaults.buttonColors(containerColor = ColorTealAccent),
            shape = RoundedCornerShape(20.dp),
            contentPadding = PaddingValues(horizontal = 24.dp)
        ) {
            Text("Chi phí", color = Color.White, fontWeight = FontWeight.Bold)
        }
        TextButton(
            onClick = {},
            colors = ButtonDefaults.textButtonColors(contentColor = Color.Gray),
            contentPadding = PaddingValues(horizontal = 24.dp)
        ) {
            Text("Thu nhập")
        }
    }
}

@Composable
fun DonutChartWithIcons(data: List<ChartUiItem>, totalDisplay: String) {
    val chartSize = 220.dp
    val strokeWidth = 70.dp

    Box(modifier = Modifier.size(chartSize), contentAlignment = Alignment.Center) {
        // --- Lớp 1: Vẽ vòng tròn biểu đồ ---
        Canvas(modifier = Modifier.fillMaxSize()) {
            var startAngle = -90f
            data.forEach { item ->
                val sweepAngle = item.percent * 360f
                drawArc(
                    color = item.color,
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth.toPx())
                )
                // Vẽ vạch trắng ngăn cách
                drawArc(
                    color = Color.White,
                    startAngle = startAngle,
                    sweepAngle = 1f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth.toPx() + 2)
                )
                startAngle += sweepAngle
            }
        }

        // --- Lớp 2: Hiển thị Tổng tiền ở giữa ---
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Tổng chi tiêu",
                fontSize = 12.sp,
                color = Color.Gray
            )
            Text(
                text = totalDisplay,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }

        // --- Lớp 3: Vẽ Icon nằm TRÊN vành biểu đồ ---
        var currentAngle = -90f

        // Khoảng cách từ tâm ra giữa vành màu:
        // Bán kính (110) - nửa độ dày (35) = 75.dp
        val distanceToCenter = 75.dp

        data.forEach { item ->
            val sweepAngle = item.percent * 360f
            val middleAngle = currentAngle + (sweepAngle / 2)

            // Đổi độ sang Radian để tính toán
            val angleRad = Math.toRadians(middleAngle.toDouble())

            // Tính tọa độ X, Y (Dùng Float để nhân với dp)
            val cosVal = cos(angleRad).toFloat()
            val sinVal = sin(angleRad).toFloat()

            val offsetX = distanceToCenter * cosVal
            val offsetY = distanceToCenter * sinVal

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = offsetX, y = offsetY) // Đặt vị trí
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(1.dp, item.color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = item.iconEmoji, fontSize = 18.sp)
            }
            currentAngle += sweepAngle
        }
    }
}
@Composable
fun ListItemWithBubbleProgress(item: ChartUiItem) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        // 1. Icon bên trái
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(item.iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Text(item.iconEmoji, fontSize = 24.sp)
        }

        Spacer(modifier = Modifier.width(16.dp))

        // 2. Phần Tên và Tiền
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = item.categoryName,
                    fontSize = 15.sp,
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Medium
                )

                // --- ĐÃ SỬA Ở ĐÂY: Gọi hàm formatVND ---
                Text(
                    text = formatVND(item.totalAmount),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
                // ----------------------------------------
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Thanh tiến trình (Progress Bar)
            BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(30.dp)) {
                val progressWidth = maxWidth * item.percent

                // Thanh nền xám
                Box(modifier = Modifier.align(Alignment.BottomStart).padding(bottom = 6.dp).fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)).background(Color(0xFFEEEEEE)))

                // Thanh màu xanh
                Box(modifier = Modifier.align(Alignment.BottomStart).padding(bottom = 6.dp).width(progressWidth).height(6.dp).clip(RoundedCornerShape(3.dp)).background(ColorTealAccent))

                // Bong bóng %
                if (item.percent > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .offset(x = progressWidth - 20.dp)
                            .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = String.format("%.1f%%", item.percent * 100), // Hiển thị kiểu 0.9%
                            fontSize = 11.sp,
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
// --- 5. CÁC MÀN HÌNH PLACEHOLDER ---
@Composable
fun ExpenseListScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = "Danh sách chi tiêu đầy đủ", fontSize = 20.sp, color = Color.Gray)
    }
}

@Composable
fun GuideScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.MenuBook,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = ColorTealAccent
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Hướng dẫn sử dụng App", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(
                text = "1. Nhập chi tiêu\n2. Xem biểu đồ\n3. Hỏi Chatbot",
                modifier = Modifier.padding(16.dp),
                color = Color.Gray
            )
            @Composable
            fun MonthSelector(
                currentMonth: YearMonth,
                onPrevious: () -> Unit,
                onNext: () -> Unit
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center, // Căn giữa
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Nút lùi
                    IconButton(onClick = onPrevious) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Tháng trước")
                    }

                    // Hiển thị tháng (Ví dụ: Tháng 12 - 2025)
                    Text(
                        text = "Tháng ${currentMonth.monthValue} - ${currentMonth.year}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorDarkBlueNav
                    )

                    // Nút tiến
                    IconButton(onClick = onNext) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Tháng sau")
                    }
                }
            }
        }

    }
}
@Composable
fun MonthSelector(
    currentMonth: YearMonth,
    onPrevious: () -> Unit,
    onNext: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Nút lùi
        IconButton(onClick = onPrevious) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "Tháng trước")
        }

        // Hiển thị tháng (Ví dụ: Tháng 12 - 2025)
        Text(
            text = "Tháng ${currentMonth.monthValue} - ${currentMonth.year}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = ColorDarkBlueNav 
        )

        // Nút tiến
        IconButton(onClick = onNext) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Tháng sau")
        }
    }
}
    fun formatVND(amount: Double): String {
        val formatter =
            java.text.NumberFormat.getNumberInstance(java.util.Locale.forLanguageTag("vi-VN"))
        return "${formatter.format(amount)}đ"
    }
