package com.example.medicitas.src.Features.Dashboard.di

import com.example.medicitas.src.Features.Dashboard.data.datasource.remote.DashboardApiService
import com.example.medicitas.src.Features.Dashboard.data.repository.DashboardRepositoryImpl
import com.example.medicitas.src.Features.Dashboard.domain.repository.DashboardRepository
import com.example.medicitas.src.Features.Dashboard.domain.usecase.DashboardUseCase
import com.example.medicitas.src.Features.Dashboard.presentation.viewModel.DashboardViewModel
import com.example.medicitas.src.Features.Dashboard.presentation.viewModel.DashboardViewModelFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object DashboardManualProvider {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api-medicitas.margaritaydidi.xyz/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val dashboardApiService: DashboardApiService by lazy {
        retrofit.create(DashboardApiService::class.java)
    }

    private val dashboardRepository: DashboardRepository by lazy {
        DashboardRepositoryImpl(dashboardApiService)
    }

    val dashboardViewModelFactory: DashboardViewModelFactory by lazy {
        val dashboardUseCase = DashboardUseCase(dashboardRepository)
        DashboardViewModelFactory(dashboardUseCase)
    }
}