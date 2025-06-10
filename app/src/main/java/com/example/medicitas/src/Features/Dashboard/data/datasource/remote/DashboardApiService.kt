package com.example.medicitas.src.Features.Dashboard.data.datasource.remote

import com.example.medicitas.src.Features.Dashboard.data.model.AppointmentResponseDto
import com.example.medicitas.src.Features.Dashboard.data.model.UserDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface DashboardApiService {
    @GET("api/v1/user/{id}")
    suspend fun getUserById(@Path("id") userId: Int): Response<UserDto>

    @GET("api/v1/appointment/user-appointments")
    suspend fun getUserAppointments(@Header("Authorization") token: String): Response<AppointmentResponseDto>
}
