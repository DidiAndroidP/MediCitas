package com.example.medicitas.src.Features.Appointment.presentation.state

import com.example.medicitas.src.Features.Appointment.domain.model.AvailableSlots
import com.example.medicitas.src.Features.Appointment.domain.model.DoctorSchedule

data class ScheduleUiState(
    val isLoading: Boolean = false,
    val availableSlots: AvailableSlots? = null,
    val doctorSchedules: List<DoctorSchedule> = emptyList(),
    val selectedDate: String = "",
    val selectedTime: String = "",
    val error: String? = null
)