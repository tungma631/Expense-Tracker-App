package com.example.all_expen.data.model

object CurrentUser {
    var id: Int? = null
    var email: String? = null
    var name: String? = null

    // Hàm kiểm tra xem đã đăng nhập chưa
    fun isLoggedIn(): Boolean {
        return id != null
    }

    // Hàm đăng xuất
    fun logout() {
        id = null
        email = null
        name = null
    }
}