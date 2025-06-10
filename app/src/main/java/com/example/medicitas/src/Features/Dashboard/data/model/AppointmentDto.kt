package com.example.medicitas.src.Features.Dashboard.data.model

data class AppointmentResponseDto(
    val success: Boolean,
    val data: List<AppointmentDto>,
    val pagination: PaginationDto
)

data class AppointmentDto(
    val id: Int,
    val doctorName: String,
    val specialty: String,
    val date: String,
    val time: String,
    val status: String
)

data class PaginationDto(
    val page: Int,
    val limit: Int,
    val total: Int
)