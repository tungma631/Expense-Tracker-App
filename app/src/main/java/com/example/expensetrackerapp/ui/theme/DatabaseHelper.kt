package com.example.expensetrackerapp.ui.theme

import android.util.Log
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement
import java.text.SimpleDateFormat
import java.util.Locale

object DatabaseHelper {

    // Thông tin kết nối lấy từ ảnh bạn gửi
    private const val HOST = "dpg-d4k8e8a4d50c73dbfq0g-a.oregon-postgres.render.com"
    private const val DATABASE = "expensetracker_db_8iqe"
    private const val USER = "mavantung"
    private const val PASS = "84X11t0GqvbdwgEaKncCZpTK30R35BLy"

    // Render bắt buộc dùng ?sslmode=require
    private const val URL = "jdbc:postgresql://$HOST:5432/$DATABASE?sslmode=require"

    fun getAllTransactions(): List<ExpenseTransaction> {
        val list = mutableListOf<ExpenseTransaction>()
        var connection: java.sql.Connection? = null

        try {
            // Nạp Driver
            Class.forName("org.postgresql.Driver")

            // Mở kết nối
            connection = DriverManager.getConnection(URL, USER, PASS)
            Log.d("DatabaseHelper", "Kết nối Database thành công!")

            val statement: Statement = connection.createStatement()
            val sql = "SELECT * FROM expenses"
            val resultSet: ResultSet = statement.executeQuery(sql)

            val dbFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val appFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            while (resultSet.next()) {
                val idStr = resultSet.getInt("id").toString()
                val rawDate = resultSet.getString("date_time") ?: ""
                val amount = resultSet.getDouble("amount")
                val description = resultSet.getString("description") ?: "Không có tên"

                // Xử lý ngày tháng
                var dateStr = "01/01/2025"
                try {
                    val cleanDate = if (rawDate.contains(".")) rawDate.split(".")[0] else rawDate
                    val dateObj = dbFormat.parse(cleanDate)
                    if (dateObj != null) {
                        dateStr = appFormat.format(dateObj)
                    }
                } catch (e: Exception) {
                    dateStr = "01/01/2025"
                }

                // Xử lý Category
                val category = guessCategory(description)

                list.add(ExpenseTransaction(
                    id = idStr,
                    title = description,
                    amount = amount,
                    category = category,
                    date = dateStr
                ))
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Lỗi SQL: ${e.message}")
            e.printStackTrace()
        } finally {
            connection?.close()
        }
        return list
    }

    private fun guessCategory(desc: String): String {
        val lower = desc.lowercase()
        return when {
            lower.contains("trà sữa") || lower.contains("ăn") || lower.contains("uống") -> "Ăn uống"
            lower.contains("xăng") || lower.contains("xe") -> "Đi lại"
            lower.contains("mua") || lower.contains("sắm") -> "Mua sắm"
            else -> "Khác"
        }
    }
}