package com.example.medicitas.src.Features.Appointment.domain.usecase

import com.example.medicitas.src.Features.Appointment.domain.model.CreateAppointmentRequest
import com.example.medicitas.src.Features.Appointment.domain.model.CreateAppointmentResult
import com.example.medicitas.src.Features.Appointment.domain.repository.AppointmentRepository

class CreateAppointmentUseCase(
    private val repository: AppointmentRepository
) {
    suspend operator fun invoke(
        token: String,
        request: CreateAppointmentRequest
    ): CreateAppointmentResult {
        return repository.createAppointment(token, request)
    }
}