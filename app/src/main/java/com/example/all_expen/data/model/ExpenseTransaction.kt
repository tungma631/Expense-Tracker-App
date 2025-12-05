package com.example.all_expen.data.model

data class ExpenseTransaction(
    val id: String,
    val title: String,
    val amount: Double,
    val category: String,
    val date: String
)