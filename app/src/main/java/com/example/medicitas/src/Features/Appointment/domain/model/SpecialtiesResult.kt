package com.example.medicitas.src.Features.Appointment.domain.model

sealed class SpecialtiesResult {
    data object Loading : SpecialtiesResult()
    data class Success(val specialties: List<SpecialtyEntity>) : SpecialtiesResult()
    data class Error(val message: String) : SpecialtiesResult()
}