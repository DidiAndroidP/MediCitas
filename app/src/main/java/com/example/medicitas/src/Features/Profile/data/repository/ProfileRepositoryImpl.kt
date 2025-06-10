package com.example.medicitas.src.Features.Profile.data.repository

import com.example.medicitas.src.Features.Profile.data.datasource.remote.ProfileApiService
import com.example.medicitas.src.Features.Profile.data.model.UserProfileDto
import com.example.medicitas.src.Features.Profile.data.model.UpdateUserProfileRequest
import com.example.medicitas.src.Features.Profile.domain.repository.ProfileRepository

class ProfileRepositoryImpl(
    private val profileApiService: ProfileApiService
) : ProfileRepository {

    override suspend fun getUserProfile(userId: Int, token: String): Result<UserProfileDto> {
        return try {
            val response = profileApiService.getUserProfile(userId, "Bearer $token")

            if (response.isSuccessful) {
                response.body()?.let { userProfile ->
                    Result.success(userProfile)
                } ?: Result.failure(Exception("Perfil no encontrado"))
            } else {
                val errorMessage = when (response.code()) {
                    403 -> "No tienes permisos para ver este perfil"
                    404 -> "Usuario no encontrado"
                    else -> "Error ${response.code()}: ${response.message()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserProfile(
        userId: Int,
        token: String,
        updateRequest: UpdateUserProfileRequest
    ): Result<UserProfileDto> {
        return try {
            val response = profileApiService.updateUserProfile(
                userId = userId,
                token = "Bearer $token",
                updateRequest = updateRequest
            )

            if (response.isSuccessful) {
                response.body()?.let { updatedProfile ->
                    Result.success(updatedProfile)
                } ?: Result.failure(Exception("Error al procesar la respuesta del servidor"))
            } else {
                val errorMessage = when (response.code()) {
                    400 -> "Datos inválidos. Verifica la información ingresada"
                    401 -> "No autorizado. Token inválido o expirado"
                    403 -> "No tienes permisos para actualizar este perfil"
                    404 -> "Usuario no encontrado"
                    422 -> "Error de validación. Verifica los datos"
                    else -> "Error ${response.code()}: ${response.message()}"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}