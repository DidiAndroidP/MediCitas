package com.example.medicitas.src.Features.Appointment.data.model

data class SpecialtyResponseDto(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val duracion_consulta: Int,
    val precio_base: Double,
    val activo: Boolean,
    val create_at: String,
    val update_at: String
)