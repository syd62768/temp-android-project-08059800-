// 1. File: app/src/main/java/com/mydesigns/hamrahekoodak/domain/repository/AuthRepository.kt
package com.mydesigns.hamrahekoodak.domain.repository

import com.mydesigns.hamrahekoodak.network.AuthResponse
import com.mydesigns.hamrahekoodak.ui.auth.Result

/**
 * این اینترفیس، قوانینی را برای ارتباط با منبع داده‌های احراز هویت تعریف می‌کند.
 * این منبع می‌تواند سرور واقعی، دیتابیس محلی یا یک شبیه‌ساز (برای حالت آفلاین) باشد.
 * ViewModel ها فقط با این اینترفیس کار دارند و نمی‌دانند داده‌ها از کجا می‌آیند.
 */
interface AuthRepository {
    /**
     * درخواست ارسال کد تایید برای یک شماره تلفن.
     * @param phoneNumber شماره تلفنی که کد برای آن ارسال می‌شود.
     * @return یک Result که یا موفقیت (Unit) یا خطا را برمی‌گرداند.
     */
    suspend fun requestOtp(phoneNumber: String): Result<Unit>

    /**
     * تایید کد OTP وارد شده توسط کاربر.
     * @param phoneNumber شماره تلفن کاربر.
     * @param otpCode کد تاییدی که کاربر وارد کرده.
     * @return یک Result که در صورت موفقیت، اطلاعات کاربر (AuthResponse) و در غیر این صورت خطا را برمی‌گرداند.
     */
    suspend fun verifyOtp(phoneNumber: String, otpCode: String): Result<AuthResponse>
}