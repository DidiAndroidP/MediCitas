package com.example.medicitas.src.Features.Appointment.domain.usecase

import com.example.medicitas.src.Features.Appointment.domain.model.SpecialtiesResult
import com.example.medicitas.src.Features.Appointment.domain.repository.AppointmentRepository

class GetSpecialtiesUseCase(
    private val repository: AppointmentRepository
) {
    suspend operator fun invoke(): SpecialtiesResult {
        return repository.getAllSpecialties()
    }
}