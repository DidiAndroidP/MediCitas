package com.example.medicitas.src.Features.Register.domain.usecase

import android.util.Patterns
import com.example.medicitas.src.Features.Register.data.model.RegisterRequestDto
import com.example.medicitas.src.Features.Register.domain.model.RegisterCredentials
import com.example.medicitas.src.Features.Register.domain.model.RegisterResult
import com.example.medicitas.src.Features.Register.domain.repository.RegisterRepository

class RegisterUseCase(
    private val repository: RegisterRepository
) {
    suspend operator fun invoke(credentials: RegisterCredentials): Result<RegisterResult> {

        if (credentials.nombres.isBlank()) {
            return Result.failure(Exception("El nombre es requerido"))
        }
        if (credentials.apellidos.isBlank()) {
            return Result.failure(Exception("Los apellidos son requeridos"))
        }
        if (!isValidEmail(credentials.correo)) {
            return Result.failure(Exception("El correo no es válido"))
        }
        if (credentials.contrasena.length < 6) {
            return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
        }
        if (credentials.telefono.isBlank()) {
            return Result.failure(Exception("El teléfono es requerido"))
        }
        if (credentials.edad <= 0) {
            return Result.failure(Exception("La edad debe ser mayor a 0"))
        }
        if (credentials.genero.isBlank()) {
            return Result.failure(Exception("El género es requerido"))
        }
        if (credentials.tipoSangre.isBlank()) {
            return Result.failure(Exception("El tipo de sangre es requerido"))
        }

        val requestDto = RegisterRequestDto(
            nombres = credentials.nombres,
            apellidos = credentials.apellidos,
            correo = credentials.correo,
            contrasena = credentials.contrasena,
            telefono = credentials.telefono,
            edad = credentials.edad,
            genero = credentials.genero,
            alergias = credentials.alergias,
            tipo_sangre = credentials.tipoSangre
        )

        return repository.registerUser(requestDto).fold(
            onSuccess = { response ->
                Result.success(RegisterResult(message = response.message))
            },
            onFailure = { exception ->
                Result.failure(exception)
            }
        )
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}