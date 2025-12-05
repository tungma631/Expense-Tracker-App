package com.example.all_expen.data.model

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val time: String // 🔥 Thêm trường này
)

data class AnalyzeResponse(
    val status: String,
    val reply: String
)

data class ExpenseSavedItem(
    val amount: Double?,
    val date_time: String?,
    val description: String?,
    val category: String?
)

data class QueryResponse(
    val status: String,
    val sql: String,
    val rows: List<List<Any?>>
)