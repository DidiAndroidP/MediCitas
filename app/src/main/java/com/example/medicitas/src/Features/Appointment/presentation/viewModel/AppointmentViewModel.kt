package com.example.medicitas.src.Features.Appointment.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicitas.src.Features.Appointment.domain.model.AppointmentResult
import com.example.medicitas.src.Features.Appointment.domain.usecase.GetUserAppointmentsUseCase
import com.example.medicitas.src.Features.Appointment.presentation.view.AppointmentUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppointmentViewModel(
    private val getUserAppointmentsUseCase: GetUserAppointmentsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppointmentUiState())
    val uiState: StateFlow<AppointmentUiState> = _uiState.asStateFlow()

    // Variable simple para controlar cargas múltiples
    private var isCurrentlyLoading = false

    fun loadAppointments(token: String) {
        // Solo protección básica: no cargar si ya se está cargando
        if (isCurrentlyLoading) {
            println("⚠️ Ya se está cargando, ignorando solicitud duplicada")
            return
        }

        println("✅ Iniciando carga de citas para token: ${token.take(10)}...")

        viewModelScope.launch {
            isCurrentlyLoading = true

            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = getUserAppointmentsUseCase(token)) {
                is AppointmentResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        appointments = result.appointments,
                        error = null
                    )
                    println("✅ Citas cargadas exitosamente: ${result.appointments.size} citas")
                }

                is AppointmentResult.Error -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = result.message
                    )
                    println("❌ Error al cargar citas: ${result.message}")
                }

                AppointmentResult.Loading -> {
                    _uiState.value = _uiState.value.copy(isLoading = true)
                }
            }

            isCurrentlyLoading = false
        }
    }

    fun retry(token: String) {
        isCurrentlyLoading = false
        loadAppointments(token)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}