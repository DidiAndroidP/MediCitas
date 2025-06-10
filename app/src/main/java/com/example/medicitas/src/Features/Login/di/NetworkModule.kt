package com.example.medicitas.src.Features.Login.di

import com.example.medicitas.src.Features.Login.data.datasource.remote.LoginApiService
import com.example.medicitas.src.Features.Login.data.repository.LoginRepositoryImpl
import com.example.medicitas.src.Features.Login.domain.repository.LoginRepository
import com.example.medicitas.src.Features.Login.domain.usecase.LoginUseCase
import com.example.medicitas.src.Features.Login.presentation.viewModel.LoginViewModelFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object LoginManualProvider {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api-medicitas.margaritaydidi.xyz/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val loginApiService: LoginApiService by lazy {
        retrofit.create(LoginApiService::class.java)
    }

    private val loginRepository: LoginRepository by lazy {
        LoginRepositoryImpl(loginApiService)
    }

    val loginViewModelFactory: LoginViewModelFactory by lazy {
        val loginUseCase = LoginUseCase(loginRepository)
        LoginViewModelFactory(loginUseCase)
    }
}

