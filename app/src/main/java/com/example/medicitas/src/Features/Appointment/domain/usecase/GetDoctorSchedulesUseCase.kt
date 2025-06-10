package com.example.medicitas.src.Features.Appointment.domain.usecase

import com.example.medicitas.src.Features.Appointment.domain.model.DoctorSchedulesResult
import com.example.medicitas.src.Features.Appointment.domain.repository.AppointmentRepository

class GetDoctorSchedulesUseCase(
    private val appointmentRepository: AppointmentRepository
) {
    suspend operator fun invoke(doctorId: Int, token: String): DoctorSchedulesResult {
        if (doctorId <= 0) {
            return DoctorSchedulesResult.Error("ID del doctor inválido")
        }

        return try {
            appointmentRepository.getDoctorSchedules(doctorId, token)
        } catch (e: Exception) {
            DoctorSchedulesResult.Error("Error inesperado: ${e.message}")
        }
    }
}