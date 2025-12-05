package com.example.all_expen.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onSave: (String, Double, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    val category = "Khác"

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text("Thêm khoản chi", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2196F3))
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Nội dung") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("Số tiền") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Hủy", color = Color.Gray) }
                    Button(onClick = {
                        val amountVal = amount.toDoubleOrNull() ?: 0.0
                        if (title.isNotEmpty() && amountVal > 0.0) {
                            onSave(title, amountVal, category)
                            onDismiss()
                        }
                    }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))) { Text("Lưu") }
                }
            }
        }
    }
}