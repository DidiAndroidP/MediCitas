package com.example.medicitas.src.Features.Appointment.presentation.view

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.medicitas.src.Features.Appointment.presentation.viewModel.AppointmentCreateViewModel
import com.example.medicitas.src.Features.Appointment.presentation.viewModel.ScheduleViewModel
import com.example.medicitas.src.Features.Appointment.presentation.viewModel.AppointmentViewModelFactory
import com.example.medicitas.src.Features.Appointment.presentation.components.TimeSlotSelector
import com.example.medicitas.src.Features.Appointment.di.AppointmentManualProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppointmentCreateScreen(
    token: String = "",
    onNavigateBack: () -> Unit = {},
    onContinue: () -> Unit = {}
) {
    // Log inicial
    Log.d("AppointmentScreen", "=== APPOINTMENT CREATE SCREEN INICIADO ===")
    Log.d("AppointmentScreen", "Token recibido: ${if(token.isNotEmpty()) "SI (${token.length} chars)" else "NO"}")

    val factory: AppointmentViewModelFactory = remember {
        Log.d("AppointmentScreen", "Creando AppointmentViewModelFactory")
        AppointmentManualProvider.appointmentViewModelFactory
    }

    val viewModel: AppointmentCreateViewModel = viewModel(factory = factory)
    val scheduleViewModel: ScheduleViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scheduleUiState by scheduleViewModel.uiState.collectAsStateWithLifecycle()

    // Logs de estado resumidos
    Log.d("AppointmentScreen", "Estado - Doctor: ${uiState.selectedDoctorId}, Fecha: '${uiState.selectedDate}', Loading: ${uiState.isLoading}")

    var especialidadExpanded by remember { mutableStateOf(false) }
    var doctorExpanded by remember { mutableStateOf(false) }
    var fechaExpanded by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(uiState.successMessage) {
        Log.d("AppointmentScreen", "LaunchedEffect - Success message: ${uiState.successMessage}")
        if (uiState.successMessage != null) {
            Log.d("AppointmentScreen", "Navegando a pantalla de éxito...")
            onContinue()
            viewModel.clearSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(start = 16.dp, top = 60.dp, end = 16.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        Log.d("AppointmentScreen", "Back button clicked")
                        onNavigateBack()
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Regresar",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Agendar Cita",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.width(48.dp))
            }

            // Loading overlay
            if (uiState.isLoading) {
                Log.d("AppointmentScreen", "Mostrando loading overlay")
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Cargando datos...")
                    }
                }
            } else {
                // Contenido scrolleable
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Error cards
                    uiState.error?.let { error ->
                        Log.e("AppointmentScreen", "Mostrando error principal: $error")
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f))
                        ) {
                            Text(
                                text = "❌ $error",
                                modifier = Modifier.padding(16.dp),
                                color = Color.Red
                            )
                        }
                    }

                    scheduleUiState.error?.let { error ->
                        Log.e("AppointmentScreen", "Mostrando error de schedule: $error")
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.1f))
                        ) {
                            Text(
                                text = "❌ Schedule: $error",
                                modifier = Modifier.padding(16.dp),
                                color = Color.Red
                            )
                        }
                    }

                    // Card de Especialidad
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
                                text = "Especialidad",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            ExposedDropdownMenuBox(
                                expanded = especialidadExpanded,
                                onExpandedChange = {
                                    Log.d("AppointmentScreen", "Especialidad dropdown expanded: $it")
                                    especialidadExpanded = !especialidadExpanded
                                }
                            ) {
                                OutlinedTextField(
                                    value = uiState.specialties.find { it.id == uiState.selectedSpecialtyId }?.nombre ?: "",
                                    onValueChange = {},
                                    readOnly = true,
                                    placeholder = {
                                        Text(
                                            "Selecciona una especialidad",
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
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = especialidadExpanded)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.LightGray,
                                        unfocusedBorderColor = Color.LightGray
                                    )
                                )

                                ExposedDropdownMenu(
                                    expanded = especialidadExpanded,
                                    onDismissRequest = {
                                        Log.d("AppointmentScreen", "Especialidad dropdown dismissed")
                                        especialidadExpanded = false
                                    }
                                ) {
                                    if (uiState.specialties.isEmpty()) {
                                        DropdownMenuItem(
                                            text = { Text("No hay especialidades disponibles") },
                                            onClick = { }
                                        )
                                    } else {
                                        uiState.specialties.forEach { specialty ->
                                            DropdownMenuItem(
                                                text = { Text(specialty.nombre) },
                                                onClick = {
                                                    Log.d("AppointmentScreen", "Especialidad seleccionada: ${specialty.nombre}")
                                                    viewModel.selectSpecialty(specialty.id)
                                                    scheduleViewModel.clearSelection()
                                                    especialidadExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Card de Doctor
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
                                text = "Doctor",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            ExposedDropdownMenuBox(
                                expanded = doctorExpanded,
                                onExpandedChange = {
                                    Log.d("AppointmentScreen", "Doctor dropdown expanded: $it")
                                    doctorExpanded = !doctorExpanded
                                }
                            ) {
                                OutlinedTextField(
                                    value = uiState.filteredDoctors.find { it.id == uiState.selectedDoctorId }?.nombreCompleto ?: "",
                                    onValueChange = {},
                                    readOnly = true,
                                    placeholder = {
                                        Text(
                                            if (uiState.selectedSpecialtyId == null) "Selecciona primero una especialidad" else "Selecciona un doctor",
                                            color = Color.Gray.copy(alpha = 0.6f)
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = null,
                                            tint = Color.Gray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = doctorExpanded)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.LightGray,
                                        unfocusedBorderColor = Color.LightGray
                                    ),
                                    enabled = uiState.selectedSpecialtyId != null
                                )

                                ExposedDropdownMenu(
                                    expanded = doctorExpanded,
                                    onDismissRequest = {
                                        Log.d("AppointmentScreen", "Doctor dropdown dismissed")
                                        doctorExpanded = false
                                    }
                                ) {
                                    if (uiState.filteredDoctors.isEmpty()) {
                                        DropdownMenuItem(
                                            text = { Text("No hay doctores disponibles") },
                                            onClick = { }
                                        )
                                    } else {
                                        uiState.filteredDoctors.forEach { doctor ->
                                            DropdownMenuItem(
                                                text = { Text(doctor.nombreCompleto) },
                                                onClick = {
                                                    Log.d("AppointmentScreen", "Doctor seleccionado: ${doctor.nombreCompleto}")
                                                    viewModel.selectDoctor(doctor.id)
                                                    scheduleViewModel.clearSelection()

                                                    if (token.isNotEmpty()) {
                                                        Log.d("AppointmentScreen", "Cargando horarios del doctor...")
                                                        scheduleViewModel.loadDoctorSchedules(doctor.id, token)
                                                    }
                                                    doctorExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Card de Fecha - OPTIMIZADO
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
                                text = "Fecha",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )

                            ExposedDropdownMenuBox(
                                expanded = fechaExpanded,
                                onExpandedChange = {
                                    Log.d("AppointmentScreen", "Fecha dropdown expanded: $it")
                                    fechaExpanded = !fechaExpanded
                                }
                            ) {
                                OutlinedTextField(
                                    value = uiState.selectedDate,
                                    onValueChange = {},
                                    readOnly = true,
                                    placeholder = {
                                        Text(
                                            if (uiState.selectedDoctorId == null) "Selecciona primero un doctor" else "Selecciona una fecha",
                                            color = Color.Gray.copy(alpha = 0.6f)
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = null,
                                            tint = Color.Gray,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    },
                                    trailingIcon = {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = fechaExpanded)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .menuAnchor(),
                                    shape = RoundedCornerShape(8.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color.LightGray,
                                        unfocusedBorderColor = Color.LightGray
                                    ),
                                    enabled = uiState.selectedDoctorId != null
                                )

                                ExposedDropdownMenu(
                                    expanded = fechaExpanded,
                                    onDismissRequest = {
                                        Log.d("AppointmentScreen", "Fecha dropdown dismissed")
                                        fechaExpanded = false
                                    }
                                ) {
                                    if (uiState.availableDates.isEmpty()) {
                                        DropdownMenuItem(
                                            text = { Text("No hay fechas disponibles") },
                                            onClick = { }
                                        )
                                    } else {
                                        uiState.availableDates.forEach { date ->
                                            DropdownMenuItem(
                                                text = { Text(date) },
                                                onClick = {
                                                    Log.d("AppointmentScreen", "=== FECHA SELECCIONADA ===")
                                                    Log.d("AppointmentScreen", "Fecha: '$date'")

                                                    try {
                                                        // Paso 1: Actualizar fecha en AppointmentViewModel
                                                        Log.d("AppointmentScreen", "PASO 1: Actualizando fecha...")
                                                        viewModel.selectDate(date)

                                                        // Paso 2: Limpiar selección de tiempo
                                                        Log.d("AppointmentScreen", "PASO 2: Limpiando tiempo...")
                                                        scheduleViewModel.selectTime("")

                                                        // Paso 3: Cerrar dropdown
                                                        Log.d("AppointmentScreen", "PASO 3: Cerrando dropdown...")
                                                        fechaExpanded = false

                                                        // Paso 4: SOLO cargar slots si es necesario
                                                        val doctorId = uiState.selectedDoctorId
                                                        if (doctorId != null && token.isNotEmpty()) {
                                                            Log.d("AppointmentScreen", "PASO 4: Iniciando carga de slots...")

                                                            // CARGA DIRECTA sin verificaciones adicionales
                                                            coroutineScope.launch {
                                                                try {
                                                                    delay(150) // Delay más largo para estabilidad
                                                                    Log.d("AppointmentScreen", "CORRUTINA: Cargando slots directamente...")
                                                                    scheduleViewModel.loadAvailableSlots(doctorId, date, token)
                                                                    Log.d("AppointmentScreen", "CORRUTINA: ✅ Completada")
                                                                } catch (e: Exception) {
                                                                    Log.e("AppointmentScreen", "CORRUTINA: ❌ Error: ${e.message}", e)
                                                                }
                                                            }
                                                        }

                                                        Log.d("AppointmentScreen", "=== FECHA SELECCIONADA - FIN ===")

                                                    } catch (e: Exception) {
                                                        Log.e("AppointmentScreen", "❌ Error en selección de fecha: ${e.message}", e)
                                                        fechaExpanded = false
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // TimeSlotSelector optimizado
                    if (uiState.selectedDoctorId != null && uiState.selectedDate.isNotEmpty()) {
                        Log.d("AppointmentScreen", "Renderizando TimeSlotSelector optimizado")
                        TimeSlotSelector(
                            doctorId = uiState.selectedDoctorId!!,
                            selectedDate = uiState.selectedDate,
                            selectedTime = scheduleUiState.selectedTime,
                            token = token,
                            onTimeSelected = { time ->
                                Log.d("AppointmentScreen", "Hora seleccionada: '$time'")
                                scheduleViewModel.selectTime(time)
                                viewModel.selectHour(time)
                            },
                            skipAutoLoad = true
                        )
                    }
                    if (uiState.selectedDoctorId == null || uiState.selectedDate.isEmpty()) {
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
                                    text = "Hora",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                OutlinedTextField(
                                    value = "",
                                    onValueChange = {},
                                    readOnly = true,
                                    placeholder = {
                                        Text(
                                            "Selecciona primero el doctor y la fecha",
                                            color = Color.Gray.copy(alpha = 0.6f)
                                        )
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Schedule,
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
                                    enabled = false
                                )
                            }
                        }
                    }

                    // Card de Motivo
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
                                value = uiState.motivo,
                                onValueChange = { viewModel.updateMotivo(it) },
                                placeholder = {
                                    Text(
                                        "Describe brevemente el motivo de tu consulta",
                                        color = Color.Gray.copy(alpha = 0.6f)
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
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

                    // Card de Notas
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
                                value = uiState.notas,
                                onValueChange = { viewModel.updateNotas(it) },
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

                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }

        // Botón continuar
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Button(
                onClick = {
                    Log.d("AppointmentScreen", "=== BOTÓN CONTINUAR PRESIONADO ===")
                    viewModel.createAppointment(token)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 24.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2C2C2C)
                ),
                enabled = uiState.selectedDoctorId != null &&
                        uiState.selectedDate.isNotEmpty() &&
                        uiState.selectedHour.isNotEmpty() &&
                        uiState.motivo.isNotBlank() &&
                        !uiState.isCreating
            ) {
                if (uiState.isCreating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "CONTINUAR",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}