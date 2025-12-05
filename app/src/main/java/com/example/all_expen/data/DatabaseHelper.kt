package com.example.all_expen.data

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.example.all_expen.data.model.CurrentUser
import com.example.all_expen.data.model.ExpenseTransaction
import java.sql.DriverManager
import java.sql.ResultSet
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Properties

object DatabaseHelper {

    // --- CẤU HÌNH KẾT NỐI ---
    private const val HOST = "dpg-d4k8e8a4d50c73dbfq0g-a.oregon-postgres.render.com"
    private const val DATABASE = "expensetracker_db_8iqe"
    private const val USER = "mavantung"
    private const val PASS = "84X11t0GqvbdwgEaKncCZpTK30R35BLy"

    // URL kết nối
    private const val URL = "jdbc:postgresql://$HOST:5432/$DATABASE?sslmode=require"

    // --- 1. HÀM LẤY DỮ LIỆU (Của riêng User) ---
    fun getAllTransactions(): List<ExpenseTransaction> {
        val list = mutableListOf<ExpenseTransaction>()

        // 🔒 KIỂM TRA BẢO MẬT: Chưa đăng nhập thì không lấy dữ liệu
        val currentUserId = CurrentUser.id ?: run {
            Log.e("KET_NOI_DB", ">>> Chưa đăng nhập, trả về danh sách rỗng.")
            return emptyList()
        }

        var connection: java.sql.Connection? = null

        try {
            // Nạp Driver
            Class.forName("org.postgresql.Driver")

            val props = Properties()
            props.setProperty("user", USER)
            props.setProperty("password", PASS)
            props.setProperty("sslmode", "require")
            props.setProperty("loginTimeout", "15")

            Log.d("KET_NOI_DB", ">>> Đang lấy dữ liệu cho User ID: $currentUserId ...")
            connection = DriverManager.getConnection(URL, props)

            // 🔥 SỬA SQL: Thêm WHERE user_id = ?
            val sql = "SELECT id, description, amount, date_time FROM expenses WHERE user_id = ? ORDER BY date_time DESC"

            val statement = connection.prepareStatement(sql)
            statement.setInt(1, currentUserId) // Điền ID người dùng vào dấu ?

            val resultSet: ResultSet = statement.executeQuery()

            val appFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            while (resultSet.next()) {
                val idStr = resultSet.getInt("id").toString()
                val amount = resultSet.getDouble("amount")
                val description = resultSet.getString("description") ?: "Không tên"

                val timestamp = resultSet.getTimestamp("date_time")
                val dateStr = if (timestamp != null) appFormat.format(timestamp) else appFormat.format(Date())

                val category = guessCategory(description)
                list.add(ExpenseTransaction(idStr, description, amount, category, dateStr))
            }
            Log.d("KET_NOI_DB", ">>> Tải xong: ${list.size} dòng cho User $currentUserId.")

        } catch (e: Throwable) {
            showErrorToast("Lỗi Home: ${e.message}")
            Log.e("KET_NOI_DB", "!!! LỖI HOME: ${e.message}", e)
        } finally {
            try { connection?.close() } catch (e: Exception) {}
        }
        return list
    }

    // --- 2. HÀM THÊM DỮ LIỆU (Gán cho User) ---
// --- 2. HÀM THÊM DỮ LIỆU (Đã sửa để lưu user_id) ---
// Trong DatabaseHelper.kt

    fun addTransaction(title: String, amount: Double) {
        val currentUserId = CurrentUser.id

        // Log kiểm tra xem ID là bao nhiêu
        Log.d("DEBUG_ID", ">>> Đang thêm chi tiêu với User ID: $currentUserId")

        if (currentUserId == null) {
            Log.e("KET_NOI_DB", ">>> LỖI: Chưa đăng nhập (ID is null), hủy thao tác!")
            return
        }

        var connection: java.sql.Connection? = null
        try {
            Class.forName("org.postgresql.Driver")
            val props = java.util.Properties()
            props.setProperty("user", USER)
            props.setProperty("password", PASS)
            props.setProperty("sslmode", "require")

            connection = DriverManager.getConnection(URL, props)

            val sql = "INSERT INTO expenses (description, amount, date_time, user_id) VALUES (?, ?, NOW(), ?)"

            // Log SQL để chắc chắn câu lệnh đã đổi
            Log.d("DEBUG_SQL", ">>> SQL thực thi: $sql")

            val statement = connection.prepareStatement(sql)
            statement.setString(1, title)
            statement.setDouble(2, amount)
            statement.setInt(3, currentUserId)

            statement.executeUpdate()
            Log.d("KET_NOI_DB", ">>> Đã thêm thành công dòng mới cho User $currentUserId")

        } catch (e: Throwable) {
            Log.e("KET_NOI_DB", "!!! LỖI THÊM: ${e.message}")
        } finally {
            try { connection?.close() } catch (e: Exception) {}
        }
    }

    // --- HÀM PHỤ TRỢ ---
    private fun guessCategory(desc: String): String {
        val lower = desc.trim().lowercase()
        return when {
            lower.contains("xăng") || lower.contains("xe") || lower.contains("grab") || lower.contains("tàu") || lower.contains("đi") -> "Đi lại"
            lower.contains("nhà") || lower.contains("điện") || lower.contains("nước") || lower.contains("mạng") || lower.contains("gas") -> "Nhà cửa"
            lower.contains("ăn") || lower.contains("uống") || lower.contains("cafe") || lower.contains("trà") || lower.contains("cơm") || lower.contains("bún") -> "Ăn uống"
            lower.contains("mua") || lower.contains("sắm") || lower.contains("áo") || lower.contains("quần") || lower.contains("giày") -> "Mua sắm"
            lower.contains("học") || lower.contains("sách") || lower.contains("vở") -> "Giáo dục"
            lower.contains("thuốc") || lower.contains("khám") || lower.contains("bệnh") -> "Y tế"
            else -> "Khác"
        }
    }

    private fun showErrorToast(message: String) {
        Handler(Looper.getMainLooper()).post {
            Log.e("LOI_HIEN_THI", "⚠️⚠️⚠️ APP BÁO LỖI: $message ⚠️⚠️⚠️")
        }
    }
}