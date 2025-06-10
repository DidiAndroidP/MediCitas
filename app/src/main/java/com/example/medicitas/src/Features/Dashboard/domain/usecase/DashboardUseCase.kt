package com.example.medicitas.src.Features.Dashboard.domain.usecase

import android.util.Log
import com.example.medicitas.src.Features.Dashboard.domain.model.AppointmentInfo
import com.example.medicitas.src.Features.Dashboard.domain.model.DashboardData
import com.example.medicitas.src.Features.Dashboard.domain.model.UserInfo
import com.example.medicitas.src.Features.Dashboard.domain.repository.DashboardRepository

class DashboardUseCase(
    private val repository: DashboardRepository
) {
    suspend operator fun invoke(userId: Int, token: String): Result<DashboardData> {
        return try {
            Log.d("DashboardUseCase", "Iniciando carga de datos para userId: $userId")

            val userResult = repository.getUserById(userId)
            Log.d("DashboardUseCase", "UserResult: ${userResult.isSuccess}")

            if (userResult.isFailure) {
                Log.e("DashboardUseCase", "Error al obtener usuario: ${userResult.exceptionOrNull()?.message}")
            }

            val appointmentsResult = repository.getUserAppointments(token)
            Log.d("DashboardUseCase", "AppointmentsResult: ${appointmentsResult.isSuccess}")

            if (appointmentsResult.isFailure) {
                Log.e("DashboardUseCase", "Error al obtener citas: ${appointmentsResult.exceptionOrNull()?.message}")
            }

            val userInfo = userResult.getOrNull()?.let { userDto ->
                Log.d("DashboardUseCase", "Datos del usuario recibidos: nombres=${userDto.nombres}, apellidos=${userDto.apellidos}")

                UserInfo(
                    id = userDto.id,
                    fullName = "${userDto.nombres} ${userDto.apellidos}",
                    firstName = userDto.nombres,
                    lastName = userDto.apellidos,
                    email = userDto.correo,
                    phone = userDto.telefono,
                    age = userDto.edad,
                    gender = userDto.genero,
                    allergies = userDto.alergias,
                    bloodType = userDto.tipo_sangre
                )
            }

            Log.d("DashboardUseCase", "UserInfo creado: $userInfo")
            Log.d("DashboardUseCase", "FirstName: ${userInfo?.firstName}")

            val appointments = appointmentsResult.getOrNull()?.data?.mapNotNull { appointmentDto ->
                try {
                    Log.d("DashboardUseCase", "Procesando cita: doctorName=${appointmentDto.doctorName}, specialty=${appointmentDto.specialty}")

                    AppointmentInfo(
                        id = appointmentDto.id,
                        doctorName = appointmentDto.doctorName ?: "Doctor no asignado",
                        specialty = appointmentDto.specialty ?: "Especialidad no especificada",
                        date = appointmentDto.date ?: "",
                        time = appointmentDto.time ?: "",
                        status = appointmentDto.status ?: "Pendiente",
                        isUpcoming = true
                    )
                } catch (e: Exception) {
                    Log.e("DashboardUseCase", "Error al procesar cita: ${e.message}")
                    null
                }
            } ?: emptyList()

            Log.d("DashboardUseCase", "Citas procesadas exitosamente: ${appointments.size}")

            val dashboardData = DashboardData(
                user = userInfo,
                appointments = appointments,
                appointmentCount = appointmentsResult.getOrNull()?.pagination?.total ?: 0
            )

            Log.d("DashboardUseCase", "SUCCESS - DashboardData creado con usuario: ${dashboardData.user?.firstName}")

            Result.success(dashboardData)

        } catch (e: Exception) {
            Log.e("DashboardUseCase", "Error general en DashboardUseCase: ${e.message}", e)
            Result.failure(e)
        }
    }
}