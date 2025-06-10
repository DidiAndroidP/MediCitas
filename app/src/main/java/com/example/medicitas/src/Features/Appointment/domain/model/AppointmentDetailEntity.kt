package com.example.medicitas.src.Features.Appointment.domain.model

data class AppointmentDetailEntity(
    val id: Int,
    val fechaCita: String,
    val horaCita: String,
    val estado: String,
    val motivo: String,
    val notas: String?,
    val precio: String?,
    val createAt: String,
    val updateAt: String,
    val doctor: DoctorEntity
)