// 2. File: app/src/main/java/com/mydesigns/hamrahekoodak/data/repository/AuthRepositoryImpl.kt
package com.mydesigns.hamrahekoodak.data.repository

import com.mydesigns.hamrahekoodak.domain.repository.AuthRepository
import com.mydesigns.hamrahekoodak.network.AuthResponse
import com.mydesigns.hamrahekoodak.network.User
import com.mydesigns.hamrahekoodak.ui.auth.Result
import kotlinx.coroutines.delay

/**
 * این کلاس، پیاده‌سازی واقعی AuthRepository است.
 * در حال حاضر، این یک پیاده‌سازی "Fake" یا "آفلاین" است.
 * به جای تماس با سرور واقعی، با یک تاخیر کوتاه، پاسخ موفقیت‌آمیز را شبیه‌سازی می‌کند.
 * در آینده، شما به راحتی می‌توانید کدهای مربوط به Retrofit را به اینجا منتقل کنید.
 */
class AuthRepositoryImpl : AuthRepository {

    override suspend fun requestOtp(phoneNumber: String): Result<Unit> {
        // شبیه‌سازی تاخیر شبکه
        delay(1500) // 1.5 ثانیه صبر می‌کند

        // همیشه پاسخ موفقیت‌آمیز برمی‌گرداند
        return Result.Success(Unit)
        // برای تست حالت خطا می‌توانید این خط را جایگزین کنید:
        // return Result.Error("این یک خطای تستی است.")
    }

    override suspend fun verifyOtp(phoneNumber: String, otpCode: String): Result<AuthResponse> {
        // شبیه‌سازی تاخیر شبکه
        delay(1500)

        // اینجا می‌توانید یک منطق ساده برای کد تایید بگذارید
        return if (otpCode == "1234") { // برای تست، کد صحیح را "1234" در نظر می‌گیریم
            // یک پاسخ موفقیت‌آمیز با اطلاعات کاربر جعلی می‌سازیم
            val fakeUser = User(id = 1, name = null, phone = phoneNumber, gender = null, birthDate = null)
            val fakeAuthResponse = AuthResponse(token = "fake_auth_token_for_test", user = fakeUser, isNewUser = true)
            Result.Success(fakeAuthResponse)
        } else {
            Result.Error("کد وارد شده صحیح نیست.")
        }
    }
}