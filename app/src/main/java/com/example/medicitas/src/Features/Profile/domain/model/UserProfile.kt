package com.example.medicitas.src.Features.Profile.domain.model

data class UserProfile(
    val id: Int,
    val fullName: String,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String,
    val age: Int,
    val gender: String,
    val allergies: String,
    val bloodType: String,
    val memberSince: String
)