package com.example.medicitas.src.Features.Appointment.domain.model

sealed class AvailableSlotsResult {
    data class Success(val availableSlots: AvailableSlots) : AvailableSlotsResult()
    data class Error(val message: String) : AvailableSlotsResult()
}

sealed class DoctorSchedulesResult {
    data class Success(val schedules: List<DoctorSchedule>) : DoctorSchedulesResult()
    data class Error(val message: String) : DoctorSchedulesResult()
}