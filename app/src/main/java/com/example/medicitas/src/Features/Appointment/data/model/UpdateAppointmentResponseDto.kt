package com.example.medicitas.src.Features.Appointment.data.model

data class UpdateAppointmentResponseDto(
    val success: Boolean,
    val message: String,
    val data: AppointmentDetailDto
)