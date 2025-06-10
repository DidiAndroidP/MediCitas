package com.example.medicitas.src.Features.Appointment.data.repository

import com.example.medicitas.src.Features.Appointment.data.datasource.remote.AppointmentApiService
import com.example.medicitas.src.Features.Appointment.data.model.CreateAppointmentRequestDto
import com.example.medicitas.src.Features.Appointment.data.model.UpdateAppointmentRequestDto
import com.example.medicitas.src.Features.Appointment.domain.model.*
import com.example.medicitas.src.Features.Appointment.domain.repository.AppointmentRepository

class AppointmentRepositoryImpl(
    private val appointmentApiService: AppointmentApiService
) : AppointmentRepository {

    override suspend fun getUserAppointments(
        token: String,
        page: Int,
        limit: Int
    ): AppointmentResult {
        return try {
            val authToken = if (token.startsWith("Bearer ")) {
                token
            } else {
                "Bearer $token"
            }

            println("🔍 DEBUG Repository: Token enviado: '$authToken'")

            val response = appointmentApiService.getUserAppointments(
                token = authToken,
                page = page,
                limit = limit
            )

            println("🔍 DEBUG Repository: Response code: ${response.code()}")

            if (response.isSuccessful) {
                response.body()?.let { appointmentResponseDto ->
                    if (appointmentResponseDto.success) {
                        val appointments = appointmentResponseDto.data.map { dto ->
                            AppointmentEntity(
                                id = dto.id,
                                date = dto.fecha_formateada,
                                time = dto.hora_formateada,
                                status = dto.estado_display,
                                reason = dto.motivo,
                                notes = dto.notas,
                                dayName = dto.dia_nombre
                            )
                        }
                        AppointmentResult.Success(appointments)
                    } else {
                        AppointmentResult.Error("Error en la respuesta del servidor")
                    }
                } ?: AppointmentResult.Error("Respuesta vacía del servidor")
            } else {
                val errorMsg = when (response.code()) {
                    401 -> "Token inválido o expirado. Inicia sesión nuevamente."
                    403 -> "No tienes permisos para acceder a esta información"
                    404 -> "Endpoint no encontrado"
                    500 -> "Error interno del servidor"
                    else -> "Error HTTP: ${response.code()} - ${response.message()}"
                }
                AppointmentResult.Error(errorMsg)
            }
        } catch (e: Exception) {
            val errorMsg = "Error de conexión: ${e.message}"
            AppointmentResult.Error(errorMsg)
        }
    }

    override suspend fun getAppointmentById(
        appointmentId: Int,
        token: String
    ): AppointmentDetailResult {
        return try {
            val authToken = if (token.startsWith("Bearer ")) {
                token
            } else {
                "Bearer $token"
            }

            println("🔍 DEBUG Repository Detail: Token enviado: '$authToken'")

            val response = appointmentApiService.getAppointmentById(
                appointmentId = appointmentId,
                token = authToken
            )

            if (response.isSuccessful) {
                response.body()?.let { appointmentDetailResponse ->
                    if (appointmentDetailResponse.success) {
                        val dto = appointmentDetailResponse.data
                        val appointmentDetail = AppointmentDetailEntity(
                            id = dto.id,
                            fechaCita = dto.fecha_cita,
                            horaCita = dto.hora_cita,
                            estado = dto.estado,
                            motivo = dto.motivo,
                            notas = dto.notas,
                            precio = dto.precio,
                            createAt = dto.create_at,
                            updateAt = dto.update_at,
                            doctor = DoctorEntity(
                                id = dto.doctor.id,
                                nombres = dto.doctor.nombres,
                                apellidos = dto.doctor.apellidos,
                                correo = dto.doctor.correo,
                                telefono = dto.doctor.telefono,
                                especialidadId = 0, // No viene en el response de detail
                                duracionConsulta = dto.doctor.duracion_consulta,
                                activo = true, // Valor por defecto
                                especialidad = SpecialtyEntity(
                                    id = 0, // No viene en el response
                                    nombre = dto.doctor.especialidad.nombre,
                                    descripcion = dto.doctor.especialidad.descripcion,
                                    duracionConsulta = 0, // No viene en el response
                                    precioBase = dto.doctor.especialidad.precio_base.toDoubleOrNull() ?: 0.0,
                                    activo = true // Valor por defecto
                                )
                            )
                        )
                        AppointmentDetailResult.Success(appointmentDetail)
                    } else {
                        AppointmentDetailResult.Error("Error en la respuesta del servidor")
                    }
                } ?: AppointmentDetailResult.Error("Respuesta vacía del servidor")
            } else {
                val errorMsg = when (response.code()) {
                    401 -> "Token inválido o expirado. Inicia sesión nuevamente."
                    403 -> "No tienes permisos para ver esta cita"
                    404 -> "Cita no encontrada"
                    else -> "Error HTTP: ${response.code()} - ${response.message()}"
                }
                AppointmentDetailResult.Error(errorMsg)
            }
        } catch (e: Exception) {
            AppointmentDetailResult.Error("Error de conexión: ${e.message}")
        }
    }

    override suspend fun getAllSpecialties(): SpecialtiesResult {
        return try {
            println("🔍 DEBUG Repository: Obteniendo especialidades")

            val response = appointmentApiService.getAllSpecialties()

            println("🔍 DEBUG Repository: Specialties response code: ${response.code()}")

            if (response.isSuccessful) {
                val specialties = response.body()?.map { dto ->
                    SpecialtyEntity(
                        id = dto.id,
                        nombre = dto.nombre,
                        descripcion = dto.descripcion,
                        duracionConsulta = dto.duracion_consulta,
                        precioBase = dto.precio_base,
                        activo = dto.activo
                    )
                } ?: emptyList()

                println("🔍 DEBUG Repository: Especialidades obtenidas: ${specialties.size}")
                SpecialtiesResult.Success(specialties)
            } else {
                val errorMsg = when (response.code()) {
                    404 -> "No se encontraron especialidades"
                    500 -> "Error interno del servidor"
                    else -> "Error al cargar especialidades: ${response.code()} - ${response.message()}"
                }
                SpecialtiesResult.Error(errorMsg)
            }
        } catch (e: Exception) {
            println("❌ DEBUG Repository: Error especialidades: ${e.message}")
            SpecialtiesResult.Error("Error de conexión: ${e.message}")
        }
    }

    override suspend fun getAllDoctors(): DoctorsResult {
        return try {
            println("🔍 DEBUG Repository: Obteniendo doctores")

            val response = appointmentApiService.getAllDoctors()

            println("🔍 DEBUG Repository: Doctors response code: ${response.code()}")

            if (response.isSuccessful) {
                val doctors = response.body()?.map { dto ->
                    DoctorEntity(
                        id = dto.id,
                        nombres = dto.nombres,
                        apellidos = dto.apellidos,
                        correo = dto.correo,
                        telefono = dto.telefono,
                        especialidadId = dto.especialidad_id,
                        duracionConsulta = dto.duracion_consulta,
                        activo = dto.activo == 1
                    )
                } ?: emptyList()

                println("🔍 DEBUG Repository: Doctores obtenidos: ${doctors.size}")
                DoctorsResult.Success(doctors)
            } else {
                val errorMsg = when (response.code()) {
                    404 -> "No se encontraron doctores"
                    500 -> "Error interno del servidor"
                    else -> "Error al cargar doctores: ${response.code()} - ${response.message()}"
                }
                DoctorsResult.Error(errorMsg)
            }
        } catch (e: Exception) {
            println("❌ DEBUG Repository: Error doctores: ${e.message}")
            DoctorsResult.Error("Error de conexión: ${e.message}")
        }
    }

    override suspend fun createAppointment(token: String, request: CreateAppointmentRequest): CreateAppointmentResult {
        return try {
            val authToken = if (token.startsWith("Bearer ")) {
                token
            } else {
                "Bearer $token"
            }

            println("🔍 DEBUG Repository: Creando cita con token: '$authToken'")
            println("🔍 DEBUG Repository: Request: doctorId=${request.doctorId}, fecha=${request.fechaCita}, hora=${request.horaCita}")

            val requestDto = CreateAppointmentRequestDto(
                doctor_id = request.doctorId,
                fecha_cita = request.fechaCita,
                hora_cita = request.horaCita,
                motivo = request.motivo,
                notas = request.notas
            )

            val response = appointmentApiService.createAppointment(authToken, requestDto)

            println("🔍 DEBUG Repository: Create appointment response code: ${response.code()}")

            if (response.isSuccessful) {
                val responseDto = response.body()
                if (responseDto?.success == true) {
                    println("✅ DEBUG Repository: Cita creada exitosamente con ID: ${responseDto.data.id}")
                    CreateAppointmentResult.Success(
                        message = responseDto.message,
                        appointmentId = responseDto.data.id
                    )
                } else {
                    val errorMsg = responseDto?.message ?: "Error desconocido al crear la cita"
                    println("❌ DEBUG Repository: Error en respuesta: $errorMsg")
                    CreateAppointmentResult.Error(errorMsg)
                }
            } else {
                val errorMsg = when (response.code()) {
                    400 -> "Datos inválidos para crear la cita"
                    401 -> "Token inválido o expirado. Inicia sesión nuevamente."
                    403 -> "No tienes permisos para crear citas"
                    409 -> "Ya existe una cita en esa fecha y hora"
                    422 -> "Los datos enviados no son válidos"
                    500 -> "Error interno del servidor"
                    else -> "Error al crear cita: ${response.code()} - ${response.message()}"
                }
                println("❌ DEBUG Repository: Error HTTP: $errorMsg")
                CreateAppointmentResult.Error(errorMsg)
            }
        } catch (e: Exception) {
            val errorMsg = "Error de conexión: ${e.message}"
            println("❌ DEBUG Repository: Excepción: $errorMsg")
            CreateAppointmentResult.Error(errorMsg)
        }
    }

     override suspend fun updateAppointment(
        appointmentId: Int,
        token: String,
        request: UpdateAppointmentRequest
    ): UpdateAppointmentResult {
        return try {
            val authToken = if (token.startsWith("Bearer ")) {
                token
            } else {
                "Bearer $token"
            }

            println("🔍 DEBUG Repository: === INICIANDO updateAppointment ===")
            println("🔍 DEBUG Repository: appointmentId = $appointmentId")
            println("🔍 DEBUG Repository: token enviado = '${authToken.take(30)}...'")
            println("🔍 DEBUG Repository: request = $request")

            val requestDto = UpdateAppointmentRequestDto(
                fecha_cita = request.fechaCita,
                hora_cita = request.horaCita,
                motivo = request.motivo,
                notas = request.notas
            )

            println("🔍 DEBUG Repository: requestDto = $requestDto")
            println("🔍 DEBUG Repository: URL = api/v1/appointment/update/$appointmentId")

            val response = appointmentApiService.updateAppointment(
                appointmentId = appointmentId,
                token = authToken,
                request = requestDto
            )

            println("🔍 DEBUG Repository: Response code = ${response.code()}")
            println("🔍 DEBUG Repository: Response message = ${response.message()}")

            if (response.isSuccessful) {
                val responseBody = response.body()
                println("🔍 DEBUG Repository: Response body = $responseBody")

                responseBody?.let { dto ->
                    if (dto.success) {
                        println("✅ DEBUG Repository: Cita actualizada exitosamente")

                        // Mapear la respuesta a AppointmentDetailEntity
                        val appointmentDto = dto.data
                        val updatedAppointment = AppointmentDetailEntity(
                            id = appointmentDto.id,
                            fechaCita = appointmentDto.fecha_cita,
                            horaCita = appointmentDto.hora_cita,
                            estado = appointmentDto.estado,
                            motivo = appointmentDto.motivo,
                            notas = appointmentDto.notas,
                            precio = appointmentDto.precio,
                            createAt = appointmentDto.create_at,
                            updateAt = appointmentDto.update_at,
                            doctor = DoctorEntity(
                                id = appointmentDto.doctor.id,
                                nombres = appointmentDto.doctor.nombres,
                                apellidos = appointmentDto.doctor.apellidos,
                                correo = appointmentDto.doctor.correo,
                                telefono = appointmentDto.doctor.telefono,
                                especialidadId = 0, // No viene en el response
                                duracionConsulta = appointmentDto.doctor.duracion_consulta,
                                activo = true, // Valor por defecto
                                especialidad = SpecialtyEntity(
                                    id = 0, // No viene en el response
                                    nombre = appointmentDto.doctor.especialidad.nombre,
                                    descripcion = appointmentDto.doctor.especialidad.descripcion,
                                    duracionConsulta = 0, // No viene en el response
                                    precioBase = appointmentDto.doctor.especialidad.precio_base.toDoubleOrNull() ?: 0.0,
                                    activo = true // Valor por defecto
                                )
                            )
                        )

                        println("✅ DEBUG Repository: Mapeo completado exitosamente")
                        UpdateAppointmentResult.Success(
                            message = dto.message,
                            updatedAppointment = updatedAppointment
                        )
                    } else {
                        val errorMsg = dto.message ?: "Error desconocido al actualizar la cita"
                        println("❌ DEBUG Repository: dto.success = false, message = $errorMsg")
                        UpdateAppointmentResult.Error(errorMsg)
                    }
                } ?: run {
                    println("❌ DEBUG Repository: Response body es null")
                    UpdateAppointmentResult.Error("Respuesta vacía del servidor")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                println("❌ DEBUG Repository: Error body = $errorBody")

                val errorMsg = when (response.code()) {
                    400 -> "Datos inválidos para actualizar la cita"
                    401 -> {
                        println("❌ DEBUG Repository: Error 401 - Token inválido")
                        "Token inválido o expirado. Inicia sesión nuevamente."
                    }
                    403 -> "No tienes permisos para modificar esta cita"
                    404 -> "Cita no encontrada"
                    409 -> "Ya existe una cita en esa fecha y hora"
                    422 -> "Los datos enviados no son válidos"
                    500 -> "Error interno del servidor"
                    else -> "Error al actualizar cita: ${response.code()} - ${response.message()}"
                }
                println("❌ DEBUG Repository: Error final = $errorMsg")
                UpdateAppointmentResult.Error(errorMsg)
            }
        } catch (e: Exception) {
            val errorMsg = "Error de conexión: ${e.message}"
            println("❌ DEBUG Repository: Excepción = $errorMsg")
            println("❌ DEBUG Repository: Stack trace = ${e.stackTrace.contentToString()}")
            UpdateAppointmentResult.Error(errorMsg)
        }
    }

    override suspend fun getAvailableSlots(doctorId: Int, fecha: String, token: String): AvailableSlotsResult {
        return try {
            println("🔍 DEBUG Repository: === INICIANDO getAvailableSlots ===")
            println("🔍 DEBUG Repository: doctorId = $doctorId")
            println("🔍 DEBUG Repository: fecha = $fecha")
            println("🔍 DEBUG Repository: token recibido = '${token.take(20)}...'")

            val authToken = if (token.startsWith("Bearer ")) {
                token
            } else {
                "Bearer $token"
            }

            println("🔍 DEBUG Repository: authToken enviado = '${authToken.take(30)}...'")
            println("🔍 DEBUG Repository: URL completa = api/v1/appointment/available-slots?doctorId=$doctorId&fecha=$fecha")

            val response = appointmentApiService.getAvailableSlots(authToken, doctorId, fecha)

            println("🔍 DEBUG Repository: Response code = ${response.code()}")
            println("🔍 DEBUG Repository: Response message = ${response.message()}")
            println("🔍 DEBUG Repository: Response headers = ${response.headers()}")

            if (response.isSuccessful) {
                val responseBody = response.body()
                println("🔍 DEBUG Repository: Response body = $responseBody")

                responseBody?.let { dto ->
                    println("🔍 DEBUG Repository: dto.success = ${dto.success}")
                    if (dto.success) {
                        println("🔍 DEBUG Repository: dto.data.slots.size = ${dto.data.slots.size}")
                        val availableSlots = AvailableSlots(
                            fecha = dto.data.fecha,
                            doctorId = dto.data.doctor_id.toIntOrNull() ?: doctorId,
                            slots = dto.data.slots.map { slot ->
                                TimeSlot(
                                    hora = slot.hora,
                                    estado = slot.estado,
                                    disponible = slot.disponible
                                )
                            },
                            resumen = SlotSummary(
                                total = dto.data.resumen.total,
                                disponibles = dto.data.resumen.disponibles,
                                ocupados = dto.data.resumen.ocupados
                            )
                        )
                        println("✅ DEBUG Repository: Slots obtenidos exitosamente: ${availableSlots.slots.size}")
                        AvailableSlotsResult.Success(availableSlots)
                    } else {
                        println("❌ DEBUG Repository: dto.success = false")
                        AvailableSlotsResult.Error("No se pudieron obtener los horarios disponibles")
                    }
                } ?: run {
                    println("❌ DEBUG Repository: Response body es null")
                    AvailableSlotsResult.Error("Respuesta vacía del servidor")
                }
            } else {
                val errorBody = response.errorBody()?.string()
                println("❌ DEBUG Repository: Error body = $errorBody")

                val errorMsg = when (response.code()) {
                    401 -> {
                        println("❌ DEBUG Repository: Error 401 - Token inválido")
                        "Token inválido o expirado. Inicia sesión nuevamente."
                    }
                    404 -> "No se encontraron horarios para este doctor en la fecha seleccionada"
                    400 -> "Fecha o doctor inválido"
                    500 -> "Error interno del servidor"
                    else -> "Error al obtener horarios: ${response.code()} - ${response.message()}"
                }
                println("❌ DEBUG Repository: Error final = $errorMsg")
                AvailableSlotsResult.Error(errorMsg)
            }
        } catch (e: Exception) {
            val errorMsg = "Error de conexión: ${e.message}"
            println("❌ DEBUG Repository: Excepción = $errorMsg")
            println("❌ DEBUG Repository: Stack trace = ${e.stackTrace.contentToString()}")
            AvailableSlotsResult.Error(errorMsg)
        }
    }

    override suspend fun getDoctorSchedules(doctorId: Int, token: String): DoctorSchedulesResult {
        return try {
            println("🔍 DEBUG Repository: === INICIANDO getDoctorSchedules ===")
            println("🔍 DEBUG Repository: doctorId = $doctorId")
            println("🔍 DEBUG Repository: token recibido = '${token.take(20)}...'")

            val authToken = if (token.startsWith("Bearer ")) {
                token
            } else {
                "Bearer $token"
            }

            println("🔍 DEBUG Repository: authToken enviado = '${authToken.take(30)}...'")
            println("🔍 DEBUG Repository: URL completa = api/v1/schedule/doctor-schedules/$doctorId")

            val response = appointmentApiService.getDoctorSchedules(authToken, doctorId)

            println("🔍 DEBUG Repository: Response code = ${response.code()}")
            println("🔍 DEBUG Repository: Response message = ${response.message()}")

            if (response.isSuccessful) {
                val responseBody = response.body()
                println("🔍 DEBUG Repository: Response body = $responseBody")

                val schedules = responseBody?.map { dto ->
                    println("🔍 DEBUG Repository: Mapeando schedule: ${dto.dia_nombre} ${dto.hora_inicio}-${dto.hora_fin}")
                    DoctorSchedule(
                        id = dto.id,
                        doctorId = dto.doctor_id,
                        diaSemana = dto.dia_semana,
                        diaNombre = dto.dia_nombre,
                        horaInicio = dto.hora_inicio,
                        horaFin = dto.hora_fin,
                        activo = dto.activo
                    )
                } ?: emptyList()

                println("✅ DEBUG Repository: Horarios del doctor obtenidos: ${schedules.size}")
                schedules.forEach { schedule ->
                    println("   - ${schedule.diaNombre}: ${schedule.horaInicio} - ${schedule.horaFin} (activo: ${schedule.activo})")
                }
                DoctorSchedulesResult.Success(schedules)
            } else {
                val errorBody = response.errorBody()?.string()
                println("❌ DEBUG Repository: Error body = $errorBody")

                val errorMsg = when (response.code()) {
                    401 -> {
                        println("❌ DEBUG Repository: Error 401 - Token inválido")
                        "Token inválido o expirado. Inicia sesión nuevamente."
                    }
                    404 -> "No se encontraron horarios para este doctor"
                    500 -> "Error interno del servidor"
                    else -> "Error al obtener horarios del doctor: ${response.code()} - ${response.message()}"
                }
                println("❌ DEBUG Repository: Error final = $errorMsg")
                DoctorSchedulesResult.Error(errorMsg)
            }
        } catch (e: Exception) {
            val errorMsg = "Error de conexión: ${e.message}"
            println("❌ DEBUG Repository: Excepción = $errorMsg")
            println("❌ DEBUG Repository: Stack trace = ${e.stackTrace.contentToString()}")
            DoctorSchedulesResult.Error(errorMsg)
        }
    }
}