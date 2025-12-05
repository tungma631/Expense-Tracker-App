package com.example.all_expen.ui.chatbot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.text.isNotBlank

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatInput(onSend: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    // Thanh nền trắng ở đáy
    Surface(
        color = Color.White,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Nút dấu cộng (Giả lập tính năng thêm ảnh)
            IconButton(onClick = { /* Mở menu */ }) {
                Icon(Icons.Default.Add, contentDescription = "More", tint = Color.Gray)
            }

            // Ô nhập liệu
            TextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("Nhập tin nhắn...", color = Color.Gray) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5), // Xám rất nhạt
                    unfocusedContainerColor = Color(0xFFF5F5F5),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color(0xFF0091FF)
                ),
                shape = RoundedCornerShape(24.dp), // Bo tròn hoàn toàn
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 48.dp),
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Nút gửi (Chỉ hiện màu xanh khi có chữ)
            val isTyping = text.isNotBlank()
            IconButton(
                onClick = {
                    if (isTyping) {
                        onSend(text)
                        text = ""
                    }
                },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (isTyping) Color(0xFF0091FF) else Color.LightGray
                )
            }
        }
    }
}