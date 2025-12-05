package com.example.all_expen.data

import android.util.Log
import com.example.all_expen.data.model.Expense
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection
import java.sql.DriverManager
import java.sql.Timestamp
import java.util.Date
import java.util.Properties

object PostgresHelper {
    // Cấu hình kết nối Database
    private const val URL = "jdbc:postgresql://dpg-d4k8e8a4d50c73dbfq0g-a.oregon-postgres.render.com:5432/expensetracker_db_8iqe"
    private const val USER = "mavantung"
    private const val PASS = "84X11t0GqvbdwgEaKncCZpTK30R35BLy"

    // 🔥 ĐÃ SỬA: Thêm tham số userId để lọc dữ liệu theo người dùng
    suspend fun getExpensesByDateRange(userId: Int, fromDateMillis: Long, toDateMillis: Long): List<Expense> = withContext(Dispatchers.IO) {
        val list = mutableListOf<Expense>()
        var connection: Connection? = null

        try {
            Class.forName("org.postgresql.Driver")
            val props = Properties()
            props.setProperty("user", USER)
            props.setProperty("password", PASS)
            props.setProperty("sslmode", "require")

            connection = DriverManager.getConnection(URL, props)

            // 🔥 ĐÃ SỬA: Thêm điều kiện 'user_id = ?' vào câu truy vấn SQL
            val query = "SELECT id, amount, description, date_time FROM expenses WHERE user_id = ? AND date_time >= ? AND date_time <= ? ORDER BY date_time DESC"

            val statement = connection.prepareStatement(query)

            // 🔥 ĐÃ SỬA: Gán giá trị userId vào dấu hỏi chấm thứ nhất
            statement.setInt(1, userId)
            statement.setTimestamp(2, Timestamp(fromDateMillis))
            statement.setTimestamp(3, Timestamp(toDateMillis))

            val resultSet = statement.executeQuery()

            while (resultSet.next()) {
                val id = resultSet.getInt("id")
                val amount = resultSet.getDouble("amount")
                val desc = resultSet.getString("description")
                val time = resultSet.getTimestamp("date_time")

                // Lưu ý: Đảm bảo thứ tự tham số này khớp với Constructor của Class Expense của bạn.
                // Nếu class Expense(id, description, amount, date) thì phải đổi vị trí amount và desc bên dưới.
                // Ở đây tôi viết theo thứ tự phổ biến: id, amount, desc, date.
                // Nếu code báo lỗi đỏ ở dòng này, hãy đảo vị trí 'amount' và 'desc ?: ...'
                list.add(Expense(id, amount, desc ?: "Không tên", time))
            }
            Log.d("PostgresHelper", ">>> History: Tải được ${list.size} dòng cho User ID: $userId")

        } catch (e: Throwable) {
            Log.e("PostgresHelper", "!!! LỖI HISTORY: ${e.message}")
            e.printStackTrace()
        } finally {
            try { connection?.close() } catch (e: Exception) {}
        }
        return@withContext list
    }
}