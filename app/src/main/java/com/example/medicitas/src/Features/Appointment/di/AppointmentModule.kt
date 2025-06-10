package com.example.medicitas.src.Features.Appointment.di

import com.example.medicitas.src.Features.Appointment.data.datasource.remote.AppointmentApiService
import com.example.medicitas.src.Features.Appointment.data.repository.AppointmentRepositoryImpl
import com.example.medicitas.src.Features.Appointment.domain.repository.AppointmentRepository
import com.example.medicitas.src.Features.Appointment.domain.usecase.*
import com.example.medicitas.src.Features.Appointment.presentation.viewModel.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object AppointmentManualProvider {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api-medicitas.margaritaydidi.xyz/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val appointmentApiService: AppointmentApiService by lazy {
        retrofit.create(AppointmentApiService::class.java)
    }

    private val appointmentRepository: AppointmentRepository by lazy {
        AppointmentRepositoryImpl(appointmentApiService)
    }

    private val getUserAppointmentsUseCase: GetUserAppointmentsUseCase by lazy {
        GetUserAppointmentsUseCase(appointmentRepository)
    }

    private val getAppointmentByIdUseCase: GetAppointmentByIdUseCase by lazy {
        GetAppointmentByIdUseCase(appointmentRepository)
    }

    private val getSpecialtiesUseCase: GetSpecialtiesUseCase by lazy {
        GetSpecialtiesUseCase(appointmentRepository)
    }

    private val getDoctorsUseCase: GetDoctorsUseCase by lazy {
        GetDoctorsUseCase(appointmentRepository)
    }

    private val createAppointmentUseCase: CreateAppointmentUseCase by lazy {
        CreateAppointmentUseCase(appointmentRepository)
    }

    private val updateAppointmentUseCase: UpdateAppointmentUseCase by lazy {
        UpdateAppointmentUseCase(appointmentRepository)
    }

    private val getAvailableSlotsUseCase: GetAvailableSlotsUseCase by lazy {
        GetAvailableSlotsUseCase(appointmentRepository)
    }

    private val getDoctorSchedulesUseCase: GetDoctorSchedulesUseCase by lazy {
        GetDoctorSchedulesUseCase(appointmentRepository)
    }

    private val validateAppointmentTimeUseCase: ValidateAppointmentTimeUseCase by lazy {
        ValidateAppointmentTimeUseCase()
    }

    private val checkDoctorAvailabilityUseCase: CheckDoctorAvailabilityUseCase by lazy {
        CheckDoctorAvailabilityUseCase()
    }

    val appointmentViewModelFactory: AppointmentViewModelFactory by lazy {
        AppointmentViewModelFactory(
            getUserAppointmentsUseCase = getUserAppointmentsUseCase,
            getAppointmentByIdUseCase = getAppointmentByIdUseCase,
            getSpecialtiesUseCase = getSpecialtiesUseCase,
            getDoctorsUseCase = getDoctorsUseCase,
            createAppointmentUseCase = createAppointmentUseCase,
            updateAppointmentUseCase = updateAppointmentUseCase,
            getAvailableSlotsUseCase = getAvailableSlotsUseCase,
            getDoctorSchedulesUseCase = getDoctorSchedulesUseCase,
            validateAppointmentTimeUseCase = validateAppointmentTimeUseCase,
            checkDoctorAvailabilityUseCase = checkDoctorAvailabilityUseCase
        )
    }

    val appointmentViewModel: AppointmentViewModel by lazy {
        AppointmentViewModel(getUserAppointmentsUseCase)
    }

    fun createAppointmentViewModel(): AppointmentViewModel {
        return AppointmentViewModel(getUserAppointmentsUseCase)
    }

    fun createAppointmentDetailViewModel(): AppointmentDetailViewModel {
        return AppointmentDetailViewModel(getAppointmentByIdUseCase)
    }

    fun createAppointmentCreateViewModel(): AppointmentCreateViewModel {
        return AppointmentCreateViewModel(
            getSpecialtiesUseCase = getSpecialtiesUseCase,
            getDoctorsUseCase = getDoctorsUseCase,
            createAppointmentUseCase = createAppointmentUseCase
        )
    }

    fun createAppointmentUpdateViewModel(): AppointmentUpdateViewModel {
        return AppointmentUpdateViewModel(
            updateAppointmentUseCase = updateAppointmentUseCase,
            getAppointmentByIdUseCase = getAppointmentByIdUseCase
        )
    }

    fun createScheduleViewModel(): ScheduleViewModel {
        return ScheduleViewModel(
            getAvailableSlotsUseCase = getAvailableSlotsUseCase,
            getDoctorSchedulesUseCase = getDoctorSchedulesUseCase,
            validateAppointmentTimeUseCase = validateAppointmentTimeUseCase,
            checkDoctorAvailabilityUseCase = checkDoctorAvailabilityUseCase
        )
    }
}