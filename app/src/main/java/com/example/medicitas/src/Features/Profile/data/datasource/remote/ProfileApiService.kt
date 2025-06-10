package com.example.medicitas.src.Features.Profile.data.datasource.remote

import com.example.medicitas.src.Features.Profile.data.model.UserProfileDto
import com.example.medicitas.src.Features.Profile.data.model.UpdateUserProfileRequest
import retrofit2.Response
import retrofit2.http.*

interface ProfileApiService {
    @GET("api/v1/user/{id}")
    suspend fun getUserProfile(
        @Path("id") userId: Int,
        @Header("Authorization") token: String
    ): Response<UserProfileDto>

    @PUT("api/v1/user/update/{id}")
    suspend fun updateUserProfile(
        @Path("id") userId: Int,
        @Header("Authorization") token: String,
        @Body updateRequest: UpdateUserProfileRequest
    ): Response<UserProfileDto>
}