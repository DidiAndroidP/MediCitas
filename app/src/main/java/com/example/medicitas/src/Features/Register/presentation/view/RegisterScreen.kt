package com.example.medicitas.src.Features.Register.presentation.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.medicitas.src.Features.Register.di.RegisterManualProvider
import com.example.medicitas.src.Features.Register.presentation.viewModel.RegisterViewModel
import com.example.medicitas.src.Features.Register.presentation.viewModel.RegisterViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val factory: RegisterViewModelFactory = remember {
        RegisterManualProvider.registerViewModelFactory
    }
    val viewModel: RegisterViewModel = viewModel(factory = factory)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val nombres by viewModel.nombres.collectAsState()
    val apellidos by viewModel.apellidos.collectAsState()
    val correo by viewModel.correo.collectAsState()
    val contrasena by viewModel.contrasena.collectAsState()
    val telefono by viewModel.telefono.collectAsState()
    val edad by viewModel.edad.collectAsState()
    val genero by viewModel.genero.collectAsState()
    val alergias by viewModel.alergias.collectAsState()
    val tipoSangre by viewModel.tipoSangre.collectAsState()
    val passwordVisible by viewModel.passwordVisible.collectAsState()
    val expandedGenero by viewModel.expandedGenero.collectAsState()
    val expandedTipoSangre by viewModel.expandedTipoSangre.collectAsState()

    val opcionesGenero = listOf("Masculino", "Femenino", "Otro")
    val opcionesTipoSangre = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onRegisterSuccess()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "CREAR CUENTA",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateToLogin) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver al login",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color(0xFF2C3E50)
                )
            )
        },
        containerColor = Color.White,
        bottomBar = {
            Surface(
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    // Botón de registro
                    Button(
                        onClick = { viewModel.register() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2C3E50)
                        ),
                        enabled = !uiState.isLoading && nombres.isNotBlank() && apellidos.isNotBlank() &&
                                correo.isNotBlank() && contrasena.isNotBlank() && telefono.isNotBlank() &&
                                edad.isNotBlank() && genero.isNotBlank() && tipoSangre.isNotBlank()
                    ) {
                        if (uiState.isLoading) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "REGISTRANDO...",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White
                                )
                            }
                        } else {
                            Text(
                                text = "REGISTRARSE",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.White
                            )
                        }
                    }


                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Complete sus datos personales",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // Campos de nombres y apellidos
            TextField(
                value = nombres,
                onValueChange = viewModel::onNombresChanged,
                label = { Text("Nombres*") },
                placeholder = { Text("Ingresa tus nombres") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Nombres")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(8.dp),
                enabled = !uiState.isLoading,
                isError = false
            )

            TextField(
                value = apellidos,
                onValueChange = viewModel::onApellidosChanged,
                label = { Text("Apellidos*") },
                placeholder = { Text("Ingresa tus apellidos") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Apellidos")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(8.dp),
                enabled = !uiState.isLoading,
                isError = false
            )

            // Campos de contacto
            TextField(
                value = correo,
                onValueChange = viewModel::onCorreoChanged,
                label = { Text("Email*") },
                placeholder = { Text("nombre@ejemplo.com") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Email, contentDescription = "Email")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(8.dp),
                enabled = !uiState.isLoading,
                isError = false
            )

            TextField(
                value = contrasena,
                onValueChange = viewModel::onContrasenaChanged,
                label = { Text("Contraseña*") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Lock, contentDescription = "Contraseña")
                },
                trailingIcon = {
                    IconButton(onClick = viewModel::togglePasswordVisibility) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(8.dp),
                enabled = !uiState.isLoading,
                isError = false
            )

            TextField(
                value = telefono,
                onValueChange = viewModel::onTelefonoChanged,
                label = { Text("Teléfono*") },
                placeholder = { Text("9611234567") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = "Teléfono")
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(8.dp),
                enabled = !uiState.isLoading,
                isError = false
            )

            // Fila de edad y género
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextField(
                    value = edad,
                    onValueChange = viewModel::onEdadChanged,
                    label = { Text("Edad*") },
                    placeholder = { Text("25") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.DateRange, contentDescription = "Edad")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !uiState.isLoading,
                    isError = false
                )

                Box(modifier = Modifier.weight(1f)) {
                    TextField(
                        value = genero,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Género*") },
                        placeholder = { Text("Seleccionar") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Person, contentDescription = "Género")
                        },
                        trailingIcon = {
                            IconButton(onClick = viewModel::toggleGeneroDropdown) {
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "Dropdown"
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !uiState.isLoading,
                        isError = false
                    )

                    DropdownMenu(
                        expanded = expandedGenero,
                        onDismissRequest = viewModel::dismissGeneroDropdown
                    ) {
                        opcionesGenero.forEach { opcion ->
                            DropdownMenuItem(
                                text = { Text(opcion) },
                                onClick = { viewModel.onGeneroChanged(opcion) }
                            )
                        }
                    }
                }
            }

            // Tipo de sangre
            Box(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = tipoSangre,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo de Sangre*") },
                    placeholder = { Text("Seleccionar") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Favorite, contentDescription = "Tipo de Sangre")
                    },
                    trailingIcon = {
                        IconButton(onClick = viewModel::toggleTipoSangreDropdown) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Dropdown"
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !uiState.isLoading,
                    isError = uiState.error.isNotEmpty()
                )

                DropdownMenu(
                    expanded = expandedTipoSangre,
                    onDismissRequest = viewModel::dismissTipoSangreDropdown
                ) {
                    opcionesTipoSangre.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = { viewModel.onTipoSangreChanged(opcion) }
                        )
                    }
                }
            }

            // Campo de alergias
            TextField(
                value = alergias,
                onValueChange = viewModel::onAlergiasChanged,
                label = { Text("Alergias (opcional)") },
                placeholder = { Text("Ejemplo: Penicilina, polen...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = "Alergias")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(8.dp),
                maxLines = 4,
                enabled = !uiState.isLoading
            )

            // Espacio extra para evitar solapamiento con bottomBar
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}