package com.example.medicitas.src.Features.Register.di

import com.example.medicitas.src.Features.Register.data.datasource.remote.RegisterApiService
import com.example.medicitas.src.Features.Register.data.repository.RegisterRepositoryImpl
import com.example.medicitas.src.Features.Register.domain.repository.RegisterRepository
import com.example.medicitas.src.Features.Register.domain.usecase.RegisterUseCase
import com.example.medicitas.src.Features.Register.presentation.viewModel.RegisterViewModel
import com.example.medicitas.src.Features.Register.presentation.viewModel.RegisterViewModelFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RegisterManualProvider {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api-medicitas.margaritaydidi.xyz/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val registerApiService: RegisterApiService by lazy {
        retrofit.create(RegisterApiService::class.java)
    }

    private val registerRepository: RegisterRepository by lazy {
        RegisterRepositoryImpl(registerApiService)
    }

    val registerViewModelFactory: RegisterViewModelFactory by lazy {
        val registerUseCase = RegisterUseCase(registerRepository)
        RegisterViewModelFactory(registerUseCase)
    }
}

