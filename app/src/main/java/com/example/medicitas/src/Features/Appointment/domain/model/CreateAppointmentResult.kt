package com.example.medicitas.src.Features.Appointment.domain.model

sealed class CreateAppointmentResult {
    data object Loading : CreateAppointmentResult()
    data class Success(val message: String, val appointmentId: Int) : CreateAppointmentResult()
    data class Error(val message: String) : CreateAppointmentResult()
}