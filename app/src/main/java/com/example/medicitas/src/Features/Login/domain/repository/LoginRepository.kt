package com.example.medicitas.src.Features.Login.domain.repository

import com.example.medicitas.src.Features.Login.domain.model.LoginCredentials
import com.example.medicitas.src.Features.Login.domain.model.LoginResult

interface LoginRepository {
    suspend fun login(credentials: LoginCredentials): Result<LoginResult>
}