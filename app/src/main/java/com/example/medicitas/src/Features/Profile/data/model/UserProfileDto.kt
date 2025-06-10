package com.example.medicitas.src.Features.Profile.data.model

data class UserProfileDto(
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
