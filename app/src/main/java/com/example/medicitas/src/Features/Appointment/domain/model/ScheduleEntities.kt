package com.example.medicitas.src.Features.Appointment.domain.model

data class AvailableSlots(
    val fecha: String,
    val doctorId: Int,
    val slots: List<TimeSlot>,
    val resumen: SlotSummary
)

data class TimeSlot(
    val hora: String,
    val estado: String,
    val disponible: Boolean
)

data class SlotSummary(
    val total: Int,
    val disponibles: Int,
    val ocupados: Int
)

data class DoctorSchedule(
    val id: Int,
    val doctorId: Int,
    val diaSemana: Int,
    val diaNombre: String,
    val horaInicio: String,
    val horaFin: String,
    val activo: Boolean
)