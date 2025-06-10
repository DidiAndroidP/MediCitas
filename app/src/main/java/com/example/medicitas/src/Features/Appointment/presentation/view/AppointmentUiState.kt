package com.example.medicitas.src.Features.Appointment.presentation.view

import com.example.medicitas.src.Features.Appointment.domain.model.AppointmentEntity

data class AppointmentUiState(
    val isLoading: Boolean = false,
    val appointments: List<AppointmentEntity> = emptyList(),
    val error: String? = null
) {
    val upcomingAppointments: List<AppointmentEntity>
        get() = appointments.filter { appointment ->
            appointment.status.lowercase() in listOf("programada", "confirmada")
        }

    val completedAppointments: List<AppointmentEntity>
        get() = appointments.filter { appointment ->
            appointment.status.lowercase() in listOf("completada", "finalizada", "realizada")
        }
}