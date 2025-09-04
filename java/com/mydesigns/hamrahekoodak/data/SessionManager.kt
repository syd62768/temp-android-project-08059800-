package com.mydesigns.hamrahekoodak.data

import android.content.Context
import android.content.SharedPreferences
import com.mydesigns.hamrahekoodak.network.User

class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    companion object {
        const val AUTH_TOKEN = "auth_token"
        const val USER_ID = "user_id"
        // کلیدهای جدید برای ذخیره اطلاعات پروفایل
        const val CHILD_NAME = "child_name"
        const val CHILD_BIRTH_DATE = "child_birth_date"
        const val CHILD_GENDER = "child_gender"
    }

    fun saveAuthToken(token: String) {
        prefs.edit().putString(AUTH_TOKEN, token).apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(AUTH_TOKEN, null)
    }

    /**
     * ذخیره کامل پروفایل کاربر در SharedPreferences.
     * این متد هم برای ساخت پروفایل جدید و هم برای آپدیت آن استفاده می‌شود.
     */
    fun saveUserProfile(user: User) {
        val editor = prefs.edit()
        editor.putInt(USER_ID, user.id)
        editor.putString(CHILD_NAME, user.name)
        editor.putString(CHILD_BIRTH_DATE, user.birthDate)
        editor.putString(CHILD_GENDER, user.gender)
        editor.apply()
    }

    /**
     * خواندن اطلاعات پروفایل ذخیره شده از SharedPreferences.
     * اگر پروفایلی وجود نداشته باشد، null برمی‌گرداند.
     */
    fun fetchUserProfile(): User? {
        val userId = prefs.getInt(USER_ID, -1)
        if (userId == -1) {
            return null // هیچ کاربری ذخیره نشده است
        }
        return User(
            id = userId,
            phone = "", // شماره تلفن در اینجا ضروری نیست
            name = prefs.getString(CHILD_NAME, null),
            gender = prefs.getString(CHILD_GENDER, null),
            birthDate = prefs.getString(CHILD_BIRTH_DATE, null)
        )
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}