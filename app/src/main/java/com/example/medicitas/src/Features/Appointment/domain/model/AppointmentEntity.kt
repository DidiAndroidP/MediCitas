package com.example.medicitas.src.Features.Appointment.domain.model

data class AppointmentEntity(
    val id: Int,
    val date: String,
    val time: String,
    val status: String,
    val reason: String,
    val notes: String?,
    val dayName: String
)