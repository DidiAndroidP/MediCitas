package com.example.medicitas.src.Features.Appointment.presentation.view

import com.example.medicitas.src.Features.Appointment.domain.model.AppointmentDetailEntity

data class AppointmentDetailUiState(
    val isLoading: Boolean = false,
    val appointmentDetail: AppointmentDetailEntity? = null,
    val error: String? = null
)