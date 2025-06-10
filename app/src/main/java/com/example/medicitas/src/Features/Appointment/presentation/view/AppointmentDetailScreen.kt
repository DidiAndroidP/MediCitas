package com.example.medicitas.src.Features.Appointment.presentation.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.medicitas.src.Features.Appointment.presentation.viewModel.AppointmentDetailViewModel
import com.example.medicitas.src.Features.Appointment.presentation.viewModel.AppointmentUpdateViewModel
import com.example.medicitas.src.Features.Appointment.presentation.viewModel.ScheduleViewModel
import com.example.medicitas.src.Features.Appointment.presentation.viewModel.AppointmentViewModelFactory
import com.example.medicitas.src.Features.Appointment.presentation.components.TimeSlotSelector
import com.example.medicitas.src.Features.Appointment.presentation.view.DateSelector
import com.example.medicitas.src.Features.Appointment.di.AppointmentManualProvider
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentDetailScreen(
    appointmentId: Int,
    token: String,
    onClose: () -> Unit = {},
    onNavigateToEdit: (Int) -> Unit = {}
) {
    val factory: AppointmentViewModelFactory = remember {
        AppointmentManualProvider.appointmentViewModelFactory
    }

    val detailViewModel: AppointmentDetailViewModel = viewModel(factory = factory)
    val updateViewModel: AppointmentUpdateViewModel = viewModel(factory = factory)
    val scheduleViewModel: ScheduleViewModel = viewModel(factory = factory)

    val detailUiState by detailViewModel.uiState.collectAsStateWithLifecycle()
    val updateUiState by updateViewModel.uiState.collectAsStateWithLifecycle()
    val scheduleUiState by scheduleViewModel.uiState.collectAsStateWithLifecycle()

    // Estado local para controlar si estamos en modo edición
    var isEditing by remember { mutableStateOf(false) }

    // Fechas disponibles generadas
    val availableDates by remember(scheduleUiState.doctorSchedules) {
        derivedStateOf {
            if (scheduleUiState.doctorSchedules.isNotEmpty()) {
                generateAvailableDates(scheduleUiState.doctorSchedules)
            } else {
                emptyList()
            }
        }
    }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(appointmentId, token) {
        if (appointmentId > 0 && token.isNotEmpty()) {
            detailViewModel.loadAppointmentDetail(appointmentId, token)
        }
    }

    // Observar cambios en el success del update para salir del modo edición
    LaunchedEffect(updateUiState.successMessage) {
        if (updateUiState.successMessage != null && isEditing) {
            isEditing = false
            // Recargar los detalles después de actualizar
            detailViewModel.loadAppointmentDetail(appointmentId, token)
            updateViewModel.clearSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Editar cita" else "Detalles de la cita",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    // Solo mostrar botones si la cita está programada
                    if (detailUiState.appointmentDetail?.estado == "programada") {
                        if (isEditing) {
                            // Botón guardar
                            IconButton(
                                onClick = {
                                    updateViewModel.updateAppointment(appointmentId, token)
                                },
                                enabled = updateViewModel.isFormValid() && updateViewModel.hasChanges() && !updateUiState.isUpdating
                            ) {
                                if (updateUiState.isUpdating) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Guardar cambios",
                                        tint = if (updateViewModel.isFormValid() && updateViewModel.hasChanges())
                                            Color(0xFF4CAF50) else Color.Gray
                                    )
                                }
                            }
                            // Botón cancelar
                            IconButton(onClick = {
                                updateViewModel.resetForm()
                                updateViewModel.clearError()
                                scheduleViewModel.clearSelection()
                                isEditing = false
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Cancelar edición",
                                    tint = Color.Red
                                )
                            }
                        } else {
                            // Botón editar
                            IconButton(onClick = {
                                Log.d("AppointmentEdit", "=== INICIANDO MODO EDICIÓN ===")
                                val appointment = detailUiState.appointmentDetail!!

                                // Cargar datos en updateViewModel
                                updateViewModel.loadAppointment(appointmentId, token)

                                // Cargar horarios del doctor
                                scheduleViewModel.loadDoctorSchedules(appointment.doctor.id, token)

                                isEditing = true
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Editar",
                                    tint = Color.Gray
                                )
                            }
                        }
                    }

                    // Botón cerrar (solo visible cuando NO está editando)
                    if (!isEditing) {
                        IconButton(onClick = onClose) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Cerrar",
                                tint = Color.Gray
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            when {
                // Estado de carga
                detailUiState.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Cargando detalles...")
                        }
                    }
                }

                // Estado de error
                detailUiState.error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f))
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "❌ ${detailUiState.error}",
                                    color = Color.Red
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { detailViewModel.retry(appointmentId, token) }
                                ) {
                                    Text("Reintentar")
                                }
                            }
                        }
                    }
                }

                // Mostrar datos de la cita
                detailUiState.appointmentDetail != null -> {
                    val appointment = detailUiState.appointmentDetail!!

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Mostrar errores de actualización
                        if (updateUiState.error != null) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Error,
                                        contentDescription = null,
                                        tint = Color.Red
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = updateUiState.error!!,
                                        color = Color.Red,
                                        modifier = Modifier.weight(1f)
                                    )
                                    IconButton(onClick = { updateViewModel.clearError() }) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Cerrar",
                                            tint = Color.Red,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Card del Doctor (igual que el original)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                if (isEditing) {
                                    Text(
                                        text = "Doctor asignado",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )
                                }

                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Avatar del doctor
                                    Box(
                                        modifier = Modifier
                                            .size(60.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFE3F2FD)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            modifier = Modifier.size(32.dp),
                                            tint = Color(0xFF1976D2)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column {
                                        Text(
                                            text = "Dr. ${appointment.doctor.nombres} ${appointment.doctor.apellidos}",
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                        Text(
                                            text = appointment.doctor.especialidad?.nombre ?: "Especialidad no disponible",
                                            fontSize = 14.sp,
                                            color = Color.Gray
                                        )

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Phone,
                                                contentDescription = null,
                                                modifier = Modifier.size(16.dp),
                                                tint = Color.Gray
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = appointment.doctor.telefono,
                                                fontSize = 14.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (isEditing) {
                            // ========== MODO EDICIÓN ==========

                            // DateSelector usando el componente que ya tienes
                            DateSelector(
                                selectedDate = updateUiState.selectedDate,
                                availableDates = availableDates,
                                onDateSelected = { date ->
                                    Log.d("AppointmentEdit", "Fecha seleccionada: $date")

                                    // Actualizar fecha en el ViewModel
                                    updateViewModel.updateSelectedDate(date)

                                    // Limpiar selección de tiempo
                                    updateViewModel.updateSelectedTime("")
                                    scheduleViewModel.selectTime("")

                                    // Cargar slots disponibles para la nueva fecha
                                    coroutineScope.launch {
                                        try {
                                            delay(150)
                                            Log.d("AppointmentEdit", "Cargando slots para fecha: $date")
                                            scheduleViewModel.loadAvailableSlots(appointment.doctor.id, date, token)
                                        } catch (e: Exception) {
                                            Log.e("AppointmentEdit", "Error cargando slots: ${e.message}", e)
                                        }
                                    }
                                },
                                placeholder = "Selecciona una nueva fecha"
                            )

                            // TimeSlotSelector (usando el componente que ya tienes)
                            if (updateUiState.selectedDate.isNotEmpty()) {
                                TimeSlotSelector(
                                    doctorId = appointment.doctor.id,
                                    selectedDate = updateUiState.selectedDate,
                                    selectedTime = updateUiState.selectedTime,
                                    token = token,
                                    onTimeSelected = { time ->
                                        Log.d("AppointmentEdit", "Hora seleccionada: $time")
                                        scheduleViewModel.selectTime(time)
                                        updateViewModel.updateSelectedTime(time)
                                    },
                                    skipAutoLoad = true
                                )
                            }

                            // Campo de Motivo
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Motivo de la consulta",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    OutlinedTextField(
                                        value = updateUiState.motivo,
                                        onValueChange = updateViewModel::updateMotivo,
                                        placeholder = {
                                            Text(
                                                "Describe brevemente el motivo de tu consulta",
                                                color = Color.Gray.copy(alpha = 0.6f)
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.MedicalServices,
                                                contentDescription = null,
                                                tint = Color.Gray,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color.LightGray,
                                            unfocusedBorderColor = Color.LightGray
                                        ),
                                        maxLines = 3,
                                        supportingText = {
                                            Text("Mínimo 10 caracteres (${updateUiState.motivo.length}/10)")
                                        },
                                        isError = updateUiState.motivo.length < 10 && updateUiState.motivo.isNotEmpty()
                                    )
                                }
                            }

                            // Campo de Notas
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp)
                                ) {
                                    Text(
                                        text = "Notas adicionales (opcional)",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    OutlinedTextField(
                                        value = updateUiState.notas,
                                        onValueChange = updateViewModel::updateNotas,
                                        placeholder = {
                                            Text(
                                                "Información adicional que consideres importante",
                                                color = Color.Gray.copy(alpha = 0.6f)
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.Note,
                                                contentDescription = null,
                                                tint = Color.Gray,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = Color.LightGray,
                                            unfocusedBorderColor = Color.LightGray
                                        ),
                                        maxLines = 3
                                    )
                                }
                            }

                            // Indicador de cambios
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (updateViewModel.hasChanges())
                                        Color(0xFFFFF3E0) else Color(0xFFF5F5F5)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (updateViewModel.hasChanges())
                                            Icons.Default.Edit else Icons.Default.Info,
                                        contentDescription = null,
                                        tint = if (updateViewModel.hasChanges())
                                            Color(0xFFFF9800) else Color.Gray
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (updateViewModel.hasChanges())
                                            "Hay cambios pendientes" else "Sin cambios detectados",
                                        color = if (updateViewModel.hasChanges())
                                            Color(0xFFE65100) else Color.Gray
                                    )
                                }
                            }

                        } else {
                            // ========== MODO VISUALIZACIÓN ==========

                            // Card de Información de la Cita
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    InfoRow(
                                        icon = Icons.Default.DateRange,
                                        label = "Fecha",
                                        value = formatDate(appointment.fechaCita)
                                    )

                                    InfoRow(
                                        icon = Icons.Default.Schedule,
                                        label = "Hora",
                                        value = formatTime(appointment.horaCita)
                                    )

                                    InfoRow(
                                        icon = Icons.Default.MedicalServices,
                                        label = "MOTIVO",
                                        value = appointment.motivo
                                    )

                                    if (!appointment.notas.isNullOrBlank()) {
                                        InfoRow(
                                            icon = Icons.Default.Note,
                                            label = "NOTAS",
                                            value = appointment.notas
                                        )
                                    }
                                }
                            }

                            // Card de Información Adicional
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    InfoRow(
                                        icon = Icons.Default.AttachMoney,
                                        label = "COSTO",
                                        value = if (!appointment.precio.isNullOrBlank())
                                            "$${appointment.precio} MXN"
                                        else
                                            "$${appointment.doctor.especialidad?.precioBase ?: 0.0} MXN"
                                    )

                                    InfoRow(
                                        icon = Icons.Default.LocationOn,
                                        label = "UBICACIÓN",
                                        value = "Consultorio 201 - Hospital San José"
                                    )

                                    // Recordatorios
                                    Column {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Notifications,
                                                contentDescription = null,
                                                modifier = Modifier.size(20.dp),
                                                tint = Color.Gray
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "RECORDATORIOS",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = Color.Gray
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(8.dp))

                                        Column(
                                            modifier = Modifier.padding(start = 28.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = "✓ 24h antes",
                                                fontSize = 14.sp,
                                                color = Color(0xFF4CAF50)
                                            )
                                            Text(
                                                text = "✓ 2h antes",
                                                fontSize = 14.sp,
                                                color = Color(0xFF4CAF50)
                                            )
                                        }
                                    }
                                }
                            }

                            // Estado de la cita
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = when (appointment.estado) {
                                        "programada" -> Color(0xFFE8F5E8)
                                        "completada" -> Color(0xFFE3F2FD)
                                        "cancelada" -> Color(0xFFFFEBEE)
                                        else -> Color.White
                                    }
                                ),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = when (appointment.estado) {
                                            "programada" -> Icons.Default.Schedule
                                            "completada" -> Icons.Default.CheckCircle
                                            "cancelada" -> Icons.Default.Cancel
                                            else -> Icons.Default.Info
                                        },
                                        contentDescription = null,
                                        tint = when (appointment.estado) {
                                            "programada" -> Color(0xFF4CAF50)
                                            "completada" -> Color(0xFF2196F3)
                                            "cancelada" -> Color(0xFFf44336)
                                            else -> Color.Gray
                                        }
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        text = "Estado: ${appointment.estado.uppercase()}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = when (appointment.estado) {
                                            "programada" -> Color(0xFF2E7D32)
                                            "completada" -> Color(0xFF1565C0)
                                            "cancelada" -> Color(0xFFc62828)
                                            else -> Color.Black
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Espaciado final
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun InfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray
            )
            Text(
                text = value,
                fontSize = 16.sp,
                color = Color.Black
            )
        }
    }
}

