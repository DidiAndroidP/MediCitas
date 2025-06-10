package com.example.medicitas.src.Features.Profile.domain.repository

import com.example.medicitas.src.Features.Profile.data.model.UserProfileDto
import com.example.medicitas.src.Features.Profile.data.model.UpdateUserProfileRequest

interface ProfileRepository {
    suspend fun getUserProfile(userId: Int, token: String): Result<UserProfileDto>

    suspend fun updateUserProfile(
        userId: Int,
        token: String,
        updateRequest: UpdateUserProfileRequest
    ): Result<UserProfileDto>
}