package com.example.medicitas.src.Features.Login.data.datasource.remote

import com.example.medicitas.src.Features.Login.data.model.LoginRequestDto
import com.example.medicitas.src.Features.Login.data.model.LoginResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginApiService {
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequestDto): LoginResponseDto
}

