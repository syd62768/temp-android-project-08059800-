package com.mydesigns.hamrahekoodak.data.repository

import android.content.Context
import com.mydesigns.hamrahekoodak.data.SessionManager
import com.mydesigns.hamrahekoodak.domain.repository.ProfileRepository
import com.mydesigns.hamrahekoodak.network.UpdateProfileRequest
import com.mydesigns.hamrahekoodak.network.User
import com.mydesigns.hamrahekoodak.ui.auth.Result
import kotlinx.coroutines.delay

// حالا این کلاس به Context نیاز دارد تا بتواند SessionManager بسازد
class ProfileRepositoryImpl(private val context: Context) : ProfileRepository {

    private val sessionManager = SessionManager(context)

    override suspend fun updateUserProfile(profileData: UpdateProfileRequest): Result<User> {
        delay(1000) // شبیه‌سازی تاخیر شبکه

        val userId = sessionManager.fetchUserProfile()?.id ?: 1 // یک ID پیش‌فرض

        val updatedUser = User(
            id = userId,
            phone = "",
            name = profileData.name,
            gender = profileData.gender,
            birthDate = profileData.birthDate
        )

        // ذخیره واقعی اطلاعات در SharedPreferences
        sessionManager.saveUserProfile(updatedUser)

        return Result.Success(updatedUser)
    }
}