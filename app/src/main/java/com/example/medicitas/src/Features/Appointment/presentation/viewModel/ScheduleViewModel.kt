package com.example.medicitas.src.Features.Appointment.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicitas.src.Features.Appointment.domain.model.*
import com.example.medicitas.src.Features.Appointment.domain.usecase.*
import com.example.medicitas.src.Features.Appointment.presentation.state.ScheduleUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScheduleViewModel(
    private val getAvailableSlotsUseCase: GetAvailableSlotsUseCase,
    private val getDoctorSchedulesUseCase: GetDoctorSchedulesUseCase,
    private val validateAppointmentTimeUseCase: ValidateAppointmentTimeUseCase,
    private val checkDoctorAvailabilityUseCase: CheckDoctorAvailabilityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    fun loadDoctorSchedules(doctorId: Int, token: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val result = getDoctorSchedulesUseCase(doctorId, token)

                when (result) {
                    is DoctorSchedulesResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            doctorSchedules = result.schedules,
                            error = null
                        )
                    }
                    is DoctorSchedulesResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("ScheduleViewModel", "Error en loadDoctorSchedules: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error inesperado: ${e.message}"
                )
            }
        }
    }

    fun loadAvailableSlots(doctorId: Int, fecha: String, token: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val result = getAvailableSlotsUseCase(doctorId, fecha, token)

                when (result) {
                    is AvailableSlotsResult.Success -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            availableSlots = result.availableSlots,
                            selectedDate = fecha,
                            error = null
                        )
                    }
                    is AvailableSlotsResult.Error -> {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e("ScheduleViewModel", "Error en loadAvailableSlots: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error inesperado: ${e.message}"
                )
            }
        }
    }

    fun selectTime(time: String) {
        try {
            _uiState.value = _uiState.value.copy(selectedTime = time)
        } catch (e: Exception) {
            Log.e("ScheduleViewModel", "Error en selectTime: ${e.message}", e)
        }
    }

    fun clearSelection() {
        try {
            _uiState.value = _uiState.value.copy(
                selectedTime = "",
                selectedDate = "",
                availableSlots = null
            )
        } catch (e: Exception) {
            Log.e("ScheduleViewModel", "Error en clearSelection: ${e.message}", e)
        }
    }

    fun clearError() {
        try {
            _uiState.value = _uiState.value.copy(error = null)
        } catch (e: Exception) {
            Log.e("ScheduleViewModel", "Error en clearError: ${e.message}", e)
        }
    }

    fun isDoctorAvailableOnDate(date: String): Boolean {
        return try {
            val schedules = _uiState.value.doctorSchedules

            if (schedules.isEmpty()) {
                return false
            }

            val result = checkDoctorAvailabilityUseCase.checkAvailability(schedules, date)
            result.isAvailable

        } catch (e: Exception) {
            Log.e("ScheduleViewModel", "Error crítico en isDoctorAvailableOnDate: ${e.message}", e)
            false
        }
    }

    fun getWorkingDays(): List<Int> {
        return try {
            val schedules = _uiState.value.doctorSchedules
            checkDoctorAvailabilityUseCase.getWorkingDaysForCalendar(schedules)
        } catch (e: Exception) {
            Log.e("ScheduleViewModel", "Error en getWorkingDays: ${e.message}", e)
            emptyList()
        }
    }

    fun getWorkingDaysNames(): String {
        return try {
            val schedules = _uiState.value.doctorSchedules
            checkDoctorAvailabilityUseCase.getWorkingDaysNames(schedules)
        } catch (e: Exception) {
            Log.e("ScheduleViewModel", "Error en getWorkingDaysNames: ${e.message}", e)
            "Error al obtener días"
        }
    }

    fun validateSelectedTime(selectedTime: String): Boolean {
        return try {
            val currentSlots = _uiState.value.availableSlots?.slots ?: emptyList()

            val validation = validateAppointmentTimeUseCase.validateAppointmentTime(
                doctorSchedules = _uiState.value.doctorSchedules,
                availableSlots = currentSlots,
                selectedDate = _uiState.value.selectedDate,
                selectedTime = selectedTime
            )

            if (!validation.isValid && validation.errorMessage != null) {
                _uiState.value = _uiState.value.copy(error = validation.errorMessage)
            }

            validation.isValid
        } catch (e: Exception) {
            Log.e("ScheduleViewModel", "Error en validateSelectedTime: ${e.message}", e)
            false
        }
    }

    fun getScheduleForDay(dayOfWeek: Int): DoctorSchedule? {
        return try {
            _uiState.value.doctorSchedules
                .find { it.diaSemana == dayOfWeek && it.activo }
        } catch (e: Exception) {
            Log.e("ScheduleViewModel", "Error en getScheduleForDay: ${e.message}", e)
            null
        }
    }

    fun retry(doctorId: Int, fecha: String, token: String) {
        try {
            clearError()
            if (fecha.isNotEmpty()) {
                loadAvailableSlots(doctorId, fecha, token)
            } else {
                loadDoctorSchedules(doctorId, token)
            }
        } catch (e: Exception) {
            Log.e("ScheduleViewModel", "Error en retry: ${e.message}", e)
        }
    }
}