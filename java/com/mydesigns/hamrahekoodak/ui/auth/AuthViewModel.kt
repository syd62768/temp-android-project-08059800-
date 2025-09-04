package com.mydesigns.hamrahekoodak.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mydesigns.hamrahekoodak.data.repository.AuthRepositoryImpl
import com.mydesigns.hamrahekoodak.domain.repository.AuthRepository
import com.mydesigns.hamrahekoodak.network.AuthResponse
import kotlinx.coroutines.launch

// کلاس Result بدون تغییر باقی می‌ماند
sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val message: String) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

class AuthViewModel : ViewModel() {

    private val authRepository: AuthRepository = AuthRepositoryImpl()

    // LiveData ها را Nullable تعریف می‌کنیم تا بتوانیم آنها را ریست کنیم
    private val _otpRequestStatus = MutableLiveData<Result<Unit>?>()
    val otpRequestStatus: LiveData<Result<Unit>?> = _otpRequestStatus

    private val _verifyStatus = MutableLiveData<Result<AuthResponse>?>()
    val verifyStatus: LiveData<Result<AuthResponse>?> = _verifyStatus

    fun requestOtp(phoneNumber: String) {
        viewModelScope.launch {
            _otpRequestStatus.value = Result.Loading
            _otpRequestStatus.value = authRepository.requestOtp(phoneNumber)
        }
    }

    fun verifyOtp(phoneNumber: String, otpCode: String) {
        viewModelScope.launch {
            _verifyStatus.value = Result.Loading
            _verifyStatus.value = authRepository.verifyOtp(phoneNumber, otpCode)
        }
    }

    /**
     * *** اصلاح اصلی: این تابع به داخل کلاس منتقل شد ***
     * این متد وضعیت‌های LiveData را ریست می‌کند تا از اجرای دوباره ناخواسته جلوگیری شود.
     */
    fun resetStates() {
        _otpRequestStatus.value = null
        _verifyStatus.value = null
    }
}
