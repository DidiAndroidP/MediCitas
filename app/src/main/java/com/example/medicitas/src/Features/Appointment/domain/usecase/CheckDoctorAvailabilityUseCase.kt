// Crear en: domain/usecase/CheckDoctorAvailabilityUseCase.kt
package com.example.medicitas.src.Features.Appointment.domain.usecase

import com.example.medicitas.src.Features.Appointment.domain.model.DoctorSchedule
import java.text.SimpleDateFormat
import java.util.*

class CheckDoctorAvailabilityUseCase {

    data class AvailabilityInfo(
        val isAvailable: Boolean,
        val workingDays: List<Int>, // Días de la semana que trabaja (0=Domingo, 1=Lunes, etc.)
        val scheduleForDate: DoctorSchedule? = null,
        val message: String
    )

    fun checkAvailability(
        doctorSchedules: List<DoctorSchedule>,
        selectedDate: String
    ): AvailabilityInfo {

        val workingDays = doctorSchedules
            .filter { it.activo }
            .map { it.diaSemana }

        if (selectedDate.isBlank()) {
            return AvailabilityInfo(
                isAvailable = false,
                workingDays = workingDays,
                message = "Seleccione una fecha para verificar disponibilidad"
            )
        }

        val dayOfWeek = getDayOfWeek(selectedDate)
        if (dayOfWeek == -1) {
            return AvailabilityInfo(
                isAvailable = false,
                workingDays = workingDays,
                message = "Formato de fecha inválido"
            )
        }

        // Buscar el horario para el día seleccionado
        val scheduleForDay = doctorSchedules.find {
            it.diaSemana == dayOfWeek && it.activo
        }

        if (scheduleForDay == null) {
            val workingDaysNames = getWorkingDaysNames(doctorSchedules)
            return AvailabilityInfo(
                isAvailable = false,
                workingDays = workingDays,
                message = "El doctor no atiende este día. Días de atención: $workingDaysNames"
            )
        }

        return AvailabilityInfo(
            isAvailable = true,
            workingDays = workingDays,
            scheduleForDate = scheduleForDay,
            message = "Doctor disponible de ${scheduleForDay.horaInicio} a ${scheduleForDay.horaFin}"
        )
    }

    fun getWorkingDaysForCalendar(doctorSchedules: List<DoctorSchedule>): List<Int> {
        return doctorSchedules
            .filter { it.activo }
            .map { it.diaSemana }
    }

    fun getWorkingDaysNames(doctorSchedules: List<DoctorSchedule>): String {
        val dayNames = doctorSchedules
            .filter { it.activo }
            .sortedBy { it.diaSemana }
            .map { it.diaNombre }

        return when (dayNames.size) {
            0 -> "Ningún día"
            1 -> dayNames.first()
            else -> "${dayNames.dropLast(1).joinToString(", ")} y ${dayNames.last()}"
        }
    }

    fun isDateInWorkingDays(date: String, doctorSchedules: List<DoctorSchedule>): Boolean {
        val dayOfWeek = getDayOfWeek(date)
        return doctorSchedules.any { schedule ->
            schedule.diaSemana == dayOfWeek && schedule.activo
        }
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
}