package com.example.medicitas.src.Features.Dashboard.domain.model

data class DashboardData(
    val user: UserInfo? = null,
    val appointments: List<AppointmentInfo> = emptyList(),
    val appointmentCount: Int = 0
)

data class UserInfo(
    val id: Int,
    val fullName: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val age: Int,
    val gender: String,
    val allergies: String,
    val bloodType: String
)

data class AppointmentInfo(
    val id: Int,
    val doctorName: String,
    val specialty: String,
    val date: String,
    val time: String,
    val status: String,
    val isUpcoming: Boolean
)