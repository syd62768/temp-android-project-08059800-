package com.mydesigns.hamrahekoodak.domain.repository

import com.mydesigns.hamrahekoodak.network.UpdateProfileRequest
import com.mydesigns.hamrahekoodak.network.User
import com.mydesigns.hamrahekoodak.ui.auth.Result

/**
 * این اینترفیس، قوانین ارتباط با منبع داده‌های پروفایل کاربر را تعریف می‌کند.
 */
interface ProfileRepository {
    /**
     * اطلاعات پروفایل کاربر را به‌روزرسانی یا ایجاد می‌کند.
     * @param profileData اطلاعاتی که باید ذخیره شود.
     * @return یک Result که در صورت موفقیت، اطلاعات به‌روز شده کاربر (User) را برمی‌گرداند.
     */
    suspend fun updateUserProfile(profileData: UpdateProfileRequest): Result<User>
}