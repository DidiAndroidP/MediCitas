package com.example.medicitas.src.Features.Appointment.domain.model

sealed class UpdateAppointmentResult {
    object Loading : UpdateAppointmentResult()
    data class Success(
        val message: String,
        val updatedAppointment: AppointmentDetailEntity
    ) : UpdateAppointmentResult()
    data class Error(val message: String) : UpdateAppointmentResult()
}