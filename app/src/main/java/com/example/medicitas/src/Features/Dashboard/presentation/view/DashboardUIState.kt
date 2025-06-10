package com.example.medicitas.src.Features.Dashboard.presentation.view

import com.example.medicitas.src.Features.Dashboard.domain.model.DashboardData

data class DashboardUIState(
    val isLoading: Boolean = false,
    val isUserLoading: Boolean = false,
    val isAppointmentsLoading: Boolean = false,
    val dashboardData: DashboardData = DashboardData(),
    val error: String = "",
    val userError: String = "",
    val appointmentsError: String = ""
)