package com.example.medicitas.src.Features.Profile.data.model

data class UpdateUserProfileRequest(
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val telefono: String,
    val edad: Int,
    val genero: String,
    val alergias: String,
    val tipo_sangre: String
)