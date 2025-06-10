package com.example.medicitas.src.Features.Appointment.domain.repository

import com.example.medicitas.src.Features.Appointment.domain.model.AppointmentResult
import com.example.medicitas.src.Features.Appointment.domain.model.AppointmentDetailResult
import com.example.medicitas.src.Features.Appointment.domain.model.SpecialtiesResult
import com.example.medicitas.src.Features.Appointment.domain.model.DoctorsResult
import com.example.medicitas.src.Features.Appointment.domain.model.CreateAppointmentRequest
import com.example.medicitas.src.Features.Appointment.domain.model.CreateAppointmentResult
import com.example.medicitas.src.Features.Appointment.domain.model.AvailableSlotsResult
import com.example.medicitas.src.Features.Appointment.domain.model.DoctorSchedulesResult
import com.example.medicitas.src.Features.Appointment.domain.model.UpdateAppointmentRequest
import com.example.medicitas.src.Features.Appointment.domain.model.UpdateAppointmentResult

interface AppointmentRepository {
    suspend fun getUserAppointments(
        token: String,
        page: Int = 1,
        limit: Int = 10
    ): AppointmentResult

    suspend fun getAppointmentById(
        appointmentId: Int,
        token: String
    ): AppointmentDetailResult

    suspend fun getAllSpecialties(): SpecialtiesResult

    suspend fun getAllDoctors(): DoctorsResult

    suspend fun createAppointment(
        token: String,
        request: CreateAppointmentRequest
    ): CreateAppointmentResult

    suspend fun updateAppointment(
        appointmentId: Int,
        token: String,
        request: UpdateAppointmentRequest
    ): UpdateAppointmentResult

    suspend fun getAvailableSlots(
        doctorId: Int,
        fecha: String,
        token: String
    ): AvailableSlotsResult

    suspend fun getDoctorSchedules(
        doctorId: Int,
        token: String
    ): DoctorSchedulesResult
}