package com.example.medicitas.src.Features.Appointment.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicitas.src.Features.Appointment.domain.model.*
import com.example.medicitas.src.Features.Appointment.domain.usecase.*
import com.example.medicitas.src.Features.Appointment.presentation.view.AppointmentUpdateUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppointmentUpdateViewModel(
    private val updateAppointmentUseCase: UpdateAppointmentUseCase,
    private val getAppointmentByIdUseCase: GetAppointmentByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppointmentUpdateUiState())
    val uiState: StateFlow<AppointmentUpdateUiState> = _uiState.asStateFlow()

    fun loadAppointment(appointmentId: Int, token: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                when (val result = getAppointmentByIdUseCase(appointmentId, token)) {
                    is AppointmentDetailResult.Success -> {
                        val appointment = result.appointmentDetail
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            currentAppointment = appointment,
                            selectedDate = appointment.fechaCita,
                            selectedTime = appointment.horaCita,
                            motivo = appointment.motivo,
                            notas = appointment.notas ?: ""
                        )
                    }
                    is AppointmentDetailResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                    AppointmentDetailResult.Loading -> {
                        _uiState.value = _uiState.value.copy(isLoading = true)
                    }
                }
            } catch (e: Exception) {
                Log.e("AppointmentUpdateViewModel", "Error en loadAppointment: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error inesperado: ${e.message}"
                )
            }
        }
    }

    fun updateSelectedDate(date: String) {
        try {
            _uiState.value = _uiState.value.copy(selectedDate = date)
        } catch (e: Exception) {
            Log.e("AppointmentUpdateViewModel", "Error en updateSelectedDate: ${e.message}", e)
        }
    }

    fun updateSelectedTime(time: String) {
        try {
            _uiState.value = _uiState.value.copy(selectedTime = time)
        } catch (e: Exception) {
            Log.e("AppointmentUpdateViewModel", "Error en updateSelectedTime: ${e.message}", e)
        }
    }

    fun updateMotivo(motivo: String) {
        try {
            _uiState.value = _uiState.value.copy(motivo = motivo)
        } catch (e: Exception) {
            Log.e("AppointmentUpdateViewModel", "Error en updateMotivo: ${e.message}", e)
        }
    }

    fun updateNotas(notas: String) {
        try {
            _uiState.value = _uiState.value.copy(notas = notas)
        } catch (e: Exception) {
            Log.e("AppointmentUpdateViewModel", "Error en updateNotas: ${e.message}", e)
        }
    }

    fun updateAppointment(appointmentId: Int, token: String) {
        val state = _uiState.value
        val currentAppointment = state.currentAppointment

        if (currentAppointment == null) {
            _uiState.value = state.copy(error = "No se encontró la cita a actualizar")
            return
        }

        // Verificar si hay cambios
        val hasChanges = state.selectedDate != currentAppointment.fechaCita ||
                state.selectedTime != currentAppointment.horaCita ||
                state.motivo != currentAppointment.motivo ||
                state.notas != (currentAppointment.notas ?: "")

        if (!hasChanges) {
            _uiState.value = state.copy(error = "No se detectaron cambios para actualizar")
            return
        }

        // Validaciones básicas
        if (state.selectedDate.isEmpty()) {
            _uiState.value = state.copy(error = "Debe seleccionar una fecha")
            return
        }

        if (state.selectedTime.isEmpty()) {
            _uiState.value = state.copy(error = "Debe seleccionar una hora")
            return
        }

        if (state.motivo.isEmpty()) {
            _uiState.value = state.copy(error = "Debe especificar el motivo de la consulta")
            return
        }

        viewModelScope.launch {
            try {
                _uiState.value = state.copy(isUpdating = true, error = null)

                // Crear request solo con campos que cambiaron
                val request = UpdateAppointmentRequest(
                    fechaCita = if (state.selectedDate != currentAppointment.fechaCita) state.selectedDate else null,
                    horaCita = if (state.selectedTime != currentAppointment.horaCita) state.selectedTime else null,
                    motivo = if (state.motivo != currentAppointment.motivo) state.motivo else null,
                    notas = if (state.notas != (currentAppointment.notas ?: "")) state.notas else null
                )

                when (val result = updateAppointmentUseCase(appointmentId, token, request)) {
                    is UpdateAppointmentResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isUpdating = false,
                            successMessage = result.message,
                            currentAppointment = result.updatedAppointment
                        )
                    }
                    is UpdateAppointmentResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isUpdating = false,
                            error = result.message
                        )
                    }
                    UpdateAppointmentResult.Loading -> {
                        _uiState.value = _uiState.value.copy(isUpdating = true)
                    }
                }
            } catch (e: Exception) {
                Log.e("AppointmentUpdateViewModel", "Error en updateAppointment: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isUpdating = false,
                    error = "Error inesperado: ${e.message}"
                )
            }
        }
    }

    fun resetForm() {
        try {
            val currentAppointment = _uiState.value.currentAppointment
            if (currentAppointment != null) {
                _uiState.value = _uiState.value.copy(
                    selectedDate = currentAppointment.fechaCita,
                    selectedTime = currentAppointment.horaCita,
                    motivo = currentAppointment.motivo,
                    notas = currentAppointment.notas ?: "",
                    error = null,
                    successMessage = null
                )
            }
        } catch (e: Exception) {
            Log.e("AppointmentUpdateViewModel", "Error en resetForm: ${e.message}", e)
        }
    }

    fun clearError() {
        try {
            _uiState.value = _uiState.value.copy(error = null)
        } catch (e: Exception) {
            Log.e("AppointmentUpdateViewModel", "Error en clearError: ${e.message}", e)
        }
    }

    fun clearSuccess() {
        try {
            _uiState.value = _uiState.value.copy(successMessage = null)
        } catch (e: Exception) {
            Log.e("AppointmentUpdateViewModel", "Error en clearSuccess: ${e.message}", e)
        }
    }

    fun retry(appointmentId: Int, token: String) {
        try {
            clearError()
            loadAppointment(appointmentId, token)
        } catch (e: Exception) {
            Log.e("AppointmentUpdateViewModel", "Error en retry: ${e.message}", e)
        }
    }
    fun isFormValid(): Boolean {
        val state = _uiState.value
        return state.selectedDate.isNotEmpty() &&
                state.selectedTime.isNotEmpty() &&
                state.motivo.isNotEmpty() &&
                state.motivo.length >= 10
    }
    fun hasChanges(): Boolean {
        val state = _uiState.value
        val currentAppointment = state.currentAppointment ?: return false

        return state.selectedDate != currentAppointment.fechaCita ||
                state.selectedTime != currentAppointment.horaCita ||
                state.motivo != currentAppointment.motivo ||
                state.notas != (currentAppointment.notas ?: "")
    }
}