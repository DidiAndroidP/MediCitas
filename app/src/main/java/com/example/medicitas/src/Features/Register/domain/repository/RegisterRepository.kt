package com.example.medicitas.src.Features.Register.domain.repository

import com.example.medicitas.src.Features.Register.data.model.RegisterRequestDto
import com.example.medicitas.src.Features.Register.data.model.RegisterResponseDto

interface RegisterRepository {
    suspend fun registerUser(request: RegisterRequestDto): Result<RegisterResponseDto>
}