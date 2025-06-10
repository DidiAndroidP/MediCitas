package com.example.medicitas.src.Features.Dashboard.data.model

data class UserDto(
    val id: Int,
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val contrasena: String,
    val telefono: String,
    val edad: Int,
    val genero: String,
    val alergias: String,
    val tipo_sangre: String,
    val create_at: String,
    val update_at: String
)