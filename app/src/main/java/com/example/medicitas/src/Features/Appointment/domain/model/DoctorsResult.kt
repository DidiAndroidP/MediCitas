package com.example.medicitas.src.Features.Appointment.domain.model

sealed class DoctorsResult {
    data object Loading : DoctorsResult()
    data class Success(val doctors: List<DoctorEntity>) : DoctorsResult()
    data class Error(val message: String) : DoctorsResult()
}