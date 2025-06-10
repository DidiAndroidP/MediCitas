package com.example.medicitas.src.Features.Appointment.data.model

data class CreateAppointmentRequestDto(
    val doctor_id: Int,
    val fecha_cita: String,
    val hora_cita: String,
    val motivo: String,
    val notas: String? = null
)
