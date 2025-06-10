package com.example.medicitas.src.Features.Appointment.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicitas.src.Features.Appointment.presentation.viewModel.ScheduleViewModel
import com.example.medicitas.src.Features.Appointment.presentation.viewModel.AppointmentViewModelFactory
import com.example.medicitas.src.Features.Appointment.di.AppointmentManualProvider
import com.example.medicitas.src.Features.Appointment.domain.model.TimeSlot

@Composable
fun TimeSlotSelector(
    doctorId: Int,
    selectedDate: String,
    selectedTime: String,
    token: String,
    onTimeSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    skipAutoLoad: Boolean = false
) {
    val factory: AppointmentViewModelFactory = remember {
        AppointmentManualProvider.appointmentViewModelFactory
    }
    val scheduleViewModel: ScheduleViewModel = viewModel(factory = factory)
    val uiState by scheduleViewModel.uiState.collectAsStateWithLifecycle()

    val isDateAvailable = remember(selectedDate, uiState.doctorSchedules) {
        try {
            if (selectedDate.isNotEmpty() && uiState.doctorSchedules.isNotEmpty()) {
                scheduleViewModel.isDoctorAvailableOnDate(selectedDate)
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color(0xFF1976D2)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Horarios Disponibles",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            when {
                selectedDate.isEmpty() -> {
                    Text(
                        text = "Selecciona una fecha para ver los horarios disponibles",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                uiState.doctorSchedules.isEmpty() -> {
                    Text(
                        text = "Cargando información del doctor...",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                !isDateAvailable -> {
                    Column {
                        Text(
                            text = "El doctor no atiende el día seleccionado",
                            fontSize = 14.sp,
                            color = Color.Red
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        val workingDays = try {
                            scheduleViewModel.getWorkingDaysNames()
                        } catch (e: Exception) {
                            "Error al obtener días"
                        }

                        Text(
                            text = "Días de atención: $workingDays",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }

                uiState.isLoading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Cargando horarios...",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }

                uiState.error != null -> {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f))
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "❌ ${uiState.error}",
                                fontSize = 12.sp,
                                color = Color.Red
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            TextButton(
                                onClick = {
                                    if (token.isNotEmpty()) {
                                        scheduleViewModel.retry(doctorId, selectedDate, token)
                                    }
                                }
                            ) {
                                Text("Reintentar", fontSize = 12.sp)
                            }
                        }
                    }
                }

                uiState.availableSlots != null -> {
                    val slots = uiState.availableSlots!!.slots
                    val availableSlots = slots.filter { it.disponible }

                    if (availableSlots.isEmpty()) {
                        Text(
                            text = "No hay horarios disponibles para esta fecha",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    } else {
                        Column {
                            Text(
                                text = "Disponibles: ${uiState.availableSlots!!.resumen.disponibles} de ${uiState.availableSlots!!.resumen.total}",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            val slotsChunks = availableSlots.chunked(3)

                            slotsChunks.forEach { rowSlots ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowSlots.forEach { slot ->
                                        TimeSlotItem(
                                            timeSlot = slot,
                                            isSelected = selectedTime == slot.hora,
                                            onClick = { onTimeSelected(slot.hora) },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    repeat(3 - rowSlots.size) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                            }

                            // Mostrar horarios ocupados si los hay
                            val occupiedSlots = slots.filter { !it.disponible }
                            if (occupiedSlots.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Horarios ocupados:",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray
                                )
                                Spacer(modifier = Modifier.height(4.dp))

                                val occupiedChunks = occupiedSlots.chunked(3)
                                occupiedChunks.forEach { rowSlots ->
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        rowSlots.forEach { slot ->
                                            TimeSlotItem(
                                                timeSlot = slot,
                                                isSelected = false,
                                                onClick = { /* No hacer nada */ },
                                                modifier = Modifier.weight(1f)
                                            )
                                        }

                                        repeat(3 - rowSlots.size) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }

                else -> {
                    Text(
                        text = "Selecciona una fecha para ver horarios disponibles",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Composable
private fun TimeSlotItem(
    timeSlot: TimeSlot,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = when {
        !timeSlot.disponible -> Color.Gray.copy(alpha = 0.2f)
        isSelected -> Color(0xFF1976D2)
        else -> Color.Transparent
    }

    val textColor = when {
        !timeSlot.disponible -> Color.Gray
        isSelected -> Color.White
        else -> Color.Black
    }

    val borderColor = when {
        !timeSlot.disponible -> Color.Gray.copy(alpha = 0.3f)
        isSelected -> Color(0xFF1976D2)
        else -> Color.Gray.copy(alpha = 0.5f)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(enabled = timeSlot.disponible) { onClick() }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = timeSlot.hora,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = textColor
        )
    }
}