package com.example.medicitas.src.Features.Login.domain.usecase

import com.example.medicitas.src.Features.Login.domain.model.LoginCredentials
import com.example.medicitas.src.Features.Login.domain.model.LoginResult
import com.example.medicitas.src.Features.Login.domain.repository.LoginRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: LoginRepository
) {
    suspend operator fun invoke(credentials: LoginCredentials): Result<LoginResult> {
        if (credentials.email.isBlank() || credentials.password.isBlank()) {
            return Result.failure(IllegalArgumentException("Email y contraseña son requeridos"))
        }

        if (!isValidEmail(credentials.email)) {
            return Result.failure(IllegalArgumentException("Email no válido"))
        }

        return repository.login(credentials)
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}