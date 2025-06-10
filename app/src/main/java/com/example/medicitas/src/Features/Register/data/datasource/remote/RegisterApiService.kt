package com.example.medicitas.src.Features.Register.data.datasource.remote

import com.example.medicitas.src.Features.Register.data.model.RegisterRequestDto
import com.example.medicitas.src.Features.Register.data.model.RegisterResponseDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RegisterApiService {
    @POST("api/v1/auth/register")
    suspend fun registerUser(@Body request: RegisterRequestDto): Response<RegisterResponseDto>
}