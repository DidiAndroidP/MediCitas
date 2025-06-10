package com.example.medicitas.src.Features.Appointment.domain.usecase

import com.example.medicitas.src.Features.Appointment.domain.model.AppointmentDetailResult
import com.example.medicitas.src.Features.Appointment.domain.repository.AppointmentRepository

class GetAppointmentByIdUseCase(
    private val repository: AppointmentRepository
) {
    suspend operator fun invoke(
        appointmentId: Int,
        token: String
    ): AppointmentDetailResult {
        return try {
            repository.getAppointmentById(appointmentId, token)
        } catch (e: Exception) {
            AppointmentDetailResult.Error("Error inesperado: ${e.message}")
        }
    }
}