package com.example.medicitas.src.Features.Appointment.data.datasource.remote

import com.example.medicitas.src.Features.Appointment.data.model.AppointmentDetailResponseDto
import com.example.medicitas.src.Features.Appointment.data.model.AppointmentResponseDto
import com.example.medicitas.src.Features.Appointment.data.model.CreateAppointmentRequestDto
import com.example.medicitas.src.Features.Appointment.data.model.CreateAppointmentResponseDto
import com.example.medicitas.src.Features.Appointment.data.model.SpecialtyResponseDto
import com.example.medicitas.src.Features.Appointment.data.model.DoctorResponseDto
import com.example.medicitas.src.Features.Appointment.data.model.AvailableSlotsResponseDto
import com.example.medicitas.src.Features.Appointment.data.model.DoctorScheduleDto
import com.example.medicitas.src.Features.Appointment.data.model.UpdateAppointmentRequestDto
import com.example.medicitas.src.Features.Appointment.data.model.UpdateAppointmentResponseDto
import retrofit2.Response
import retrofit2.http.*

interface AppointmentApiService {
    @GET("api/v1/appointment/user-appointments")
    suspend fun getUserAppointments(
        @Header("Authorization") token: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<AppointmentResponseDto>

    @GET("api/v1/appointment/{appointmentId}")
    suspend fun getAppointmentById(
        @Path("appointmentId") appointmentId: Int,
        @Header("Authorization") token: String
    ): Response<AppointmentDetailResponseDto>

    @GET("api/v1/specialty/get-all")
    suspend fun getAllSpecialties(): Response<List<SpecialtyResponseDto>>

    @GET("api/v1/doctor/get-all")
    suspend fun getAllDoctors(): Response<List<DoctorResponseDto>>

    @POST("api/v1/appointment/create")
    suspend fun createAppointment(
        @Header("Authorization") token: String,
        @Body request: CreateAppointmentRequestDto
    ): Response<CreateAppointmentResponseDto>

    @GET("api/v1/appointment/available-slots")
    suspend fun getAvailableSlots(
        @Header("Authorization") token: String,
        @Query("doctorId") doctorId: Int,
        @Query("fecha") fecha: String
    ): Response<AvailableSlotsResponseDto>

    @GET("api/v1/schedule/doctor-schedules/{doctorId}")
    suspend fun getDoctorSchedules(
        @Header("Authorization") token: String,
        @Path("doctorId") doctorId: Int
    ): Response<List<DoctorScheduleDto>>

    @PUT("api/v1/appointment/update/{appointmentId}")
    suspend fun updateAppointment(
        @Path("appointmentId") appointmentId: Int,
        @Header("Authorization") token: String,
        @Body request: UpdateAppointmentRequestDto
    ): Response<UpdateAppointmentResponseDto>
}