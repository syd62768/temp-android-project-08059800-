package com.mydesigns.hamrahekoodak.domain.repository

import com.mydesigns.hamrahekoodak.network.User
import com.mydesigns.hamrahekoodak.ui.auth.Result

/**
 * این اینترفیس، قوانین دریافت اطلاعات مورد نیاز برای داشبورد را تعریف می‌کند.
 */
interface DashboardRepository {
    /**
     * اطلاعات پروفایل فعال کاربر (کودک) را دریافت می‌کند.
     * @return یک Result که در صورت موفقیت، اطلاعات کاربر (User) را برمی‌گرداند.
     */
    suspend fun getActiveUserProfile(): Result<User>
}