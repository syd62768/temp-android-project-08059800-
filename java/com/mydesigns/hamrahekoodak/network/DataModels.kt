package com.mydesigns.hamrahekoodak.network

import com.google.gson.annotations.SerializedName

/**
 * مدل داده برای ارسال شماره تلفن جهت دریافت کد تایید.
 */
data class OtpRequest(
    @SerializedName("phone_number") val phoneNumber: String
)

/**
 * مدل داده برای ارسال شماره تلفن و کد تایید جهت ورود.
 */
data class VerifyRequest(
    @SerializedName("phone_number") val phoneNumber: String,
    @SerializedName("otp") val otp: String
)

/**
 * مدل داده پاسخ سرور پس از تایید موفق کد.
 * شامل توکن، اطلاعات کاربر و وضعیتی برای تشخیص کاربر جدید است.
 */
data class AuthResponse(
    @SerializedName("token") val token: String,
    @SerializedName("user") val user: User,
    @SerializedName("is_new_user") val isNewUser: Boolean
)

/**
 * مدل داده اصلی برای نگهداری اطلاعات کاربر.
 */
data class User(
    @SerializedName("id") val id: Int,
    @SerializedName("phone") val phone: String,
    @SerializedName("name") val name: String?,
    @SerializedName("gender") val gender: String?,
    @SerializedName("birth_date") val birthDate: String?
    // در آینده فیلدهای دیگر مثل وزن و قد هم می‌توانند اینجا اضافه شوند
)

/**
 * مدل داده برای ارسال اطلاعات پروفایل جدید یا به‌روزرسانی شده به سرور.
 */
data class UpdateProfileRequest(
    @SerializedName("name") val name: String,
    @SerializedName("gender") val gender: String,
    @SerializedName("birth_date") val birthDate: String,
    @SerializedName("birth_weight_in_grams") val birthWeightInGrams: Int?
)