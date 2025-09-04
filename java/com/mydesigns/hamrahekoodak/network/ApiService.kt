package com.mydesigns.hamrahekoodak.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("api/auth/request-otp")
    suspend fun requestOtp(@Body request: OtpRequest): Response<Unit>

    @POST("api/auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyRequest): Response<AuthResponse>

    @PUT("api/users/{id}/profile")
    suspend fun updateUserProfile(
        @Path("id") userId: Int,
        @Body request: UpdateProfileRequest
    ): Response<User>
}