package com.example.medicitas.src.Features.Appointment.domain.usecase

import com.example.medicitas.src.Features.Appointment.domain.model.AvailableSlotsResult
import com.example.medicitas.src.Features.Appointment.domain.repository.AppointmentRepository
import java.text.SimpleDateFormat
import java.util.*

class GetAvailableSlotsUseCase(
    private val appointmentRepository: AppointmentRepository
) {
    suspend operator fun invoke(doctorId: Int, fecha: String, token: String): AvailableSlotsResult {
        // Validaciones antes de hacer la petición
        if (doctorId <= 0) {
            return AvailableSlotsResult.Error("ID del doctor inválido")
        }

        if (fecha.isBlank()) {
            return AvailableSlotsResult.Error("La fecha es requerida")
        }

        // Validar formato de fecha
        if (!isValidDateFormat(fecha)) {
            return AvailableSlotsResult.Error("Formato de fecha inválido. Use YYYY-MM-DD")
        }

        // Validar que la fecha no sea en el pasado
        if (isDateInPast(fecha)) {
            return AvailableSlotsResult.Error("No se pueden obtener horarios para fechas pasadas")
        }

        // Validar que la fecha no sea muy lejana (opcional, ejemplo: máximo 3 meses)
        if (isDateTooFarInFuture(fecha)) {
            return AvailableSlotsResult.Error("No se pueden obtener horarios para fechas tan lejanas")
        }

        return try {
            appointmentRepository.getAvailableSlots(doctorId, fecha, token)
        } catch (e: Exception) {
            AvailableSlotsResult.Error("Error inesperado: ${e.message}")
        }
    }

    private fun isValidDateFormat(fecha: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            sdf.isLenient = false
            sdf.parse(fecha) != null
        } catch (e: Exception) {
            false
        }
    }

    private fun isDateInPast(fecha: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val inputDate = sdf.parse(fecha)
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time

            inputDate?.before(today) ?: false
        } catch (e: Exception) {
            false
        }
    }

    private fun isDateTooFarInFuture(fecha: String): Boolean {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val inputDate = sdf.parse(fecha)
            val threeMonthsFromNow = Calendar.getInstance().apply {
                add(Calendar.MONTH, 3)
            }.time

            inputDate?.after(threeMonthsFromNow) ?: false
        } catch (e: Exception) {
            false
        }
    }
}