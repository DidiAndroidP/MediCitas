package com.example.medicitas.src.Features.Appointment.data.model

data class DoctorScheduleDto(
    val id: Int,
    val doctor_id: Int,
    val dia_semana: Int,
    val dia_nombre: String,
    val hora_inicio: String,
    val hora_fin: String,
    val activo: Boolean,
    val create_at: String,
    val update_at: String
)