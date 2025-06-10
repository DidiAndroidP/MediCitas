package com.example.medicitas.src.Features.Register.data.model


data class RegisterRequestDto(
    val nombres: String,
    val apellidos: String,
    val correo: String,
    val contrasena: String,
    val telefono: String,
    val edad: Int,
    val genero: String,
    val alergias: String,
    val tipo_sangre: String
)