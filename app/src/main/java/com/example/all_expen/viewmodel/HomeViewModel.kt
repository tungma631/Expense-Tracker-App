package com.example.all_expen.viewmodel

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.all_expen.data.DatabaseHelper
// 👇 Import đúng model từ data
import com.example.all_expen.data.model.ExpenseTransaction

// --- 1. UI MODEL ---
data class ChartUiItem(
    val categoryName: String,
    val totalAmount: Double,
    val percent: Float,
    val color: Color,
    val iconEmoji: String,
    val iconBgColor: Color
)

// --- 2. VIEW MODEL ---
class HomeViewModel : ViewModel() {

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth.asStateFlow()

    private val _totalExpense = MutableStateFlow(0.0)
    val totalExpense: StateFlow<Double> = _totalExpense.asStateFlow()

    private val _transactions = MutableStateFlow<List<ExpenseTransaction>>(emptyList())
    val transactions: StateFlow<List<ExpenseTransaction>> = _transactions.asStateFlow()

    private val _chartData = MutableStateFlow<List<ChartUiItem>>(emptyList())
    val chartData: StateFlow<List<ChartUiItem>> = _chartData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private var allTransactionsCache: List<ExpenseTransaction> = emptyList()

    init {
        startRealtimeUpdates()
    }

    private fun startRealtimeUpdates() {
        viewModelScope.launch {
            while (isActive) {
                loadData(isSilent = true)
                delay(5000)
            }
        }
    }

    fun loadData(isSilent: Boolean = false) {
        viewModelScope.launch {
            if (!isSilent) _isLoading.value = true
            try {
                // Gọi Database ở luồng IO
                val rawTransactions = withContext(Dispatchers.IO) {
                    DatabaseHelper.getAllTransactions()
                }

                // Luôn cập nhật lại kể cả khi dữ liệu không đổi (để test logic lọc)
                Log.d("HomeViewModel", "Đã tải được ${rawTransactions.size} giao dịch từ DB.")
                allTransactionsCache = rawTransactions
                recalculateDataByMonth()

            } catch (e: Exception) {
                Log.e("HomeViewModel", "Lỗi update: ${e.message}")
            } finally {
                if (!isSilent) _isLoading.value = false
            }
        }
    }

    fun addNewExpense(title: String, amount: Double, category: String) {
        viewModelScope.launch(Dispatchers.IO) {
            DatabaseHelper.addTransaction(title, amount)
            loadData(isSilent = true)
        }
    }

    fun changeMonth(monthsToAdd: Long) {
        _currentMonth.value = _currentMonth.value.plusMonths(monthsToAdd)
        recalculateDataByMonth()
    }

    private fun recalculateDataByMonth() {
        val selectedMonth = _currentMonth.value

        // Sử dụng Formatter linh hoạt hơn
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        val filteredList = allTransactionsCache.filter { transaction ->
            try {
                // 🔥 QUAN TRỌNG: trim() để xóa khoảng trắng thừa
                val cleanDateStr = transaction.date.trim()

                // Parse ngày
                val date = LocalDate.parse(cleanDateStr, dateFormatter)

                // So sánh tháng
                val isMatch = YearMonth.from(date) == selectedMonth

                // Log debug để kiểm tra (Xem trong Logcat)
                if (isMatch) Log.d("HomeDebug", "Khớp tháng: $cleanDateStr")

                isMatch
            } catch (e: Exception) {
                Log.e("HomeDebug", "Lỗi parse ngày '${transaction.date}': ${e.message}")
                false
            }
        }

        Log.d("HomeViewModel", "Sau khi lọc tháng $selectedMonth còn lại: ${filteredList.size} dòng")

        _transactions.value = filteredList
        val total = filteredList.sumOf { it.amount }
        _totalExpense.value = total

        val groupedMap = filteredList.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        val chartItems = groupedMap.map { (category, groupTotal) ->
            val percentage = if (total > 0) (groupTotal / total).toFloat() else 0f
            val config = getCategoryConfig(category)

            ChartUiItem(
                categoryName = category,
                totalAmount = groupTotal,
                percent = percentage,
                color = config.color,
                iconEmoji = config.emoji,
                iconBgColor = config.bgColor
            )
        }.sortedByDescending { it.percent }

        _chartData.value = chartItems
    }

    private data class CategoryConfig(val color: Color, val emoji: String, val bgColor: Color)

    private fun getCategoryConfig(category: String): CategoryConfig {
        return when (category) {
            "Ăn uống" -> CategoryConfig(Color(0xFF6ABEF7), "🍔", Color(0xFFE1F5FE))
            "Mua sắm" -> CategoryConfig(Color(0xFFF6C8F2), "🛍️", Color(0xFFFCE4EC))
            "Nhà cửa" -> CategoryConfig(Color(0xFFEF5350), "🏠", Color(0xFFEF5350))
            "Đi lại" -> CategoryConfig(Color(0xFFFFA726), "🛵", Color(0xFFFFF3E0))
            "Giải trí" -> CategoryConfig(Color(0xFFAB47BC), "🎬", Color(0xFFF3E5F5))
            else -> CategoryConfig(Color(0xFFBDBDBD), "❓", Color(0xFFEEEEEE))
        }
    }

    fun formatCurrency(amount: Double): String {
        return try {
            val formatter = NumberFormat.getCurrencyInstance(Locale("vi", "VN"))
            formatter.format(amount).replace("₫", "đ").replace(" ", " ")
        } catch (e: Exception) {
            "${amount.toInt()} đ"
        }
    }
}