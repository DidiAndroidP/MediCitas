package com.example.medicitas.src.Features.Appointment.domain.model

data class CreateAppointmentRequest(
    val doctorId: Int,
    val fechaCita: String,
    val horaCita: String,
    val motivo: String,
    val notas: String? = null
)