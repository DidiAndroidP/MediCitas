package com.example.medicitas.src.Features.Profile.di

import com.example.medicitas.src.Features.Profile.data.datasource.remote.ProfileApiService
import com.example.medicitas.src.Features.Profile.data.repository.ProfileRepositoryImpl
import com.example.medicitas.src.Features.Profile.domain.repository.ProfileRepository
import com.example.medicitas.src.Features.Profile.domain.usecase.ProfileUseCase
import com.example.medicitas.src.Features.Profile.domain.usecase.UpdateProfileUseCase
import com.example.medicitas.src.Features.Profile.presentation.viewModel.ProfileViewModelFactory
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ProfileManualProvider {

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api-medicitas.margaritaydidi.xyz/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val profileApiService: ProfileApiService by lazy {
        retrofit.create(ProfileApiService::class.java)
    }

    private val profileRepository: ProfileRepository by lazy {
        ProfileRepositoryImpl(profileApiService)
    }

    private val profileUseCase: ProfileUseCase by lazy {
        ProfileUseCase(profileRepository)
    }

    private val updateProfileUseCase: UpdateProfileUseCase by lazy {
        UpdateProfileUseCase(profileRepository)
    }

    val profileViewModelFactory: ProfileViewModelFactory by lazy {
        ProfileViewModelFactory(
            profileUseCase = profileUseCase,
            updateProfileUseCase = updateProfileUseCase
        )
    }
}