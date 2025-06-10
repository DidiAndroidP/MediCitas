package com.example.medicitas.src.Features.Dashboard.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.medicitas.src.Features.Dashboard.domain.usecase.DashboardUseCase

class DashboardViewModelFactory(
    private val dashboardUseCase: DashboardUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(dashboardUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
