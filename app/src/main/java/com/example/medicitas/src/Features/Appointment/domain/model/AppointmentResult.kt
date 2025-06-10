package com.example.medicitas.src.Features.Appointment.domain.model

sealed class AppointmentResult {
    data class Success(val appointments: List<AppointmentEntity>) : AppointmentResult()
    data class Error(val message: String) : AppointmentResult()
    object Loading : AppointmentResult()
}