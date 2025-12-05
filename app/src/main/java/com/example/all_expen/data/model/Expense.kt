package com.example.all_expen.data.model

import java.util.Date

// Khuôn mẫu khớp với database trên Render của bạn
data class Expense(
    val id: Int,
    val amount: Double,       // Cột 'amount'
    val description: String,  // Cột 'description'
    val date: Date            // Cột 'date_time'
)