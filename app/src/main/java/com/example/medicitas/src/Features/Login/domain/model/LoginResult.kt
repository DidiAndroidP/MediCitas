package com.example.medicitas.src.Features.Login.domain.model

data class LoginResult(
    val message: String,
    val token: String,
    val isSuccess: Boolean
)