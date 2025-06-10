package com.example.medicitas.src.Features.Appointment.domain.model

sealed class AppointmentDetailResult {
    data class Success(val appointmentDetail: AppointmentDetailEntity) : AppointmentDetailResult()
    data class Error(val message: String) : AppointmentDetailResult()
    object Loading : AppointmentDetailResult()
}