// Funciones utilitarias directas en el archivo
private fun generateAvailableDates(schedules: List<com.example.medicitas.src.Features.Appointment.domain.model.DoctorSchedule>): List<String> {
    val dates = mutableListOf<String>()
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    // Obtener días de la semana que trabaja el doctor
    val workingDays = schedules.filter { it.activo }.map { it.diaSemana }

    if (workingDays.isEmpty()) return emptyList()

    // Generar fechas para los próximos 30 días
    for (i in 0..30) {
        calendar.time = Date()
        calendar.add(Calendar.DAY_OF_YEAR, i)

        // Domingo = 1, Lunes = 2, etc. en Calendar
        // Pero en nuestro sistema: Lunes = 1, Martes = 2, etc.
        val dayOfWeek = when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            Calendar.SATURDAY -> 6
            Calendar.SUNDAY -> 7
            else -> 0
        }

        if (dayOfWeek in workingDays) {
            dates.add(dateFormat.format(calendar.time))
        }
    }

    return dates
}

private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("EEEE, dd 'de' MMMM yyyy", Locale("es", "ES"))
        val date = inputFormat.parse(dateString)
        outputFormat.format(date ?: Date())
    } catch (e: Exception) {
        "Fecha no disponible"
    }
}

private fun formatTime(timeString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat("HH:mm 'hrs'", Locale.getDefault())
        val time = inputFormat.parse(timeString)
        outputFormat.format(time ?: Date())
    } catch (e: Exception) {
        "$timeString hrs"
    }
}