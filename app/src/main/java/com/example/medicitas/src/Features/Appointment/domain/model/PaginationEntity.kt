package com.example.medicitas.src.Features.Appointment.domain.model

data class PaginationEntity(
    val page: Int,
    val limit: Int,
    val total: Int
)