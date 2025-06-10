package com.example.medicitas.src.Features.Appointment.domain.usecase

import com.example.medicitas.src.Features.Appointment.domain.model.UpdateAppointmentRequest
import com.example.medicitas.src.Features.Appointment.domain.model.UpdateAppointmentResult
import com.example.medicitas.src.Features.Appointment.domain.repository.AppointmentRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class UpdateAppointmentUseCase(
    private val appointmentRepository: AppointmentRepository
) {
    suspend operator fun invoke(
        appointmentId: Int,
        token: String,
        request: UpdateAppointmentRequest
    ): UpdateAppointmentResult {
        return try {
            // ✅ Validación de ID de cita
            if (appointmentId <= 0) {
                return UpdateAppointmentResult.Error("ID de cita inválido")
            }

            // ✅ Validación de token
            if (token.isBlank()) {
                return UpdateAppointmentResult.Error("Token de autenticación requerido")
            }

            // ✅ Validar que al menos un campo venga para actualizar
            if (request.fechaCita.isNullOrBlank() &&
                request.horaCita.isNullOrBlank() &&
                request.motivo.isNullOrBlank() &&
                request.notas == null) { // notas puede ser string vacío para limpiar
                return UpdateAppointmentResult.Error("Debe proporcionar al menos un campo para actualizar")
            }

            // ✅ Validar fecha si se proporciona
            request.fechaCita?.let { fecha ->
                if (fecha.isNotBlank()) {
                    try {
                        val fechaObj = LocalDate.parse(fecha, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                        val hoy = LocalDate.now()

                        if (fechaObj.isBefore(hoy)) {
                            return UpdateAppointmentResult.Error("No se pueden agendar citas en fechas pasadas")
                        }

                        // Validar que no sea más de 6 meses en el futuro
                        val maxFecha = hoy.plusMonths(6)
                        if (fechaObj.isAfter(maxFecha)) {
                            return UpdateAppointmentResult.Error("No se pueden agendar citas con más de 6 meses de anticipación")
                        }
                    } catch (e: DateTimeParseException) {
                        return UpdateAppointmentResult.Error("Formato de fecha inválido. Use yyyy-MM-dd")
                    }
                }
            }

            request.horaCita?.let { hora ->
                if (hora.isNotBlank()) {
                    val horaRegex = "^([01]?[0-9]|2[0-3]):[0-5][0-9]$".toRegex()
                    if (!horaRegex.matches(hora)) {
                        return UpdateAppointmentResult.Error("Formato de hora inválido. Use HH:mm (ejemplo: 14:30)")
                    }

                    val horaParts = hora.split(":")
                    val horaInt = horaParts[0].toInt()
                    val minutoInt = horaParts[1].toInt()

                    if (horaInt < 8 || horaInt > 18 || (horaInt == 18 && minutoInt > 0)) {
                        return UpdateAppointmentResult.Error("La hora debe estar entre 08:00 y 18:00")
                    }
                }
            }

            request.motivo?.let { motivo ->
                if (motivo.isNotBlank()) {
                    if (motivo.length < 10) {
                        return UpdateAppointmentResult.Error("El motivo debe tener al menos 10 caracteres")
                    }

                    if (motivo.length > 500) {
                        return UpdateAppointmentResult.Error("El motivo no puede exceder 500 caracteres")
                    }
                }
            }

            request.notas?.let { notas ->
                if (notas.length > 1000) {
                    return UpdateAppointmentResult.Error("Las notas no pueden exceder 1000 caracteres")
                }
            }

            appointmentRepository.updateAppointment(appointmentId, token, request)

        } catch (e: Exception) {
            UpdateAppointmentResult.Error("Error inesperado: ${e.message}")
        }
    }
}