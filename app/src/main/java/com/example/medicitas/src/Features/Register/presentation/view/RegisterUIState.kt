package com.example.medicitas.src.Features.Register.presentation.view

data class RegisterUIState(
    val isLoading: Boolean = false,
    val error: String = "",
    val isSuccess: Boolean = false,
    val successMessage: String = ""
)