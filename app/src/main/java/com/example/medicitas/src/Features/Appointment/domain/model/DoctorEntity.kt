package com.example.medicitas.src.Features.Appointment.domain.model

data class DoctorEntity(
    val id: Int,
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val telefono: String,
    val especialidadId: Int,
    val duracionConsulta: Int,
    val activo: Boolean,
    val especialidad: SpecialtyEntity? = null
) {
    val nombreCompleto: String
        get() = "Dr. $nombres $apellidos"
}