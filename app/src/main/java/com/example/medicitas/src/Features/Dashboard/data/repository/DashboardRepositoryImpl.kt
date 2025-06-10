package com.example.medicitas.src.Features.Dashboard.data.repository

import com.example.medicitas.src.Features.Dashboard.data.datasource.remote.DashboardApiService
import com.example.medicitas.src.Features.Dashboard.data.model.AppointmentResponseDto
import com.example.medicitas.src.Features.Dashboard.data.model.UserDto
import com.example.medicitas.src.Features.Dashboard.domain.repository.DashboardRepository

class DashboardRepositoryImpl(
    private val dashboardApiService: DashboardApiService
) : DashboardRepository {

    override suspend fun getUserById(userId: Int): Result<UserDto> {
        return try {
            val response = dashboardApiService.getUserById(userId)

            if (response.isSuccessful) {
                response.body()?.let { user ->
                    Result.success(user)
                } ?: Result.failure(Exception("Usuario no encontrado"))
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserAppointments(token: String): Result<AppointmentResponseDto> {
        return try {
            val response = dashboardApiService.getUserAppointments("Bearer $token")

            if (response.isSuccessful) {
                response.body()?.let { appointments ->
                    Result.success(appointments)
                } ?: Result.failure(Exception("No se pudieron cargar las citas"))
            } else {
                Result.failure(Exception("Error ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}