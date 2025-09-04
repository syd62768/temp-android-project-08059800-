package com.mydesigns.hamrahekoodak.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mydesigns.hamrahekoodak.PersianCalendarHelper
import com.mydesigns.hamrahekoodak.data.repository.DashboardRepositoryImpl
import com.mydesigns.hamrahekoodak.domain.repository.DashboardRepository
import com.mydesigns.hamrahekoodak.ui.auth.Result
import kotlinx.coroutines.launch

data class DashboardUiState(
    val childName: String = "",
    val childAge: String = "",
    val gender: String? = null,
    val totalAgeInDays: Long = 0,
    val birthDate: String? = null
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val dashboardRepository: DashboardRepository = DashboardRepositoryImpl(application.applicationContext)

    private val _uiState = MutableLiveData<Result<DashboardUiState>>()
    val uiState: LiveData<Result<DashboardUiState>> = _uiState

    init {
        loadUserProfile()
    }

    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = Result.Loading
            when (val result = dashboardRepository.getActiveUserProfile()) {
                is Result.Success -> {
                    val user = result.data
                    val ageText = user.birthDate?.let { PersianCalendarHelper.getAgeText(it) } ?: "نامشخص"
                    val ageInDays = user.birthDate?.let { PersianCalendarHelper.getAgeInDays(it) } ?: 0L

                    // *** شروع اصلاحیه ***
                    // در این بخش، مقدار جنسیت را استانداردسازی می‌کنیم.
                    // فرقی نمی‌کند چه مقداری ذخیره شده باشد، ما آن را به "GIRL" یا "BOY" تبدیل می‌کنیم.
                    val normalizedGender = when (user.gender?.uppercase()) {
                        "FEMALE", "F", "دختر" -> "GIRL" // مقادیر احتمالی برای دختر
                        "MALE", "M", "پسر" -> "BOY"   // مقادیر احتمالی برای پسر
                        else -> user.gender // اگر مقادیر بالا نبود، همان مقدار قبلی را نگه دار
                    }
                    // *** پایان اصلاحیه ***

                    val state = DashboardUiState(
                        childName = user.name ?: "فرشته کوچولو",
                        childAge = ageText,
                        gender = normalizedGender, // از مقدار استاندارد شده استفاده می‌کنیم
                        totalAgeInDays = ageInDays,
                        birthDate = user.birthDate
                    )
                    _uiState.value = Result.Success(state)
                }
                is Result.Error -> {
                    _uiState.value = Result.Error(result.message)
                }
                is Result.Loading -> {}
            }
        }
    }
}
