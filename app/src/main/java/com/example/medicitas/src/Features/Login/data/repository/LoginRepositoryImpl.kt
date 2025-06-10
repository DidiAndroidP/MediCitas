package com.example.medicitas.src.Features.Login.data.repository

import com.example.medicitas.src.Features.Login.data.datasource.remote.LoginApiService
import com.example.medicitas.src.Features.Login.data.model.LoginRequestDto
import com.example.medicitas.src.Features.Login.domain.model.LoginCredentials
import com.example.medicitas.src.Features.Login.domain.model.LoginResult
import com.example.medicitas.src.Features.Login.domain.repository.LoginRepository
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val apiService: LoginApiService
) : LoginRepository {

    override suspend fun login(credentials: LoginCredentials): Result<LoginResult> {
        return try {
            val request = LoginRequestDto(
                correo = credentials.email,
                contrasena = credentials.password
            )

            val response = apiService.login(request)

            val loginResult = LoginResult(
                message = response.message,
                token = response.token,
                isSuccess = true
            )

            Result.success(loginResult)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}