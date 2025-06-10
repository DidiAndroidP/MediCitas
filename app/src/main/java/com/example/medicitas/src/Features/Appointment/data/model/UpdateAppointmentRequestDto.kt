package com.example.medicitas.src.Features.Appointment.data.model

data class UpdateAppointmentRequestDto(
    val fecha_cita: String? = null,
    val hora_cita: String? = null,
    val motivo: String? = null,
    val notas: String? = null
)