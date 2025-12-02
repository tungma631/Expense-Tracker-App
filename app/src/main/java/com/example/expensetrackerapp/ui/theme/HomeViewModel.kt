package com.example.expensetrackerapp.ui.theme

import android.util.Log
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

// --- 1. DATA MODEL CHO GIAO DỊCH ---
data class ExpenseTransaction(
    val id: String,
    val title: String,
    val amount: Double,
    val category: String,
    val date: String
)

// --- 2. DATA MODEL CHO BIỂU ĐỒ ---
data class ChartUiItem(
    val categoryName: String,
    val totalAmount: Double,
    val percent: Float,
    val color: Color,
    val iconEmoji: String,
    val iconBgColor: Color
)

// --- 3. HOME VIEW MODEL ---
class HomeViewModel : ViewModel() {

    // --- MỚI: Biến lưu tháng đang chọn (Mặc định là tháng hiện tại) ---
    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth.asStateFlow()

    // Tổng tiền chi tiêu (Của tháng đang chọn)
    private val _totalExpense = MutableStateFlow(0.0)
    val totalExpense: StateFlow<Double> = _totalExpense.asStateFlow()

    // Danh sách giao dịch (Của tháng đang chọn)
    private val _transactions = MutableStateFlow<List<ExpenseTransaction>>(emptyList())
    val transactions: StateFlow<List<ExpenseTransaction>> = _transactions.asStateFlow()

    // Dữ liệu Biểu đồ (Của tháng đang chọn)
    private val _chartData = MutableStateFlow<List<ChartUiItem>>(emptyList())
    val chartData: StateFlow<List<ChartUiItem>> = _chartData.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // --- MỚI: Biến cache để lưu TẤT CẢ dữ liệu lấy từ API ---
    private var allTransactionsCache: List<ExpenseTransaction> = emptyList()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("HomeViewModel", "Đang gọi API...")
                val rawTransactions = RetrofitClient.apiService.getTransactions()
                Log.d("HomeViewModel", "Đã lấy được ${rawTransactions.size} giao dịch")

                // 1. Lưu toàn bộ dữ liệu vào Cache (Bộ nhớ tạm)
                allTransactionsCache = rawTransactions

                // 2. Tính toán lại dữ liệu cho tháng hiện tại
                recalculateDataByMonth()

            } catch (e: Exception) {
                Log.e("HomeViewModel", "Lỗi gọi API: ${e.message}")
                e.printStackTrace()
                _transactions.value = emptyList()
                _chartData.value = emptyList()
                _totalExpense.value = 0.0
            } finally {
                _isLoading.value = false
            }
        }
    }

    // --- MỚI: Hàm đổi tháng (Khi bấm nút < hoặc >) ---
    fun changeMonth(monthsToAdd: Long) {
        _currentMonth.value = _currentMonth.value.plusMonths(monthsToAdd)
        // Mỗi khi đổi tháng, tính toán lại dữ liệu từ Cache
        recalculateDataByMonth()
    }

    // --- MỚI: Hàm lọc dữ liệu theo tháng và tính toán ---
    private fun recalculateDataByMonth() {
        val selectedMonth = _currentMonth.value
        // Định dạng ngày phải khớp với Database: dd/MM/yyyy
        val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

        // 1. Lọc danh sách: Chỉ lấy giao dịch khớp tháng/năm đang chọn
        val filteredList = allTransactionsCache.filter { transaction ->
            try {
                val date = LocalDate.parse(transaction.date, dateFormatter)
                YearMonth.from(date) == selectedMonth
            } catch (e: Exception) {
                false // Bỏ qua nếu ngày sai định dạng
            }
        }

        // 2. Cập nhật danh sách hiển thị
        _transactions.value = filteredList

        // 3. Tính tổng tiền (Của tháng đó)
        val total = filteredList.sumOf { it.amount }
        _totalExpense.value = total

        // 4. Tính toán biểu đồ (Của tháng đó)
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

    // --- CẤU HÌNH MÀU SẮC ---
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