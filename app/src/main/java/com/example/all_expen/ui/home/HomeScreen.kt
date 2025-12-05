package com.example.all_expen.ui.home

import android.app.Activity
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import kotlin.math.cos
import kotlin.math.sin
import java.time.YearMonth
import com.example.all_expen.data.SessionManager
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource


// IMPORTS TỪ PROJECT CỦA BẠN
import com.example.all_expen.ui.screens.ExpenseHistoryScreen
import com.example.all_expen.viewmodel.ChartUiItem
import com.example.all_expen.viewmodel.HomeViewModel

// --- MÀU SẮC (Lấy từ file Copy cho đồng bộ) ---
val ColorDarkBlueNav = Color(0xFF1A237E)
val ColorTealAccent = Color(0xFF26C6DA)
val ColorUnselected = Color(0xFF90A4AE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenWithNavigation(
    navController: NavController,
    viewModel: HomeViewModel = viewModel()
) {
    var selectedItem by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    val sessionManager = SessionManager(context)

    // State Dialog thêm chi tiêu
    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddExpenseDialog(
            onDismiss = { showAddDialog = false },
            onSave = { title, amount, cat ->
                viewModel.addNewExpense(title, amount, cat)
            }
        )
    }

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    // --- THAY ĐỔI: Dùng Image thay cho Text ---
                    // Đảm bảo bạn đã import androidx.compose.ui.res.painterResource
                    // Và file app_logo.png đã nằm trong res/drawable/
                    Image(
                        painter = painterResource(id = com.example.all_expen.R.drawable.app_logo),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .height(60.dp) // Chỉnh chiều cao cho vừa vặn thanh TopBar
                            .wrapContentWidth(Alignment.Start) // Căn logo sang trái
                    )
                },
                actions = {
                    // --- NÚT LOGOUT ---
                    IconButton(onClick = {
                        sessionManager.logout()
                        navController.navigate("login_screen") {
                            popUpTo("home_screen") { inclusive = true }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout",
                            tint = Color.Red,
                            modifier = Modifier.size(32.dp) // <-- Đã sửa lỗi tại đây (thêm dấu phẩy và modifier)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                // Nút con: Thêm chi tiêu (Chỉ hiện ở Tab Home)
                if (selectedItem == 0) {
                    SmallFloatingActionButton(
                        onClick = { showAddDialog = true },
                        containerColor = Color.White,
                        contentColor = ColorTealAccent,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Expense")
                    }
                }

                // Nút to: Chatbot
                FloatingActionButton(
                    onClick = { navController.navigate("chat_screen") },
                    containerColor = ColorTealAccent,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.SmartToy, "Chatbot", Modifier.size(28.dp))
                }
            }
        },
        bottomBar = {
            // Phần Navigation bo tròn đẹp
            Surface(
                shadowElevation = 15.dp,
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                NavigationBar(
                    containerColor = ColorDarkBlueNav,
                    tonalElevation = 0.dp
                ) {
                    val items = listOf(
                        Triple("Trang chủ", Icons.Default.Home, 0),
                        Triple("Lịch sử", Icons.Default.ReceiptLong, 1)
                    )

                    items.forEach { (label, icon, index) ->
                        val isSelected = selectedItem == index
                        NavigationBarItem(
                            icon = { Icon(icon, contentDescription = null) },
                            label = {
                                Text(
                                    label,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            selected = isSelected,
                            onClick = { selectedItem = index },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Color.White,
                                indicatorColor = ColorTealAccent,
                                selectedTextColor = ColorTealAccent,
                                unselectedIconColor = ColorUnselected,
                                unselectedTextColor = ColorUnselected
                            )
                        )
                    }
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
                0 -> ExpensiveCloneScreen(viewModel)
                1 -> ExpenseHistoryScreen()
            }
        }
    }
}

// --- 3. MÀN HÌNH BIỂU ĐỒ (Đã cập nhật giao diện đẹp) ---
@Composable
fun ExpensiveCloneScreen(viewModel: HomeViewModel) {
    val chartData by viewModel.chartData.collectAsState()
    val totalExpense by viewModel.totalExpense.collectAsState()
    val currentMonth by viewModel.currentMonth.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        // UI Mới: Thêm nút Toggle hình viên thuốc
        TopToggle()

        // UI Mới: Selector tháng
        MonthSelector(
            currentMonth = currentMonth,
            onPrevious = { viewModel.changeMonth(-1) },
            onNext = { viewModel.changeMonth(1) }
        )

        Spacer(modifier = Modifier.height(10.dp))

        if (chartData.isNotEmpty()) {
            // UI Mới: Biểu đồ Donut xịn
            DonutChartWithIcons(
                data = chartData,
                totalDisplay = viewModel.formatCurrency(totalExpense)
            )

            Spacer(modifier = Modifier.height(40.dp))

            // UI Mới: Danh sách List đẹp
            chartData.forEach { item ->
                ListItemWithBubbleProgress(item)
                Spacer(modifier = Modifier.height(24.dp))
            }
            Spacer(modifier = Modifier.height(100.dp)) // Padding dưới cùng để không bị che bởi FAB
        } else {
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

// --- 4. CÁC COMPOSABLE GIAO DIỆN ĐẸP (Lấy từ HomeScreen - Copy) ---

@Composable
fun TopToggle() {
    Surface(
        shape = RoundedCornerShape(50),
        color = ColorTealAccent,
        modifier = Modifier
            .height(40.dp)
            .width(120.dp)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Chi phí",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
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

        // --- Lớp 3: Vẽ Icon nằm TRÊN vành biểu đồ (Logic sin/cos) ---
        var currentAngle = -90f
        val distanceToCenter = 75.dp // Khoảng cách từ tâm ra giữa vành màu

        data.forEach { item ->
            val sweepAngle = item.percent * 360f
            val middleAngle = currentAngle + (sweepAngle / 2)

            val angleRad = Math.toRadians(middleAngle.toDouble())
            val cosVal = cos(angleRad).toFloat()
            val sinVal = sin(angleRad).toFloat()

            val offsetX = distanceToCenter * cosVal
            val offsetY = distanceToCenter * sinVal

            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(x = offsetX, y = offsetY)
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

                Text(
                    text = formatVND(item.totalAmount), // Sử dụng hàm formatVND ở dưới
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. Thanh tiến trình (Progress Bar) có bong bóng
            BoxWithConstraints(modifier = Modifier.fillMaxWidth().height(30.dp)) {
                val progressWidth = maxWidth * item.percent

                // Thanh nền xám
                Box(modifier = Modifier.align(Alignment.BottomStart).padding(bottom = 6.dp).fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)).background(Color(0xFFEEEEEE)))

                // Thanh màu xanh
                Box(modifier = Modifier.align(Alignment.BottomStart).padding(bottom = 6.dp).width(progressWidth).height(6.dp).clip(RoundedCornerShape(3.dp)).background(ColorTealAccent))

                // Bong bóng hiển thị %
                if (item.percent > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .offset(x = progressWidth - 20.dp) // Căn chỉnh vị trí bong bóng
                            .border(1.dp, Color.Gray, RoundedCornerShape(12.dp))
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = String.format("%.1f%%", item.percent * 100),
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
        IconButton(onClick = onPrevious) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "Tháng trước")
        }

        Text(
            text = "Tháng ${currentMonth.monthValue} - ${currentMonth.year}",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = ColorDarkBlueNav
        )

        IconButton(onClick = onNext) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Tháng sau")
        }
    }
}

// Helper format tiền tệ (Để hiển thị trong List Item)
fun formatVND(amount: Double): String {
    val formatter = java.text.NumberFormat.getNumberInstance(java.util.Locale("vi", "VN"))
    return "${formatter.format(amount)}đ"
}