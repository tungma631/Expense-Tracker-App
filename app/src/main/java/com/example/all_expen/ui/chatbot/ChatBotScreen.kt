package com.example.all_expen.ui.chatbot

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController // <--- Import cái này
import com.example.all_expen.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatBotScreen(
    navController: NavController, // <--- THÊM DÒNG NÀY ĐỂ HẾT LỖI
    chatViewModel: ChatViewModel = viewModel()
) {

    val messages by chatViewModel.messages.collectAsState()
    val isLoading by chatViewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) { chatViewModel.sendGreeting() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0E0E0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("AI", color = Color.Gray, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Trợ lý Tài chính", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                            Text("Vừa truy cập", fontSize = 12.sp, color = Color.Gray)
                        }
                    }
                },
                navigationIcon = {
                    // Xử lý nút Back tại đây
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            // Thanh nhập liệu
            ChatInput(onSend = { chatViewModel.sendMessage(it) })
        }
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF2F5F8))
                .padding(paddingValues)
        ) {
            if (isLoading && messages.isEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = Color(0xFF0091FF))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Đang kết nối...", fontSize = 13.sp, color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 10.dp),
                    reverseLayout = false, // Sửa lại thành false để tin nhắn mới ở dưới
                    contentPadding = PaddingValues(bottom = 10.dp, top = 10.dp)
                ) {
                    items(messages) { message ->
                        ChatBubble(
                            text = message.text,
                            isUser = message.isUser,
                            time = message.time
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}