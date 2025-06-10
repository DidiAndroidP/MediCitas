package com.example.medicitas.src.Features.Register.domain.model

data class RegisterCredentials(
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val contrasena: String,
    val telefono: String,
    val edad: Int,
    val genero: String,
    val alergias: String,
    val tipoSangre: String
)
