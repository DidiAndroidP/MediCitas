package com.example.medicitas.src.Features.Appointment.presentation.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicitas.src.Features.Appointment.domain.model.*
import com.example.medicitas.src.Features.Appointment.domain.usecase.*
import com.example.medicitas.src.Features.Appointment.presentation.view.AppointmentCreateUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AppointmentCreateViewModel(
    private val getSpecialtiesUseCase: GetSpecialtiesUseCase,
    private val getDoctorsUseCase: GetDoctorsUseCase,
    private val createAppointmentUseCase: CreateAppointmentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AppointmentCreateUiState())
    val uiState: StateFlow<AppointmentCreateUiState> = _uiState.asStateFlow()

    private var isLoadingData = false

    init {
        Log.d("AppointmentViewModel", "=== APPOINTMENT CREATE VIEWMODEL INICIALIZADO ===")
        loadInitialData()
    }

    private fun loadInitialData() {
        if (isLoadingData) {
            Log.w("AppointmentViewModel", "⚠️ Ya se están cargando los datos iniciales")
            return
        }

        Log.d("AppointmentViewModel", "=== loadInitialData INICIADO ===")
        Log.d("AppointmentViewModel", "Estado inicial - Loading: ${_uiState.value.isLoading}")

        viewModelScope.launch {
            try {
                isLoadingData = true
                Log.d("AppointmentViewModel", "Actualizando estado a loading...")
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)
                Log.d("AppointmentViewModel", "Estado actualizado - Loading: ${_uiState.value.isLoading}")

                // Cargar especialidades
                Log.d("AppointmentViewModel", "Cargando especialidades...")
                when (val specialtiesResult = getSpecialtiesUseCase()) {
                    is SpecialtiesResult.Success -> {
                        Log.d("AppointmentViewModel", "✅ Especialidades cargadas: ${specialtiesResult.specialties.size}")
                        specialtiesResult.specialties.forEachIndexed { index, specialty ->
                            Log.d("AppointmentViewModel", "  Especialidad $index: ${specialty.nombre} (ID: ${specialty.id})")
                        }

                        _uiState.value = _uiState.value.copy(
                            specialties = specialtiesResult.specialties
                        )
                        Log.d("AppointmentViewModel", "Estado actualizado con especialidades: ${_uiState.value.specialties.size}")
                    }
                    is SpecialtiesResult.Error -> {
                        Log.e("AppointmentViewModel", "❌ Error cargando especialidades: ${specialtiesResult.message}")
                        _uiState.value = _uiState.value.copy(
                            error = "Error al cargar especialidades: ${specialtiesResult.message}",
                            isLoading = false
                        )
                        isLoadingData = false
                        return@launch
                    }
                    is SpecialtiesResult.Loading -> {
                        Log.d("AppointmentViewModel", "Especialidades en loading...")
                    }
                }

                // Cargar doctores
                Log.d("AppointmentViewModel", "Cargando doctores...")
                when (val doctorsResult = getDoctorsUseCase()) {
                    is DoctorsResult.Success -> {
                        Log.d("AppointmentViewModel", "✅ Doctores cargados: ${doctorsResult.doctors.size}")
                        doctorsResult.doctors.take(5).forEachIndexed { index, doctor ->
                            Log.d("AppointmentViewModel", "  Doctor $index: ${doctor.nombreCompleto} - Especialidad: ${doctor.especialidadId} - Activo: ${doctor.activo}")
                        }
                        if (doctorsResult.doctors.size > 5) {
                            Log.d("AppointmentViewModel", "  ... y ${doctorsResult.doctors.size - 5} doctores más")
                        }

                        _uiState.value = _uiState.value.copy(
                            doctors = doctorsResult.doctors,
                            isLoading = false
                        )
                        Log.d("AppointmentViewModel", "Estado actualizado - Doctores: ${_uiState.value.doctors.size}, Loading: ${_uiState.value.isLoading}")
                    }
                    is DoctorsResult.Error -> {
                        Log.e("AppointmentViewModel", "❌ Error cargando doctores: ${doctorsResult.message}")
                        _uiState.value = _uiState.value.copy(
                            error = "Error al cargar doctores: ${doctorsResult.message}",
                            isLoading = false
                        )
                    }
                    is DoctorsResult.Loading -> {
                        Log.d("AppointmentViewModel", "Doctores en loading...")
                    }
                }

                isLoadingData = false
                Log.d("AppointmentViewModel", "=== loadInitialData COMPLETADO ===")
                Log.d("AppointmentViewModel", "Estado final - Especialidades: ${_uiState.value.specialties.size}, Doctores: ${_uiState.value.doctors.size}")

            } catch (e: Exception) {
                Log.e("AppointmentViewModel", "❌ EXCEPCIÓN en loadInitialData: ${e.message}", e)
                Log.e("AppointmentViewModel", "StackTrace: ${e.stackTraceToString()}")
                _uiState.value = _uiState.value.copy(
                    error = "Error inesperado al cargar datos: ${e.message}",
                    isLoading = false
                )
                isLoadingData = false
            }
        }
    }

    fun selectSpecialty(specialtyId: Int) {
        Log.d("AppointmentViewModel", "=== selectSpecialty ===")
        Log.d("AppointmentViewModel", "Especialidad ID: $specialtyId")
        Log.d("AppointmentViewModel", "Estado anterior - SelectedSpecialtyId: ${_uiState.value.selectedSpecialtyId}")
        Log.d("AppointmentViewModel", "Doctores totales: ${_uiState.value.doctors.size}")

        try {
            val filteredDoctors = _uiState.value.doctors.filter {
                it.especialidadId == specialtyId && it.activo
            }
            Log.d("AppointmentViewModel", "Doctores filtrados: ${filteredDoctors.size}")
            filteredDoctors.forEach { doctor ->
                Log.d("AppointmentViewModel", "  - ${doctor.nombreCompleto} (ID: ${doctor.id})")
            }

            _uiState.value = _uiState.value.copy(
                selectedSpecialtyId = specialtyId,
                filteredDoctors = filteredDoctors,
                selectedDoctorId = null,
                selectedDate = "",
                selectedHour = "",
                availableDates = emptyList(),
                availableHours = emptyList()
            )

            Log.d("AppointmentViewModel", "✅ Estado actualizado:")
            Log.d("AppointmentViewModel", "  - SelectedSpecialtyId: ${_uiState.value.selectedSpecialtyId}")
            Log.d("AppointmentViewModel", "  - FilteredDoctors: ${_uiState.value.filteredDoctors.size}")
            Log.d("AppointmentViewModel", "  - SelectedDoctorId: ${_uiState.value.selectedDoctorId}")
            Log.d("AppointmentViewModel", "  - Reset: Date='${_uiState.value.selectedDate}', Hour='${_uiState.value.selectedHour}'")

        } catch (e: Exception) {
            Log.e("AppointmentViewModel", "❌ Error en selectSpecialty: ${e.message}", e)
        }
    }

    fun selectDoctor(doctorId: Int) {
        Log.d("AppointmentViewModel", "=== selectDoctor ===")
        Log.d("AppointmentViewModel", "Doctor ID: $doctorId")
        Log.d("AppointmentViewModel", "Estado anterior - SelectedDoctorId: ${_uiState.value.selectedDoctorId}")
        Log.d("AppointmentViewModel", "Doctores filtrados disponibles: ${_uiState.value.filteredDoctors.size}")

        try {
            val selectedDoctor = _uiState.value.filteredDoctors.find { it.id == doctorId }
            Log.d("AppointmentViewModel", "Doctor encontrado: ${selectedDoctor?.let { "${it.nombreCompleto} (Especialidad: ${it.especialidadId})" } ?: "NO ENCONTRADO"}")

            Log.d("AppointmentViewModel", "Generando fechas disponibles...")
            val availableDates = generateAvailableDates()
            Log.d("AppointmentViewModel", "Fechas disponibles generadas: ${availableDates.size}")
            availableDates.take(5).forEach { date ->
                Log.d("AppointmentViewModel", "  - $date")
            }
            if (availableDates.size > 5) {
                Log.d("AppointmentViewModel", "  ... y ${availableDates.size - 5} fechas más")
            }

            _uiState.value = _uiState.value.copy(
                selectedDoctorId = doctorId,
                availableDates = availableDates,
                selectedDate = "", // Reset selections
                selectedHour = "",
                availableHours = emptyList()
            )

            Log.d("AppointmentViewModel", "✅ Estado actualizado:")
            Log.d("AppointmentViewModel", "  - SelectedDoctorId: ${_uiState.value.selectedDoctorId}")
            Log.d("AppointmentViewModel", "  - AvailableDates: ${_uiState.value.availableDates.size}")
            Log.d("AppointmentViewModel", "  - Reset: Date='${_uiState.value.selectedDate}', Hour='${_uiState.value.selectedHour}'")

        } catch (e: Exception) {
            Log.e("AppointmentViewModel", "❌ Error en selectDoctor: ${e.message}", e)
        }
    }

    fun selectDate(date: String) {
        Log.d("AppointmentViewModel", "=== selectDate ===")
        Log.d("AppointmentViewModel", "Fecha seleccionada: '$date'")
        Log.d("AppointmentViewModel", "Estado anterior:")
        Log.d("AppointmentViewModel", "  - SelectedDate: '${_uiState.value.selectedDate}'")
        Log.d("AppointmentViewModel", "  - SelectedHour: '${_uiState.value.selectedHour}'")
        Log.d("AppointmentViewModel", "  - SelectedDoctorId: ${_uiState.value.selectedDoctorId}")
        Log.d("AppointmentViewModel", "  - AvailableDates count: ${_uiState.value.availableDates.size}")

        try {
            // Verificar que la fecha esté en las fechas disponibles
            if (!_uiState.value.availableDates.contains(date)) {
                Log.w("AppointmentViewModel", "⚠️ Fecha seleccionada no está en availableDates")
                Log.d("AppointmentViewModel", "AvailableDates: ${_uiState.value.availableDates.take(3)}")
            }

            Log.d("AppointmentViewModel", "Generando horas disponibles...")
            val availableHours = generateAvailableHours()
            Log.d("AppointmentViewModel", "Horas disponibles generadas: ${availableHours.size}")
            availableHours.take(5).forEach { hour ->
                Log.d("AppointmentViewModel", "  - $hour")
            }
            if (availableHours.size > 5) {
                Log.d("AppointmentViewModel", "  ... y ${availableHours.size - 5} horas más")
            }

            Log.d("AppointmentViewModel", "Actualizando estado...")
            _uiState.value = _uiState.value.copy(
                selectedDate = date,
                availableHours = availableHours,
                selectedHour = ""
            )

            Log.d("AppointmentViewModel", "✅ Estado actualizado exitosamente:")
            Log.d("AppointmentViewModel", "  - SelectedDate: '${_uiState.value.selectedDate}'")
            Log.d("AppointmentViewModel", "  - AvailableHours: ${_uiState.value.availableHours.size}")
            Log.d("AppointmentViewModel", "  - SelectedHour (reset): '${_uiState.value.selectedHour}'")

        } catch (e: Exception) {
            Log.e("AppointmentViewModel", "❌ EXCEPCIÓN en selectDate: ${e.message}", e)
            Log.e("AppointmentViewModel", "StackTrace: ${e.stackTraceToString()}")
        }
    }

    fun selectHour(hour: String) {
        Log.d("AppointmentViewModel", "=== selectHour ===")
        Log.d("AppointmentViewModel", "Hora seleccionada: '$hour'")
        Log.d("AppointmentViewModel", "Estado anterior - SelectedHour: '${_uiState.value.selectedHour}'")

        try {
            _uiState.value = _uiState.value.copy(selectedHour = hour)
            Log.d("AppointmentViewModel", "✅ Hora actualizada exitosamente - nuevo SelectedHour: '${_uiState.value.selectedHour}'")
        } catch (e: Exception) {
            Log.e("AppointmentViewModel", "❌ Error en selectHour: ${e.message}", e)
        }
    }

    fun updateMotivo(motivo: String) {
        Log.d("AppointmentViewModel", "=== updateMotivo ===")
        Log.d("AppointmentViewModel", "Motivo: '${motivo.take(50)}${if(motivo.length > 50) "..." else ""}'")

        try {
            _uiState.value = _uiState.value.copy(motivo = motivo)
            Log.d("AppointmentViewModel", "✅ Motivo actualizado - length: ${_uiState.value.motivo.length}")
        } catch (e: Exception) {
            Log.e("AppointmentViewModel", "❌ Error en updateMotivo: ${e.message}", e)
        }
    }

    fun updateNotas(notas: String) {
        Log.d("AppointmentViewModel", "=== updateNotas ===")
        Log.d("AppointmentViewModel", "Notas: '${notas.take(50)}${if(notas.length > 50) "..." else ""}'")

        try {
            _uiState.value = _uiState.value.copy(notas = notas)
            Log.d("AppointmentViewModel", "✅ Notas actualizadas - length: ${_uiState.value.notas.length}")
        } catch (e: Exception) {
            Log.e("AppointmentViewModel", "❌ Error en updateNotas: ${e.message}", e)
        }
    }

    fun createAppointment(token: String) {
        Log.d("AppointmentViewModel", "=== createAppointment ===")
        val state = _uiState.value

        Log.d("AppointmentViewModel", "Validando datos de la cita:")
        Log.d("AppointmentViewModel", "  - SelectedDoctorId: ${state.selectedDoctorId}")
        Log.d("AppointmentViewModel", "  - SelectedDate: '${state.selectedDate}'")
        Log.d("AppointmentViewModel", "  - SelectedHour: '${state.selectedHour}'")
        Log.d("AppointmentViewModel", "  - Motivo length: ${state.motivo.length}")
        Log.d("AppointmentViewModel", "  - Notas length: ${state.notas.length}")
        Log.d("AppointmentViewModel", "  - Token presente: ${token.isNotEmpty()} (length: ${token.length})")

        // Validaciones
        if (state.selectedDoctorId == null) {
            Log.e("AppointmentViewModel", "❌ Validación fallida: No hay doctor seleccionado")
            _uiState.value = state.copy(error = "Debe seleccionar un doctor")
            return
        }

        if (state.selectedDate.isEmpty()) {
            Log.e("AppointmentViewModel", "❌ Validación fallida: No hay fecha seleccionada")
            _uiState.value = state.copy(error = "Debe seleccionar una fecha")
            return
        }

        if (state.selectedHour.isEmpty()) {
            Log.e("AppointmentViewModel", "❌ Validación fallida: No hay hora seleccionada")
            _uiState.value = state.copy(error = "Debe seleccionar una hora")
            return
        }

        if (state.motivo.isEmpty()) {
            Log.e("AppointmentViewModel", "❌ Validación fallida: Motivo vacío")
            _uiState.value = state.copy(error = "Debe especificar el motivo de la consulta")
            return
        }

        if (state.motivo.length < 10) {
            Log.e("AppointmentViewModel", "❌ Validación fallida: Motivo muy corto (${state.motivo.length} chars)")
            _uiState.value = state.copy(error = "El motivo debe tener al menos 10 caracteres")
            return
        }

        Log.d("AppointmentViewModel", "✅ Todas las validaciones pasaron - iniciando creación de cita")

        viewModelScope.launch {
            try {
                Log.d("AppointmentViewModel", "Actualizando estado a creating...")
                _uiState.value = state.copy(isCreating = true, error = null)
                Log.d("AppointmentViewModel", "Estado actualizado - IsCreating: ${_uiState.value.isCreating}")

                val request = CreateAppointmentRequest(
                    doctorId = state.selectedDoctorId,
                    fechaCita = state.selectedDate,
                    horaCita = state.selectedHour,
                    motivo = state.motivo,
                    notas = state.notas.ifBlank { null }
                )

                Log.d("AppointmentViewModel", "Request creado:")
                Log.d("AppointmentViewModel", "  - doctorId: ${request.doctorId}")
                Log.d("AppointmentViewModel", "  - fechaCita: '${request.fechaCita}'")
                Log.d("AppointmentViewModel", "  - horaCita: '${request.horaCita}'")
                Log.d("AppointmentViewModel", "  - motivo: '${request.motivo.take(50)}...'")
                Log.d("AppointmentViewModel", "  - notas: ${request.notas?.let { "'${it.take(30)}...'" } ?: "null"}")

                Log.d("AppointmentViewModel", "Llamando createAppointmentUseCase...")
                val result = createAppointmentUseCase(token, request)
                Log.d("AppointmentViewModel", "Resultado recibido: ${result::class.simpleName}")

                when (result) {
                    is CreateAppointmentResult.Success -> {
                        Log.d("AppointmentViewModel", "✅ SUCCESS - Cita creada exitosamente: ${result.message}")
                        _uiState.value = _uiState.value.copy(
                            isCreating = false,
                            successMessage = result.message
                        )
                        Log.d("AppointmentViewModel", "Estado actualizado - IsCreating: ${_uiState.value.isCreating}, SuccessMessage: '${_uiState.value.successMessage}'")
                    }
                    is CreateAppointmentResult.Error -> {
                        Log.e("AppointmentViewModel", "❌ ERROR - Error al crear cita: ${result.message}")
                        _uiState.value = _uiState.value.copy(
                            isCreating = false,
                            error = result.message
                        )
                        Log.d("AppointmentViewModel", "Estado actualizado con error: ${_uiState.value.error}")
                    }
                    is CreateAppointmentResult.Loading -> {
                        Log.d("AppointmentViewModel", "Loading state recibido")
                        _uiState.value = _uiState.value.copy(isCreating = true)
                    }
                }
                Log.d("AppointmentViewModel", "=== createAppointment COMPLETADO ===")

            } catch (e: Exception) {
                Log.e("AppointmentViewModel", "❌ EXCEPCIÓN en createAppointment: ${e.message}", e)
                Log.e("AppointmentViewModel", "StackTrace: ${e.stackTraceToString()}")
                _uiState.value = _uiState.value.copy(
                    isCreating = false,
                    error = "Error inesperado: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        Log.d("AppointmentViewModel", "=== clearError ===")
        Log.d("AppointmentViewModel", "Error anterior: '${_uiState.value.error}'")

        try {
            _uiState.value = _uiState.value.copy(error = null)
            Log.d("AppointmentViewModel", "✅ Error limpiado exitosamente")
        } catch (e: Exception) {
            Log.e("AppointmentViewModel", "❌ Error en clearError: ${e.message}", e)
        }
    }

    fun clearSuccess() {
        Log.d("AppointmentViewModel", "=== clearSuccess ===")
        Log.d("AppointmentViewModel", "Success message anterior: '${_uiState.value.successMessage}'")

        try {
            _uiState.value = _uiState.value.copy(successMessage = null)
            Log.d("AppointmentViewModel", "✅ Success message limpiado exitosamente")
        } catch (e: Exception) {
            Log.e("AppointmentViewModel", "❌ Error en clearSuccess: ${e.message}", e)
        }
    }

    fun retry() {
        Log.d("AppointmentViewModel", "=== retry ===")
        Log.d("AppointmentViewModel", "Reiniciando carga de datos...")

        try {
            isLoadingData = false
            loadInitialData()
        } catch (e: Exception) {
            Log.e("AppointmentViewModel", "❌ Error en retry: ${e.message}", e)
        }
    }

    private fun generateAvailableDates(): List<String> {
        Log.d("AppointmentViewModel", "=== generateAvailableDates ===")
        return try {
            val dates = mutableListOf<String>()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            var currentDate = LocalDate.now().plusDays(1)

            Log.d("AppointmentViewModel", "Generando fechas desde: $currentDate")

            for (i in 0 until 30) {
                if (currentDate.dayOfWeek.value != 7) {
                    dates.add(currentDate.format(formatter))
                }
                currentDate = currentDate.plusDays(1)
            }

            Log.d("AppointmentViewModel", "✅ Fechas generadas: ${dates.size} (excluyendo domingos)")
            Log.d("AppointmentViewModel", "Primeras fechas: ${dates.take(3)}")
            dates
        } catch (e: Exception) {
            Log.e("AppointmentViewModel", "❌ Error en generateAvailableDates: ${e.message}", e)
            emptyList()
        }
    }

    private fun generateAvailableHours(): List<String> {
        Log.d("AppointmentViewModel", "=== generateAvailableHours ===")
        return try {
            val hours = mutableListOf<String>()

            for (hour in 8..17) {
                hours.add(String.format("%02d:00", hour))
                if (hour < 17) {
                    hours.add(String.format("%02d:30", hour))
                }
            }

            Log.d("AppointmentViewModel", "✅ Horas generadas: ${hours.size}")
            Log.d("AppointmentViewModel", "Rango: ${hours.first()} - ${hours.last()}")
            hours
        } catch (e: Exception) {
            Log.e("AppointmentViewModel", "❌ Error en generateAvailableHours: ${e.message}", e)
            emptyList()
        }
    }
}