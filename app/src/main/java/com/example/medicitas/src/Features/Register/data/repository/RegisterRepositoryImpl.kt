package com.example.medicitas.src.Features.Register.data.repository

import com.example.medicitas.src.Features.Register.data.datasource.remote.RegisterApiService
import com.example.medicitas.src.Features.Register.data.model.RegisterRequestDto
import com.example.medicitas.src.Features.Register.data.model.RegisterResponseDto
import com.example.medicitas.src.Features.Register.domain.repository.RegisterRepository

class RegisterRepositoryImpl(
    private val registerApiService: RegisterApiService
) : RegisterRepository {

    override suspend fun registerUser(request: RegisterRequestDto): Result<RegisterResponseDto> {
        return try {
            val response = registerApiService.registerUser(request)

            if (response.isSuccessful) {
                response.body()?.let { registerResponse ->
                    Result.success(registerResponse)
                } ?: Result.failure(Exception("Respuesta vacía del servidor"))
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("Error ${response.code()}: ${errorBody ?: response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}