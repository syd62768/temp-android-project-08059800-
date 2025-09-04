package com.mydesigns.hamrahekoodak.data.repository

import android.content.Context
import com.mydesigns.hamrahekoodak.data.SessionManager
import com.mydesigns.hamrahekoodak.domain.repository.DashboardRepository
import com.mydesigns.hamrahekoodak.network.User
import com.mydesigns.hamrahekoodak.ui.auth.Result
import kotlinx.coroutines.delay

class DashboardRepositoryImpl(private val context: Context) : DashboardRepository {

    private val sessionManager = SessionManager(context)

    override suspend fun getActiveUserProfile(): Result<User> {
        delay(200) // تاخیر کم برای خواندن از حافظه

        val user = sessionManager.fetchUserProfile()

        return if (user != null) {
            Result.Success(user)
        } else {
            Result.Error("پروفایلی یافت نشد. لطفاً یک پروفایل بسازید.")
        }
    }
}