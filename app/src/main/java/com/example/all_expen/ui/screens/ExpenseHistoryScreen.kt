package com.example.all_expen.ui.screens

import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
// 👇 1. Import CurrentUser để lấy ID
import com.example.all_expen.data.model.CurrentUser
import com.example.all_expen.data.PostgresHelper
import com.example.all_expen.data.model.Expense
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

// --- MÀU SẮC ---
val PrimaryBlue = Color(0xFF1976D2)
val LightBlueBg = Color(0xFFE3F2FD)
val TextGray = Color(0xFF757575)
val ExpenseRed = Color(0xFFD32F2F)
val SurfaceWhite = Color(0xFFFFFFFF)
val BackgroundGray = Color(0xFFF7F9FC)

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ExpenseHistoryScreen() {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // --- LOGIC CHỌN NGÀY ---
    val today = remember { Calendar.getInstance().timeInMillis }
    val startOfMonth = remember {
        val c = Calendar.getInstance()
        c.set(Calendar.DAY_OF_MONTH, 1)
        c.set(Calendar.HOUR_OF_DAY, 0)
        c.timeInMillis
    }

    var fromDate by remember { mutableStateOf(startOfMonth) }
    var toDate by remember { mutableStateOf(today + 86400000) } // Cộng thêm 1 ngày để lấy hết hôm nay

    fun showDatePicker(isFrom: Boolean) {
        val currentMillis = if(isFrom) fromDate else toDate
        calendar.timeInMillis = currentMillis

        DatePickerDialog(
            context,
            { _: DatePicker, year: Int, month: Int, day: Int ->
                val selected = Calendar.getInstance()
                selected.set(year, month, day)
                if (isFrom) {
                    selected.set(Calendar.HOUR_OF_DAY, 0)
                    fromDate = selected.timeInMillis
                } else {
                    selected.set(Calendar.HOUR_OF_DAY, 23)
                    selected.set(Calendar.MINUTE, 59)
                    toDate = selected.timeInMillis
                }
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    // --- LOGIC LẤY DỮ LIỆU ---
    var expenseList by remember { mutableStateOf<List<Expense>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // 👇 2. Lấy ID user hiện tại
    val currentUserId = CurrentUser.id

    LaunchedEffect(fromDate, toDate) {

        if (currentUserId == null) {
            isLoading = false
            return@LaunchedEffect
        }

        isLoading = true
        while (true) {
            // 👇 3. Truyền ID vào hàm lấy dữ liệu
            val newData = PostgresHelper.getExpensesByDateRange(currentUserId, fromDate, toDate)
            expenseList = newData
            isLoading = false
            delay(5000) // Tự động refresh mỗi 5s
        }
    }

    val groupedExpenses = remember(expenseList) {
        expenseList.groupBy {
            try {
                SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(it.date)
            } catch (e: Exception) { "Không xác định" }
        }
    }

    // --- GIAO DIỆN ---
    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            Column {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            "Lịch sử chi tiêu",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = SurfaceWhite
                    )
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceWhite)
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterButton(label = "Từ: ${formatDateShort(fromDate)}", isSelected = true) { showDatePicker(true) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, contentDescription = null, tint = TextGray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    FilterButton(label = "Đến: ${formatDateShort(toDate)}", isSelected = true) { showDatePicker(false) }
                }
                HorizontalDivider(color = Color.LightGray.copy(alpha = 0.3f))
            }
        }
    ) { padding ->
        if (isLoading && expenseList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryBlue)
            }
        } else if (expenseList.isEmpty()) {
            EmptyStateView(padding)
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                groupedExpenses.forEach { (date, expenses) ->
                    stickyHeader {
                        DateHeader(date, expenses.sumOf { it.amount })
                    }
                    items(expenses) { expense ->
                        ExpenseItem(expense)
                    }
                }
            }
        }
    }
}

// --- CÁC COMPONENT CON (GIỮ NGUYÊN) ---

@Composable
fun DateHeader(date: String, totalAmount: Double) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundGray)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = date,
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = TextGray
            )
        )
        Text(
            text = "Tổng: -${formatMoney(totalAmount)}",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.Bold,
                color = PrimaryBlue
            )
        )
    }
}

@Composable
fun ExpenseItem(expense: Expense) {
    val iconVector = getIconForExpense(expense.description)

    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(LightBlueBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = null,
                    tint = PrimaryBlue,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = expense.description,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    ),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccessTime,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = try {
                            SimpleDateFormat("HH:mm", Locale.getDefault()).format(expense.date)
                        } catch (e: Exception) { "--:--" },
                        style = MaterialTheme.typography.bodySmall.copy(color = TextGray)
                    )
                }
            }

            Text(
                text = "-" + formatMoney(expense.amount),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = ExpenseRed
                )
            )
        }
    }
}

@Composable
fun FilterButton(label: String, isSelected: Boolean = false, onClick: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(50),
        color = if (isSelected) LightBlueBg else Color.Transparent,
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray) else null,
        modifier = Modifier
            .clickable { onClick() }
            .clip(RoundedCornerShape(50))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = if (isSelected) PrimaryBlue else TextGray
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) PrimaryBlue else TextGray
            )
        }
    }
}

@Composable
fun EmptyStateView(padding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(BackgroundGray),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = null,
                modifier = Modifier.size(60.dp),
                tint = Color(0xFFE0E0E0)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Không có giao dịch nào",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = TextGray)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Vui lòng chọn khoảng thời gian khác",
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.LightGray)
        )
    }
}

// --- HELPER FUNCTIONS ---

fun getIconForExpense(description: String): ImageVector {
    val desc = description.lowercase()
    return when {
        desc.contains("ăn") || desc.contains("uống") || desc.contains("bún") || desc.contains("trà") || desc.contains("cà phê") -> Icons.Default.Restaurant
        desc.contains("xe") || desc.contains("xăng") || desc.contains("grab") || desc.contains("đi") -> Icons.Default.DirectionsCar
        desc.contains("nhà") || desc.contains("điện") || desc.contains("nước") || desc.contains("wifi") -> Icons.Default.Home
        desc.contains("thịt") || desc.contains("rau") || desc.contains("siêu thị") || desc.contains("chợ") -> Icons.Default.ShoppingCart
        desc.contains("mua") || desc.contains("sắm") -> Icons.Default.ShoppingBag
        else -> Icons.Default.ReceiptLong
    }
}

fun formatMoney(amount: Double): String {
    return try {
        NumberFormat.getCurrencyInstance(Locale("vi", "VN")).format(amount)
    } catch (e: Exception) {
        "${amount.toInt()} đ"
    }
}

fun formatDateShort(millis: Long): String {
    return SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(millis))
}