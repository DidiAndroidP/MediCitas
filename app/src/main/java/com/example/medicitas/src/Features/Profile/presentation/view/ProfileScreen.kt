package com.example.medicitas.src.Features.Profile.presentation.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicitas.src.Features.Profile.di.ProfileManualProvider
import com.example.medicitas.src.Features.Profile.presentation.viewModel.ProfileViewModel
import com.example.medicitas.src.Features.Profile.presentation.viewModel.ProfileViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    token: String = "",
    onNavigateBack: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val factory: ProfileViewModelFactory = remember {
        ProfileManualProvider.profileViewModelFactory
    }
    val viewModel: ProfileViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val showLogoutDialog by viewModel.showLogoutDialog.collectAsStateWithLifecycle()
    val isEditing by viewModel.isEditing.collectAsStateWithLifecycle()
    val editableProfile by viewModel.editableProfile.collectAsStateWithLifecycle()

    LaunchedEffect(token) {
        if (token.isNotEmpty()) {
            viewModel.loadUserProfileWithToken(token)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(top = 60.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Regresar",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                text = "Mi Perfil",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            // Botón de editar/guardar/cancelar
            Row {
                if (isEditing) {
                    // Botón guardar
                    IconButton(onClick = {
                        viewModel.saveProfile(token)
                    }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Guardar cambios",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    // Botón cancelar
                    IconButton(onClick = { viewModel.cancelEdit() }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cancelar edición",
                            tint = Color.Red,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                } else {
                    // Botón editar
                    IconButton(onClick = { viewModel.toggleEditMode() }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar perfil",
                            tint = Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }

        if (uiState.isLoading) {
            // LOADING STATE
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.userProfile != null) {
            val profile = uiState.userProfile!!
            val currentEditableProfile = editableProfile

            // INFORMACIÓN DEL USUARIO CON AVATAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar placeholder
                Card(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFE0E0E0)
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Avatar",
                            modifier = Modifier.size(32.dp),
                            tint = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    if (isEditing && currentEditableProfile != null) {
                        // Campos editables para nombre
                        OutlinedTextField(
                            value = currentEditableProfile.firstName,
                            onValueChange = { viewModel.updateEditableField("firstName", it) },
                            label = { Text("Nombre") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = currentEditableProfile.lastName,
                            onValueChange = { viewModel.updateEditableField("lastName", it) },
                            label = { Text("Apellido") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    } else {
                        Text(
                            text = profile.fullName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email",
                                modifier = Modifier.size(16.dp),
                                tint = Color.Gray
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = profile.email,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            // SECCIÓN DE CONTACTO
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF8F9FA)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = "Contacto",
                            modifier = Modifier.size(20.dp),
                            tint = Color(0xFF2196F3)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Contacto",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    if (isEditing && currentEditableProfile != null) {
                        OutlinedTextField(
                            value = currentEditableProfile.phone,
                            onValueChange = { viewModel.updateEditableField("phone", it) },
                            label = { Text("Teléfono") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 28.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = currentEditableProfile.email,
                            onValueChange = { viewModel.updateEditableField("email", it) },
                            label = { Text("Email") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 28.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true
                        )
                    } else {
                        Text(
                            text = profile.phone,
                            fontSize = 15.sp,
                            color = Color(0xFF424242),
                            modifier = Modifier.padding(start = 28.dp)
                        )
                    }
                }
            }

            // SECCIÓN PERSONAL
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF8F9FA)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Personal",
                            modifier = Modifier.size(20.dp),
                            tint = Color(0xFF4CAF50)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Información Personal",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    if (isEditing && currentEditableProfile != null) {
                        // Campo de edad editable
                        OutlinedTextField(
                            value = currentEditableProfile.age.toString(),
                            onValueChange = { viewModel.updateEditableField("age", it) },
                            label = { Text("Edad") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 28.dp, bottom = 8.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )

                        // Dropdown para género
                        var expanded by remember { mutableStateOf(false) }
                        val genderOptions = listOf("Masculino", "Femenino", "Otro", "Prefiero no decir")

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 28.dp)
                        ) {
                            OutlinedTextField(
                                value = currentEditableProfile.gender,
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Género") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                genderOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            viewModel.updateEditableField("gender", option)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 28.dp, bottom = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Edad:",
                                fontSize = 14.sp,
                                color = Color(0xFF666666),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "${profile.age} años",
                                fontSize = 14.sp,
                                color = Color(0xFF424242)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 28.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Género:",
                                fontSize = 14.sp,
                                color = Color(0xFF666666),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = profile.gender,
                                fontSize = 14.sp,
                                color = Color(0xFF424242)
                            )
                        }
                    }
                }
            }

            // SECCIÓN MÉDICA
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFF8F9FA)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MedicalServices,
                            contentDescription = "Médica",
                            modifier = Modifier.size(20.dp),
                            tint = Color(0xFFFF5722)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Información Médica",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    if (isEditing && currentEditableProfile != null) {
                        // Dropdown para tipo de sangre
                        var bloodTypeExpanded by remember { mutableStateOf(false) }
                        val bloodTypes = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

                        ExposedDropdownMenuBox(
                            expanded = bloodTypeExpanded,
                            onExpandedChange = { bloodTypeExpanded = !bloodTypeExpanded },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 28.dp, bottom = 8.dp)
                        ) {
                            OutlinedTextField(
                                value = currentEditableProfile.bloodType,
                                onValueChange = { },
                                readOnly = true,
                                label = { Text("Tipo de sangre") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodTypeExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth()
                            )
                            ExposedDropdownMenu(
                                expanded = bloodTypeExpanded,
                                onDismissRequest = { bloodTypeExpanded = false }
                            ) {
                                bloodTypes.forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type) },
                                        onClick = {
                                            viewModel.updateEditableField("bloodType", type)
                                            bloodTypeExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        // Campo de alergias
                        OutlinedTextField(
                            value = currentEditableProfile.allergies,
                            onValueChange = { viewModel.updateEditableField("allergies", it) },
                            label = { Text("Alergias") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 28.dp),
                            maxLines = 3,
                            placeholder = { Text("Ej: Polen, medicamentos, alimentos...") }
                        )
                    } else {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 28.dp, bottom = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Tipo de sangre:",
                                fontSize = 14.sp,
                                color = Color(0xFF666666),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = profile.bloodType,
                                fontSize = 14.sp,
                                color = Color(0xFF424242),
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 28.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Alergias:",
                                fontSize = 14.sp,
                                color = Color(0xFF666666),
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = profile.allergies,
                                fontSize = 14.sp,
                                color = Color(0xFF424242)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(80.dp))

        // BOTÓN CERRAR SESIÓN (solo visible cuando NO está editando)
        if (!isEditing) {
            Button(
                onClick = { viewModel.showLogoutDialog() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F)
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Cerrar sesión",
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Cerrar sesión",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }

    // DIÁLOGO DE CONFIRMACIÓN DE LOGOUT
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideLogoutDialog() },
            title = {
                Text(
                    text = "Cerrar sesión",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text("¿Estás seguro de que quieres cerrar sesión?")
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.hideLogoutDialog()
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F)
                    )
                ) {
                    Text("Cerrar sesión")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.hideLogoutDialog() }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}