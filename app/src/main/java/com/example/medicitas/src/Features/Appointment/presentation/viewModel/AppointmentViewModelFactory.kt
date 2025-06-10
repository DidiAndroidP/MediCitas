package com.example.medicitas.src.Features.Appointment.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medicitas.src.Features.Appointment.domain.usecase.*

class AppointmentViewModelFactory(
    private val getUserAppointmentsUseCase: GetUserAppointmentsUseCase,
    private val getAppointmentByIdUseCase: GetAppointmentByIdUseCase? = null,
    private val getSpecialtiesUseCase: GetSpecialtiesUseCase? = null,
    private val getDoctorsUseCase: GetDoctorsUseCase? = null,
    private val createAppointmentUseCase: CreateAppointmentUseCase? = null,
    private val updateAppointmentUseCase: UpdateAppointmentUseCase? = null,
    private val getAvailableSlotsUseCase: GetAvailableSlotsUseCase? = null,
    private val getDoctorSchedulesUseCase: GetDoctorSchedulesUseCase? = null,
    private val validateAppointmentTimeUseCase: ValidateAppointmentTimeUseCase? = null,
    private val checkDoctorAvailabilityUseCase: CheckDoctorAvailabilityUseCase? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AppointmentViewModel::class.java) -> {
                AppointmentViewModel(getUserAppointmentsUseCase) as T
            }
            modelClass.isAssignableFrom(AppointmentDetailViewModel::class.java) -> {
                if (getAppointmentByIdUseCase != null) {
                    AppointmentDetailViewModel(getAppointmentByIdUseCase) as T
                } else {
                    throw IllegalArgumentException("GetAppointmentByIdUseCase is required for AppointmentDetailViewModel")
                }
            }
            modelClass.isAssignableFrom(AppointmentCreateViewModel::class.java) -> {
                if (getSpecialtiesUseCase != null &&
                    getDoctorsUseCase != null &&
                    createAppointmentUseCase != null) {
                    AppointmentCreateViewModel(
                        getSpecialtiesUseCase = getSpecialtiesUseCase,
                        getDoctorsUseCase = getDoctorsUseCase,
                        createAppointmentUseCase = createAppointmentUseCase
                    ) as T
                } else {
                    throw IllegalArgumentException(
                        "GetSpecialtiesUseCase, GetDoctorsUseCase and CreateAppointmentUseCase are required for AppointmentCreateViewModel"
                    )
                }
            }
            modelClass.isAssignableFrom(AppointmentUpdateViewModel::class.java) -> {
                if (updateAppointmentUseCase != null && getAppointmentByIdUseCase != null) {
                    AppointmentUpdateViewModel(
                        updateAppointmentUseCase = updateAppointmentUseCase,
                        getAppointmentByIdUseCase = getAppointmentByIdUseCase
                    ) as T
                } else {
                    throw IllegalArgumentException(
                        "UpdateAppointmentUseCase and GetAppointmentByIdUseCase are required for AppointmentUpdateViewModel"
                    )
                }
            }
            modelClass.isAssignableFrom(ScheduleViewModel::class.java) -> {
                if (getAvailableSlotsUseCase != null &&
                    getDoctorSchedulesUseCase != null &&
                    validateAppointmentTimeUseCase != null &&
                    checkDoctorAvailabilityUseCase != null) {
                    ScheduleViewModel(
                        getAvailableSlotsUseCase = getAvailableSlotsUseCase,
                        getDoctorSchedulesUseCase = getDoctorSchedulesUseCase,
                        validateAppointmentTimeUseCase = validateAppointmentTimeUseCase,
                        checkDoctorAvailabilityUseCase = checkDoctorAvailabilityUseCase
                    ) as T
                } else {
                    throw IllegalArgumentException(
                        "GetAvailableSlotsUseCase, GetDoctorSchedulesUseCase, ValidateAppointmentTimeUseCase and CheckDoctorAvailabilityUseCase are required for ScheduleViewModel"
                    )
                }
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}