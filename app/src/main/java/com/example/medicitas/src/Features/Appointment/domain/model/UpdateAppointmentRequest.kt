package com.example.medicitas.src.Features.Appointment.domain.model

data class UpdateAppointmentRequest(
    val fechaCita: String? = null,
    val horaCita: String? = null,
    val motivo: String? = null,
    val notas: String? = null
)