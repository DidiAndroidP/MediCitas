package com.example.medicitas.src.Features.Appointment.domain.model

data class SpecialtyEntity(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val duracionConsulta: Int,
    val precioBase: Double,
    val activo: Boolean
)