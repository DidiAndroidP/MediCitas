package com.example.medicitas.src.Features.Appointment.data.model

data class AppointmentDetailDto(
    val id: Int,
    val fecha_cita: String,
    val hora_cita: String,
    val estado: String,
    val motivo: String,
    val notas: String?,
    val precio: String?,
    val create_at: String,
    val update_at: String,
    val doctor: DoctorDto
)