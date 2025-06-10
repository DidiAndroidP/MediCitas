package com.example.medicitas.src.Features.Profile.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medicitas.src.Features.Profile.domain.usecase.ProfileUseCase
import com.example.medicitas.src.Features.Profile.domain.usecase.UpdateProfileUseCase

class ProfileViewModelFactory(
    private val profileUseCase: ProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(
                profileUseCase = profileUseCase,
                updateProfileUseCase = updateProfileUseCase
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}