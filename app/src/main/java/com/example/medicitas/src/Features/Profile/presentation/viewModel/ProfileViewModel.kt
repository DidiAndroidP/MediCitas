package com.example.medicitas.src.Features.Profile.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicitas.src.Features.Profile.domain.usecase.ProfileUseCase
import com.example.medicitas.src.Features.Profile.domain.usecase.UpdateProfileUseCase
import com.example.medicitas.src.Features.Profile.domain.model.UpdateUserProfile
import com.example.medicitas.src.Features.Profile.presentation.view.ProfileUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import android.util.Base64
import android.util.Log
import org.json.JSONObject

class ProfileViewModel(
    private val profileUseCase: ProfileUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUIState())
    val uiState: StateFlow<ProfileUIState> = _uiState.asStateFlow()

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    // States for editable fields
    private val _editableProfile = MutableStateFlow<UpdateUserProfile?>(null)
    val editableProfile: StateFlow<UpdateUserProfile?> = _editableProfile.asStateFlow()

    private val _showLogoutDialog = MutableStateFlow(false)
    val showLogoutDialog: StateFlow<Boolean> = _showLogoutDialog.asStateFlow()

    /**
     * Main function to load user profile using only the token
     * The userId is automatically extracted from the token
     */
    fun loadUserProfileWithToken(token: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = "")
                Log.d("ProfileViewModel", "🔄 Starting profile loading with token")

                // Extract userId from token
                val userId = extractUserIdFromToken(token)

                if (userId == null) {
                    Log.e("ProfileViewModel", "❌ Could not extract userId from token")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error: Could not get user ID from token"
                    )
                    return@launch
                }

                Log.d("ProfileViewModel", "✅ Extracted UserId: $userId")

                // Call use case with extracted userId
                val result = profileUseCase(userId, token)

                result.fold(
                    onSuccess = { userProfile ->
                        Log.d("ProfileViewModel", "✅ Profile loaded successfully")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            userProfile = userProfile,
                            error = ""
                        )
                    },
                    onFailure = { exception ->
                        Log.e("ProfileViewModel", "❌ Error loading profile: ${exception.message}", exception)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = exception.message ?: "Error loading profile"
                        )
                    }
                )
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "❌ Unexpected exception in loadUserProfileWithToken: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Unexpected error: ${e.message}"
                )
            }
        }
    }

    /**
     * Alternative function for compatibility with existing code
     * Use loadUserProfileWithToken() when possible
     */
    fun loadUserProfile(userId: Int, token: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = "")

                val result = profileUseCase(userId, token)

                result.fold(
                    onSuccess = { userProfile ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            userProfile = userProfile,
                            error = ""
                        )
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = exception.message ?: "Error loading profile"
                        )
                    }
                )
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "❌ Exception in loadUserProfile: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Unexpected error: ${e.message}"
                )
            }
        }
    }

    fun toggleEditMode() {
        try {
            if (_isEditing.value) {
                // Cancel editing - reset data
                Log.d("ProfileViewModel", "🔄 Cancelling edit mode")
                _isEditing.value = false
                _editableProfile.value = null
            } else {
                // Start editing - load current data
                Log.d("ProfileViewModel", "🔄 Starting edit mode")
                _uiState.value.userProfile?.let { profile ->
                    // Ensure no field is null or empty
                    _editableProfile.value = UpdateUserProfile(
                        firstName = profile.firstName ?: "",
                        lastName = profile.lastName ?: "",
                        email = profile.email ?: "",
                        phone = profile.phone ?: "",
                        age = profile.age ?: 0,
                        gender = profile.gender ?: "",
                        allergies = profile.allergies ?: "None",
                        bloodType = profile.bloodType ?: ""
                    )
                    _isEditing.value = true

                    // Debug: Verify all fields are initialized
                    _editableProfile.value?.let { editable ->
                        Log.d("ProfileViewModel", "📊 Editable fields initialized:")
                        Log.d("ProfileViewModel", "   firstName: '${editable.firstName}'")
                        Log.d("ProfileViewModel", "   lastName: '${editable.lastName}'")
                        Log.d("ProfileViewModel", "   email: '${editable.email}'")
                        Log.d("ProfileViewModel", "   phone: '${editable.phone}'")
                        Log.d("ProfileViewModel", "   age: ${editable.age}")
                        Log.d("ProfileViewModel", "   gender: '${editable.gender}'")
                        Log.d("ProfileViewModel", "   allergies: '${editable.allergies}'")
                        Log.d("ProfileViewModel", "   bloodType: '${editable.bloodType}'")
                    }

                    Log.d("ProfileViewModel", "✅ Edit mode activated")
                } ?: run {
                    Log.e("ProfileViewModel", "❌ No profile loaded to edit")
                    _uiState.value = _uiState.value.copy(error = "Cannot edit: profile not loaded")
                }
            }
        } catch (e: Exception) {
            Log.e("ProfileViewModel", "❌ Error in toggleEditMode: ${e.message}", e)
            _uiState.value = _uiState.value.copy(error = "Error changing edit mode: ${e.message}")
        }
    }

    fun updateEditableField(field: String, value: String) {
        try {
            _editableProfile.value?.let { current ->
                _editableProfile.value = when (field) {
                    "firstName" -> current.copy(firstName = value)
                    "lastName" -> current.copy(lastName = value)
                    "email" -> current.copy(email = value)
                    "phone" -> current.copy(phone = value)
                    "age" -> current.copy(age = value.toIntOrNull() ?: current.age)
                    "gender" -> current.copy(gender = value)
                    "allergies" -> current.copy(allergies = value)
                    "bloodType" -> current.copy(bloodType = value)
                    else -> current
                }
                Log.d("ProfileViewModel", "✅ Field $field updated to: $value")
            }
        } catch (e: Exception) {
            Log.e("ProfileViewModel", "❌ Error updating field $field: ${e.message}", e)
            _uiState.value = _uiState.value.copy(error = "Error updating field")
        }
    }

    fun saveProfile(token: String) {
        viewModelScope.launch {
            try {
                Log.d("ProfileViewModel", "🔄 Starting profile save")

                _editableProfile.value?.let { updateData ->
                    Log.d("ProfileViewModel", "📊 Data to save:")
                    Log.d("ProfileViewModel", "   firstName: '${updateData.firstName}'")
                    Log.d("ProfileViewModel", "   lastName: '${updateData.lastName}'")
                    Log.d("ProfileViewModel", "   email: '${updateData.email}'")
                    Log.d("ProfileViewModel", "   phone: '${updateData.phone}'")
                    Log.d("ProfileViewModel", "   age: ${updateData.age}")
                    Log.d("ProfileViewModel", "   gender: '${updateData.gender}'")
                    Log.d("ProfileViewModel", "   allergies: '${updateData.allergies}'")
                    Log.d("ProfileViewModel", "   bloodType: '${updateData.bloodType}'")

                    // Stricter validations
                    val validationErrors = mutableListOf<String>()

                    if (updateData.firstName.trim().isEmpty()) {
                        validationErrors.add("First name is required")
                    }
                    if (updateData.lastName.trim().isEmpty()) {
                        validationErrors.add("Last name is required")
                    }
                    if (updateData.email.trim().isEmpty()) {
                        validationErrors.add("Email is required")
                    } else if (!updateData.email.contains("@") || !updateData.email.contains(".")) {
                        validationErrors.add("Email format is not valid")
                    }
                    if (updateData.phone.trim().isEmpty()) {
                        validationErrors.add("Phone is required")
                    }
                    if (updateData.age <= 0 || updateData.age > 150) {
                        validationErrors.add("Age must be between 1 and 150 years")
                    }
                    if (updateData.gender.trim().isEmpty()) {
                        validationErrors.add("Gender is required")
                    }
                    if (updateData.bloodType.trim().isEmpty()) {
                        validationErrors.add("Blood type is required")
                    }

                    if (validationErrors.isNotEmpty()) {
                        val errorMessage = validationErrors.joinToString("\n• ", "• ")
                        Log.e("ProfileViewModel", "❌ Validation errors: $errorMessage")
                        _uiState.value = _uiState.value.copy(error = errorMessage)
                        return@launch
                    }

                    // Create clean copy with trimmed data
                    val cleanUpdateData = updateData.copy(
                        firstName = updateData.firstName.trim(),
                        lastName = updateData.lastName.trim(),
                        email = updateData.email.trim().lowercase(),
                        phone = updateData.phone.trim(),
                        gender = updateData.gender.trim(),
                        allergies = updateData.allergies.trim(),
                        bloodType = updateData.bloodType.trim()
                    )

                    Log.d("ProfileViewModel", "✅ Validations passed, saving profile...")
                    updateUserProfileWithToken(token, cleanUpdateData)

                    // Small delay to ensure operation completes
                    delay(500)

                    // Only change state if there are no errors
                    if (_uiState.value.error.isEmpty()) {
                        _isEditing.value = false
                        _editableProfile.value = null
                        Log.d("ProfileViewModel", "✅ Profile saved successfully")
                    }
                } ?: run {
                    Log.e("ProfileViewModel", "❌ No editable data to save")
                    _uiState.value = _uiState.value.copy(error = "No data to save")
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "❌ Exception in saveProfile: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error saving: ${e.message}"
                )
            }
        }
    }

    fun cancelEdit() {
        try {
            Log.d("ProfileViewModel", "🔄 Cancelling edit")
            _isEditing.value = false
            _editableProfile.value = null
            // Clear any errors that might exist
            _uiState.value = _uiState.value.copy(error = "")
            Log.d("ProfileViewModel", "✅ Edit cancelled")
        } catch (e: Exception) {
            Log.e("ProfileViewModel", "❌ Error cancelling edit: ${e.message}", e)
        }
    }

    fun showLogoutDialog() {
        _showLogoutDialog.value = true
    }

    fun hideLogoutDialog() {
        _showLogoutDialog.value = false
    }

    /**
     * Updates user profile using only the token
     * The userId is automatically extracted from the token
     */
    fun updateUserProfileWithToken(token: String, updateData: UpdateUserProfile) {
        viewModelScope.launch {
            try {
                Log.d("ProfileViewModel", "🔄 Starting profile update")
                _uiState.value = _uiState.value.copy(isLoading = true, error = "")

                // Extract userId from token
                val userId = extractUserIdFromToken(token)

                if (userId == null) {
                    Log.e("ProfileViewModel", "❌ Could not extract userId for update")
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error: Could not get user ID from token"
                    )
                    return@launch
                }

                Log.d("ProfileViewModel", "✅ UserId for update: $userId")

                // Call update use case
                val result = updateProfileUseCase(userId, token, updateData)

                result.fold(
                    onSuccess = { updatedProfile ->
                        Log.d("ProfileViewModel", "✅ Profile updated successfully")
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            userProfile = updatedProfile,
                            error = ""
                        )
                    },
                    onFailure = { exception ->
                        Log.e("ProfileViewModel", "❌ Error updating profile: ${exception.message}", exception)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = exception.message ?: "Error updating profile"
                        )
                    }
                )
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "❌ Exception in updateUserProfileWithToken: ${e.message}", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Unexpected error updating: ${e.message}"
                )
            }
        }
    }

    fun refreshProfile(userId: Int, token: String) {
        loadUserProfile(userId, token)
    }

    // Function to manually clear errors
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = "")
    }

    /**
     * Extracts userId from JWT token
     * Looks for different common fields: id, userId, user_id, sub
     */
    private fun extractUserIdFromToken(token: String): Int? {
        return try {
            Log.d("ProfileTokenDebug", "🔍 Token received in Profile: ${token.take(50)}...")

            if (token.isBlank()) {
                Log.e("ProfileTokenDebug", "❌ Token is empty")
                return null
            }

            // Remove "Bearer " prefix if it exists
            val cleanToken = if (token.startsWith("Bearer ", ignoreCase = true)) {
                token.substring(7)
            } else {
                token
            }

            // Split JWT parts (header.payload.signature)
            val parts = cleanToken.split(".")
            if (parts.size != 3) {
                Log.e("ProfileTokenDebug", "❌ Token doesn't have 3 parts")
                return null
            }

            // Decode payload (second part)
            val payload = parts[1]
            val paddedPayload = when (payload.length % 4) {
                2 -> payload + "=="
                3 -> payload + "="
                else -> payload
            }

            val decodedBytes = Base64.decode(paddedPayload, Base64.URL_SAFE)
            val decodedString = String(decodedBytes)
            val jsonObject = JSONObject(decodedString)

            val userId = when {
                jsonObject.has("id") -> jsonObject.getInt("id")
                jsonObject.has("userId") -> jsonObject.getInt("userId")
                jsonObject.has("user_id") -> jsonObject.getInt("user_id")
                jsonObject.has("sub") -> jsonObject.getInt("sub")
                else -> null
            }

            Log.d("ProfileTokenDebug", "✅ Extracted UserId: $userId")
            return userId

        } catch (e: Exception) {
            Log.e("ProfileTokenDebug", "❌ Error extracting ID: ${e.message}", e)
            null
        }
    }
}