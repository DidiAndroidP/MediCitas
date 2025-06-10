package com.example.medicitas.src.Features.Appointment.presentation.view

import com.example.medicitas.src.Features.Appointment.domain.model.SpecialtyEntity
import com.example.medicitas.src.Features.Appointment.domain.model.DoctorEntity

data class AppointmentCreateUiState(
    // ✅ ESTADOS DE CARGA
    val isLoading: Boolean = false,
    val isCreating: Boolean = false,

    // ✅ DATOS DE LAS LISTAS
    val specialties: List<SpecialtyEntity> = emptyList(),
    val doctors: List<DoctorEntity> = emptyList(),
    val filteredDoctors: List<DoctorEntity> = emptyList(),
    val availableDates: List<String> = emptyList(),
    val availableHours: List<String> = emptyList(),

    // ✅ SELECCIONES DEL USUARIO
    val selectedSpecialtyId: Int? = null,
    val selectedDoctorId: Int? = null,
    val selectedDate: String = "",
    val selectedHour: String = "",

    // ✅ CAMPOS DE TEXTO
    val motivo: String = "",
    val notas: String = "",

    // ✅ ESTADOS DE RESULTADO
    val error: String? = null,
    val successMessage: String? = null
) {
    // ✅ COMPUTED PROPERTIES ÚTILES

    /**
     * Indica si el formulario está completo y listo para enviar
     */
    val isFormValid: Boolean
        get() = selectedSpecialtyId != null &&
                selectedDoctorId != null &&
                selectedDate.isNotEmpty() &&
                selectedHour.isNotEmpty() &&
                motivo.isNotEmpty() &&
                motivo.length >= 10

    /**
     * Indica si se pueden mostrar los doctores
     */
    val canShowDoctors: Boolean
        get() = selectedSpecialtyId != null && filteredDoctors.isNotEmpty()

    /**
     * Indica si se pueden mostrar las fechas
     */
    val canShowDates: Boolean
        get() = selectedDoctorId != null && availableDates.isNotEmpty()

    /**
     * Indica si se pueden mostrar las horas
     */
    val canShowHours: Boolean
        get() = selectedDate.isNotEmpty() && availableHours.isNotEmpty()

    /**
     * Obtiene la especialidad seleccionada
     */
    val selectedSpecialty: SpecialtyEntity?
        get() = specialties.find { it.id == selectedSpecialtyId }

    /**
     * Obtiene el doctor seleccionado
     */
    val selectedDoctor: DoctorEntity?
        get() = filteredDoctors.find { it.id == selectedDoctorId }

    /**
     * Indica si hay algún error o está cargando
     */
    val hasError: Boolean
        get() = error != null

    /**
     * Indica si la operación fue exitosa
     */
    val isSuccess: Boolean
        get() = successMessage != null

    /**
     * Indica si se está realizando alguna operación
     */
    val isBusy: Boolean
        get() = isLoading || isCreating

    /**
     * Mensaje para el placeholder del dropdown de doctores
     */
    val doctorPlaceholder: String
        get() = when {
            selectedSpecialtyId == null -> "Selecciona primero una especialidad"
            filteredDoctors.isEmpty() -> "No hay doctores disponibles"
            else -> "Selecciona un doctor"
        }

    /**
     * Mensaje para el placeholder del dropdown de fechas
     */
    val datePlaceholder: String
        get() = when {
            selectedDoctorId == null -> "Selecciona primero un doctor"
            availableDates.isEmpty() -> "No hay fechas disponibles"
            else -> "Selecciona una fecha"
        }

    /**
     * Mensaje para el placeholder del dropdown de horas
     */
    val hourPlaceholder: String
        get() = when {
            selectedDate.isEmpty() -> "Selecciona primero una fecha"
            availableHours.isEmpty() -> "No hay horas disponibles"
            else -> "Selecciona una hora"
        }

    /**
     * Progreso del formulario (0.0 a 1.0)
     */
    val formProgress: Float
        get() {
            var progress = 0f
            if (selectedSpecialtyId != null) progress += 0.2f
            if (selectedDoctorId != null) progress += 0.2f
            if (selectedDate.isNotEmpty()) progress += 0.2f
            if (selectedHour.isNotEmpty()) progress += 0.2f
            if (motivo.isNotEmpty() && motivo.length >= 10) progress += 0.2f
            return progress
        }
    val appointmentSummary: String?
        get() = if (isFormValid) {
            buildString {
                selectedDoctor?.let { doctor ->
                    append("Doctor: ${doctor.nombreCompleto}\n")
                }
                selectedSpecialty?.let { specialty ->
                    append("Especialidad: ${specialty.nombre}\n")
                }
                append("Fecha: $selectedDate\n")
                append("Hora: $selectedHour\n")
                append("Motivo: $motivo")
                if (notas.isNotBlank()) {
                    append("\nNotas: $notas")
                }
            }
        } else null
}