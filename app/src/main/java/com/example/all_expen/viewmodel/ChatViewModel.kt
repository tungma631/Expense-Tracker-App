package com.example.all_expen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.all_expen.data.api.ApiClient
import com.example.all_expen.data.model.ChatMessage // Import quan trọng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.example.all_expen.data.model.CurrentUser

class ChatViewModel : ViewModel() {

    // 🔥 QUAN TRỌNG: Phải khai báo <List<ChatMessage>> rõ ràng ở đây
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun sendGreeting() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Ping nhẹ server
                ApiClient.api.query(mapOf("question" to "ping"))
            } catch (e: Exception) {
                // Ignore error
            } finally {
                // Chỉ gửi lời chào nếu danh sách đang trống
                if (_messages.value.isEmpty()) {
                    addLocalMessage("Chào bạn! Tôi là trợ lý tài chính.\nHãy nhập khoản chi (ví dụ: 'cafe 25k') để tôi ghi lại nhé!", isUser = false)
                }
                _isLoading.value = false
            }
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        addLocalMessage(text, isUser = true)

        if (containsMoney(text)) {
            analyzeExpense(text)
        } else {
            queryExpense(text)
        }
    }

    private fun addLocalMessage(text: String, isUser: Boolean) {
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
        // Thêm tin nhắn mới vào danh sách
        val newMessage = ChatMessage(text, isUser, currentTime)
        _messages.value = _messages.value + newMessage
    }

    private fun containsMoney(text: String): Boolean {
        return Regex("\\d+").containsMatchIn(text)
    }

    private fun analyzeExpense(text: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val fullDateTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                val textWithContext = "$text (thời gian: $fullDateTime)"

                // 👇 2. SỬA ĐOẠN GỌI API NÀY 👇

                // Lấy ID người dùng hiện tại
                val currentUserId = CurrentUser.id ?: -1

                // Tạo payload gửi lên Server gồm cả Text và UserID
                val payload = mapOf(
                    "text" to textWithContext,
                    "user_id" to currentUserId // <--- QUAN TRỌNG: Gửi ID để Server biết mà lưu
                )

                // Gọi API với payload mới
                val res = ApiClient.api.analyze(payload as Map<String, String>)

                addLocalMessage(res.reply, isUser = false)
            } catch (e: Exception) {
                addLocalMessage("Lỗi kết nối: ${e.message}", isUser = false)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun queryExpense(text: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val res = ApiClient.api.query(mapOf("question" to text))
                val rows = res.rows
                val answerText = when {
                    rows.isNullOrEmpty() -> "Không tìm thấy dữ liệu."
                    rows[0].isNotEmpty() && rows[0][0] != null -> "Kết quả: ${rows[0][0]}"
                    else -> "Không có kết quả."
                }
                addLocalMessage(answerText, isUser = false)
            } catch (e: Exception) {
                addLocalMessage("Tôi chưa hiểu ý bạn, hoặc lỗi server: ${e.message}", isUser = false)
            } finally {
                _isLoading.value = false
            }
        }
    }
}