package com.example.medicitas.src.Features.Profile.domain.model

data class UpdateUserProfile(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val phone: String = "",
    val age: Int = 0,
    val gender: String = "",
    val allergies: String = "Ninguna",
    val bloodType: String = ""
) {
    fun isValid(): Boolean {
        return firstName.isNotBlank() &&
                lastName.isNotBlank() &&
                email.isNotBlank() &&
                email.contains("@") &&
                phone.isNotBlank() &&
                age > 0 &&
                gender.isNotBlank() &&
                bloodType.isNotBlank()
    }
    fun getValidationErrors(): List<String> {
        val errors = mutableListOf<String>()

        if (firstName.isBlank()) errors.add("El nombre es obligatorio")
        if (lastName.isBlank()) errors.add("El apellido es obligatorio")
        if (email.isBlank()) errors.add("El email es obligatorio")
        else if (!email.contains("@")) errors.add("El email debe ser válido")
        if (phone.isBlank()) errors.add("El teléfono es obligatorio")
        if (age <= 0) errors.add("La edad debe ser mayor a 0")
        if (gender.isBlank()) errors.add("El género es obligatorio")
        if (bloodType.isBlank()) errors.add("El tipo de sangre es obligatorio")

        return errors
    }
}