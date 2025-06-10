package com.example.medicitas.src.Features.Appointment.data.model

data class CreateAppointmentResponseDto(
    val success: Boolean,
    val message: String,
    val data: CreatedAppointmentDataDto
)

data class CreatedAppointmentDataDto(
    val id: Int,
    val fecha_cita: String,
    val hora_cita: String,
    val estado: String,
    val motivo: String,
    val notas: String?,
    val precio: String?,
    val create_at: String,
    val update_at: String,
    val doctor: CreatedAppointmentDoctorDto
)

data class CreatedAppointmentDoctorDto(
    val id: Int,
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val telefono: String,
    val duracion_consulta: Int,
    val especialidad: CreatedAppointmentSpecialtyDto
)

data class CreatedAppointmentSpecialtyDto(
    val nombre: String,
    val descripcion: String,
    val precio_base: String
)