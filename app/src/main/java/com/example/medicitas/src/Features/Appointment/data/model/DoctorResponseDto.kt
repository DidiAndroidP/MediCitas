package com.example.medicitas.src.Features.Appointment.data.model

data class DoctorResponseDto(
    val id: Int,
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val telefono: String,
    val especialidad_id: Int,
    val duracion_consulta: Int,
    val activo: Int,
    val create_at: String,
    val update_at: String
)