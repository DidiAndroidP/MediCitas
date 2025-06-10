package com.example.medicitas.src.Features.Appointment.data.model

data class AvailableSlotsResponseDto(
    val success: Boolean,
    val data: AvailableSlotsDataDto
)

data class AvailableSlotsDataDto(
    val fecha: String,
    val doctor_id: String,
    val slots: List<TimeSlotDto>,
    val resumen: SlotSummaryDto
)

data class TimeSlotDto(
    val hora: String,
    val estado: String,
    val disponible: Boolean
)

data class SlotSummaryDto(
    val total: Int,
    val disponibles: Int,
    val ocupados: Int
)
