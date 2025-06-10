package com.example.medicitas.src.Features.Appointment.domain.usecase
import com.example.medicitas.src.Features.Appointment.domain.model.AppointmentResult
import com.example.medicitas.src.Features.Appointment.domain.repository.AppointmentRepository

class GetUserAppointmentsUseCase(
    private val repository: AppointmentRepository
) {
    suspend operator fun invoke(
        token: String,
        page: Int = 1,
        limit: Int = 10
    ): AppointmentResult {
        return repository.getUserAppointments(token, page, limit)
    }
}