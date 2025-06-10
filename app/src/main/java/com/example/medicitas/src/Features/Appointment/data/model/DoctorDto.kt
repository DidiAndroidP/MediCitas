package com.example.medicitas.src.Features.Appointment.data.model

data class DoctorDto(
    val id: Int,
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val telefono: String,
    val duracion_consulta: Int,
    val especialidad: EspecialidadDto
)