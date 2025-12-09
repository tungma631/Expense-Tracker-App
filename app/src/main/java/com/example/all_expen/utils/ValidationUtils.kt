package com.example.all_expen.utils

object ValidationUtils {

    // 1. Kiểm tra Username (Không dấu cách, không rỗng)
    fun getUsernameError(username: String): String? {
        return when {
            username.isBlank() -> "Tên đăng nhập không được để trống"
            username.contains(" ") -> "Tên đăng nhập không được chứa khoảng trắng"
            username.length < 4 -> "Tên đăng nhập quá ngắn (tối thiểu 4 ký tự)"
            else -> null
        }
    }


    fun getPasswordError(password: String): String? {
        return when {
            password.length < 6 -> "Mật khẩu quá ngắn (tối thiểu 6 ký tự)"
            !password.any { it.isDigit() } -> "Mật khẩu phải chứa ít nhất 1 số"
            !password.any { it.isLetter() } -> "Mật khẩu phải chứa ít nhất 1 chữ cái"
            else -> null
        }
    }
}