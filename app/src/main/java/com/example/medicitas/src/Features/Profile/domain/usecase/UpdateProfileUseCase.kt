package com.example.medicitas.src.Features.Profile.domain.usecase

import com.example.medicitas.src.Features.Profile.domain.model.UserProfile
import com.example.medicitas.src.Features.Profile.domain.model.UpdateUserProfile
import com.example.medicitas.src.Features.Profile.domain.repository.ProfileRepository
import com.example.medicitas.src.Features.Profile.data.model.UpdateUserProfileRequest
import java.text.SimpleDateFormat
import java.util.*

class UpdateProfileUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(
        userId: Int,
        token: String,
        updateData: UpdateUserProfile
    ): Result<UserProfile> {

        val updateRequest = UpdateUserProfileRequest(
            nombres = updateData.firstName,
            apellidos = updateData.lastName,
            correo = updateData.email,
            telefono = updateData.phone,
            edad = updateData.age,
            genero = updateData.gender,
            alergias = updateData.allergies,
            tipo_sangre = updateData.bloodType
        )

        return repository.updateUserProfile(userId, token, updateRequest).fold(
            onSuccess = { userProfileDto ->
                val userProfile = UserProfile(
                    id = userProfileDto.id,
                    fullName = "${userProfileDto.nombres} ${userProfileDto.apellidos}",
                    firstName = userProfileDto.nombres,
                    lastName = userProfileDto.apellidos,
                    email = userProfileDto.correo,
                    phone = userProfileDto.telefono,
                    age = userProfileDto.edad,
                    gender = userProfileDto.genero,
                    allergies = userProfileDto.alergias,
                    bloodType = userProfileDto.tipo_sangre,
                    memberSince = formatDate(userProfileDto.create_at)
                )
                Result.success(userProfile)
            },
            onFailure = { exception ->
                Result.failure(exception)
            }
        )
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: Date())
        } catch (e: Exception) {
            "Date not available"
        }
    }
}