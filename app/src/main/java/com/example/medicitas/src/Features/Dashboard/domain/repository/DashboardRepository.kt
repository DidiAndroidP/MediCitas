package com.example.medicitas.src.Features.Dashboard.domain.repository

import com.example.medicitas.src.Features.Dashboard.data.model.AppointmentResponseDto
import com.example.medicitas.src.Features.Dashboard.data.model.UserDto

interface DashboardRepository {
    suspend fun getUserById(userId: Int): Result<UserDto>
    suspend fun getUserAppointments(token: String): Result<AppointmentResponseDto>
}