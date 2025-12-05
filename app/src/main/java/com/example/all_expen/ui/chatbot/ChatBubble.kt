package com.example.all_expen.ui.chatbot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChatBubble(text: String, isUser: Boolean, time: String) { // 🔥 Nhận thêm time
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "Bot Avatar",
                    tint = Color(0xFF0091FF),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Surface(
            color = if (isUser) Color(0xFFE5F0FF) else Color.White,
            shape = if (isUser)
                RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp)
            else
                RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp),
            shadowElevation = 1.dp,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            // 🔥 Đổi thành Column để xếp thời gian xuống dưới text
            Column(
                modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 8.dp, bottom = 4.dp)
            ) {
                Text(
                    text = text,
                    fontSize = 15.sp,
                    color = Color.Black,
                    lineHeight = 22.sp
                )

                // Hiển thị thời gian
                Text(
                    text = time,
                    fontSize = 10.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .align(Alignment.End) // Căn phải
                        .padding(top = 4.dp)
                )
            }
        }
    }
}