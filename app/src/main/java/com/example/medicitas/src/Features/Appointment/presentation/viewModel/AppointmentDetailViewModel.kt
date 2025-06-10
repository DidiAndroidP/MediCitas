package com.example.medicitas.src.Features.Appointment.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicitas.src.Features.Appointment.domain.model.AppointmentDetailResult
import com.example.medicitas.src.Features.Appointment.domain.usecase.GetAppointmentByIdUseCase
import com.example.medicitas.src.Features.Appointment.presentation.view.AppointmentDetailUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppointmentDetailViewModel(
    private val getAppointmentByIdUseCase: GetAppointmentByIdUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppointmentDetailUiState())
    val uiState: StateFlow<AppointmentDetailUiState> = _uiState.asStateFlow()

    fun loadAppointmentDetail(appointmentId: Int, token: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            when (val result = getAppointmentByIdUseCase(appointmentId, token)) {
                is AppointmentDetailResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        appointmentDetail = result.appointmentDetail,
                        error = null
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
        }
    }

    fun retry(appointmentId: Int, token: String) {
        loadAppointmentDetail(appointmentId, token)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}