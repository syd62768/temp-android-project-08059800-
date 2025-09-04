package com.mydesigns.hamrahekoodak.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mydesigns.hamrahekoodak.data.repository.ProfileRepositoryImpl
import com.mydesigns.hamrahekoodak.domain.repository.ProfileRepository
import com.mydesigns.hamrahekoodak.network.UpdateProfileRequest
import com.mydesigns.hamrahekoodak.network.User
import com.mydesigns.hamrahekoodak.ui.auth.Result
import kotlinx.coroutines.launch

// از AndroidViewModel استفاده می‌کنیم تا به Context دسترسی داشته باشیم
class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val profileRepository: ProfileRepository = ProfileRepositoryImpl(application.applicationContext)

    private val _updateStatus = MutableLiveData<Result<User>>()
    val updateStatus: LiveData<Result<User>> = _updateStatus

    fun updateUserProfile(name: String, birthDate: String, gender: String, birthWeight: Int?) {
        val request = UpdateProfileRequest(
            name = name,
            birthDate = birthDate,
            gender = gender,
            birthWeightInGrams = birthWeight
        )
        viewModelScope.launch {
            _updateStatus.value = Result.Loading
            _updateStatus.value = profileRepository.updateUserProfile(request)
        }
    }
}