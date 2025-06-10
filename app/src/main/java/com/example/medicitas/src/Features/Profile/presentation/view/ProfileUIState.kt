package com.example.medicitas.src.Features.Profile.presentation.view

import com.example.medicitas.src.Features.Profile.domain.model.UserProfile

data class ProfileUIState(
    val isLoading: Boolean = false,
    val userProfile: UserProfile? = null,
    val error: String = ""
)

