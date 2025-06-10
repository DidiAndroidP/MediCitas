package com.example.medicitas.src.Features.Profile.domain.usecase

import com.example.medicitas.src.Features.Profile.domain.model.UserProfile
import com.example.medicitas.src.Features.Profile.domain.repository.ProfileRepository
import java.text.SimpleDateFormat
import java.util.*

class ProfileUseCase(
    private val repository: ProfileRepository
) {
    suspend operator fun invoke(userId: Int, token: String): Result<UserProfile> {
        return repository.getUserProfile(userId, token).fold(
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
            "Fecha no disponible"
        }
    }
}