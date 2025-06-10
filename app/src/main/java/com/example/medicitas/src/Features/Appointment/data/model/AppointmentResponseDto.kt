package com.example.medicitas.src.Features.Appointment.data.model

data class AppointmentResponseDto(
    val success: Boolean,
    val data: List<AppointmentDto>,
    val pagination: PaginationDto
)
