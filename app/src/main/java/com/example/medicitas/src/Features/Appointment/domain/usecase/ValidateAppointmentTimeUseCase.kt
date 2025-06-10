// Crear en: domain/usecase/ValidateAppointmentTimeUseCase.kt
package com.example.medicitas.src.Features.Appointment.domain.usecase

import com.example.medicitas.src.Features.Appointment.domain.model.DoctorSchedule
import com.example.medicitas.src.Features.Appointment.domain.model.TimeSlot
import java.text.SimpleDateFormat
import java.util.*

class ValidateAppointmentTimeUseCase {

    data class ValidationResult(
        val isValid: Boolean,
        val errorMessage: String? = null
    )

    fun validateAppointmentTime(
        doctorSchedules: List<DoctorSchedule>,
        availableSlots: List<TimeSlot>,
        selectedDate: String,
        selectedTime: String
    ): ValidationResult {

        // Validar que la fecha esté seleccionada
        if (selectedDate.isBlank()) {
            return ValidationResult(false, "Debe seleccionar una fecha")
        }

        // Validar que la hora esté seleccionada
        if (selectedTime.isBlank()) {
            return ValidationResult(false, "Debe seleccionar una hora")
        }

        // Validar que el doctor trabaje el día seleccionado
        val dayOfWeek = getDayOfWeek(selectedDate)
        val doctorWorksOnDay = doctorSchedules.any { schedule ->
            schedule.diaSemana == dayOfWeek && schedule.activo
        }

        if (!doctorWorksOnDay) {
            return ValidationResult(false, "El doctor no atiende el día seleccionado")
        }

        // Validar que la hora esté dentro del horario del doctor
        val daySchedule = doctorSchedules.find {
            it.diaSemana == dayOfWeek && it.activo
        }

        if (daySchedule != null) {
            if (!isTimeWithinSchedule(selectedTime, daySchedule.horaInicio, daySchedule.horaFin)) {
                return ValidationResult(false, "La hora seleccionada está fuera del horario de atención")
            }
        }

        // Validar que el slot esté disponible
        val selectedSlot = availableSlots.find { it.hora == selectedTime }
        if (selectedSlot == null) {
            return ValidationResult(false, "La hora seleccionada no está disponible")
        }

        if (!selectedSlot.disponible) {
            return ValidationResult(false, "La hora seleccionada ya está ocupada")
        }

        // Validar que no sea una fecha/hora pasada
        if (isDateTimePast(selectedDate, selectedTime)) {
            return ValidationResult(false, "No se pueden agendar citas en fechas u horas pasadas")
        }

        return ValidationResult(true)
    }

    private fun getDayOfWeek(fecha: String): Int {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val calendar = Calendar.getInstance()
            calendar.time = sdf.parse(fecha) ?: return -1
            calendar.get(Calendar.DAY_OF_WEEK) - 1 // 0 = Domingo, 1 = Lunes, etc.
        } catch (e: Exception) {
            -1
        }
    }

    private fun isTimeWithinSchedule(selectedTime: String, startTime: String, endTime: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
            val selected = sdf.parse(selectedTime)
            val start = sdf.parse(startTime)
            val end = sdf.parse(endTime)

            selected != null && start != null && end != null &&
                    !selected.before(start) && selected.before(end)
        } catch (e: Exception) {
            false
        }
    }

    private fun isDateTimePast(date: String, time: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val appointmentDateTime = sdf.parse("$date $time")
            val now = Date()

            appointmentDateTime?.before(now) ?: false
        } catch (e: Exception) {
            false
        }
    }
}