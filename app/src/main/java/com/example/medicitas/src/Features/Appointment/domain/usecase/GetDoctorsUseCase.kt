package com.example.medicitas.src.Features.Appointment.domain.usecase

import com.example.medicitas.src.Features.Appointment.domain.model.DoctorsResult
import com.example.medicitas.src.Features.Appointment.domain.repository.AppointmentRepository

class GetDoctorsUseCase(
    private val repository: AppointmentRepository
) {
    suspend operator fun invoke(): DoctorsResult {
        return repository.getAllDoctors()
    }
}