package com.example.medicitas.src.Features.Appointment.data.model

data class AppointmentDto(
    val id: Int,
    val `fecha_cita`: String,
    val `hora_cita`: String,
    val `estado`: String,
    val motivo: String,
    val notas: String?,
    val `create_at`: String,
    val `update_at`: String,
    val `fecha_formateada`: String,
    val `hora_formateada`: String,
    val `estado_display`: String,
    val `dia_nombre`: String
)