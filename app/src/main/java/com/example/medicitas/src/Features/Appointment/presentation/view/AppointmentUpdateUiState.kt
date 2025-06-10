package com.example.medicitas.src.Features.Appointment.presentation.view

import com.example.medicitas.src.Features.Appointment.domain.model.AppointmentDetailEntity

data class AppointmentUpdateUiState(
    val isLoading: Boolean = false,
    val isUpdating: Boolean = false,
    val currentAppointment: AppointmentDetailEntity? = null,
    val successMessage: String? = null,
    val error: String? = null,

    val selectedDate: String = "",
    val selectedTime: String = "",
    val motivo: String = "",
    val notas: String = ""
)