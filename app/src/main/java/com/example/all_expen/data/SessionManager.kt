//package com.example.all_expen.data
//
//import android.content.Context
//import android.content.SharedPreferences
//
//class SessionManager(context: Context) {
//    private val prefs: SharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
//
//    companion object {
//        private const val KEY_IS_FIRST_TIME = "is_first_time"
//        private const val KEY_IS_LOGGED_IN = "is_logged_in"
//        private const val KEY_USER_NAME = "user_name"
//    }
//
//    // 1. Kiểm tra xem có phải lần đầu mở app không
//    fun isFirstTime(): Boolean {
//        return prefs.getBoolean(KEY_IS_FIRST_TIME, true)
//    }
//
//    // Lưu lại là đã xem hướng dẫn rồi
//    fun setFinishedGuide() {
//        prefs.edit().putBoolean(KEY_IS_FIRST_TIME, false).apply()
//    }
//
//    // 2. Kiểm tra đã đăng nhập chưa
//    fun isLoggedIn(): Boolean {
//        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
//    }
//
//    // Lưu trạng thái đăng nhập thành công
//    fun saveLoginSession(name: String) {
//        val editor = prefs.edit()
//        editor.putBoolean(KEY_IS_LOGGED_IN, true)
//        editor.putString(KEY_USER_NAME, name)
//        editor.apply()
//    }
//
//    // 3. Đăng xuất (Logout)
//    fun logout() {
//        val editor = prefs.edit()
//        editor.putBoolean(KEY_IS_LOGGED_IN, false)
//        editor.remove(KEY_USER_NAME)
//        editor.apply()
//    }
//
//    fun getUserName(): String? {
//        return prefs.getString(KEY_USER_NAME, "User")
//    }
//}
package com.example.all_expen.data

import android.content.Context
import android.content.SharedPreferences
import com.example.all_expen.data.model.CurrentUser

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    private val editor = prefs.edit()

    companion object {
        const val KEY_IS_LOGGED_IN = "is_logged_in"
        const val KEY_USER_ID = "user_id"
        const val KEY_USER_NAME = "user_name"
        const val KEY_USER_EMAIL = "user_email"
        // Key mới để lưu trạng thái lần đầu mở app
        const val KEY_IS_FIRST_TIME = "is_first_time"
    }

    // --- 1. CÁC HÀM XỬ LÝ LOGIN (Giữ nguyên) ---
    fun saveLoginSession(id: Int, name: String, email: String) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putInt(KEY_USER_ID, id)
        editor.putString(KEY_USER_NAME, name)
        editor.putString(KEY_USER_EMAIL, email)
        editor.apply()

        // Cập nhật RAM
        CurrentUser.id = id
        CurrentUser.name = name
        CurrentUser.email = email
    }

    fun restoreUserSession() {
        if (isLoggedIn()) {
            val id = prefs.getInt(KEY_USER_ID, -1)
            val name = prefs.getString(KEY_USER_NAME, "User")
            val email = prefs.getString(KEY_USER_EMAIL, "")

            if (id != -1) {
                CurrentUser.id = id
                CurrentUser.name = name
                CurrentUser.email = email
            }
        }
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun logout() {
        editor.clear()
        editor.apply()
        CurrentUser.logout()
    }

    // --- 2. CÁC HÀM MỚI ĐỂ FIX LỖI (Quan trọng) ---

    // Hàm này sửa lỗi trong AppNavigation.kt
    fun isFirstTime(): Boolean {
        return prefs.getBoolean(KEY_IS_FIRST_TIME, true)
    }

    // Hàm này sửa lỗi trong GuideScreen.kt
    // (Lưu ý: Tên hàm phải chính xác là setFinishedGuide)
    fun setFinishedGuide() {
        editor.putBoolean(KEY_IS_FIRST_TIME, false)
        editor.apply()
    }
}