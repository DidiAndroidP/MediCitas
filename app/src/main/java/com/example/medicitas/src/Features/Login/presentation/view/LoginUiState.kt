package com.example.medicitas.src.Features.Login.presentation.view

data class LoginUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val token: String? = null,
    val message: String? = null
)