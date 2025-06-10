package com.example.medicitas.src.Features.Dashboard.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicitas.src.Features.Dashboard.domain.usecase.DashboardUseCase
import com.example.medicitas.src.Features.Dashboard.presentation.view.DashboardUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.util.Base64
import org.json.JSONObject

class DashboardViewModel(
    private val dashboardUseCase: DashboardUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUIState())
    val uiState: StateFlow<DashboardUIState> = _uiState.asStateFlow()

    fun loadDashboardDataWithToken(token: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = "")

            val userId = extractUserIdFromToken(token)

            if (userId == null) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error: No se pudo obtener el ID del usuario del token"
                )
                return@launch
            }

            val result = dashboardUseCase(userId, token)

            result.fold(
                onSuccess = { dashboardData ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        dashboardData = dashboardData,
                        error = ""
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al cargar datos"
                    )
                }
            )
        }
    }

    fun loadDashboardData(userId: Int, token: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = "")

            val result = dashboardUseCase(userId, token)

            result.fold(
                onSuccess = { dashboardData ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        dashboardData = dashboardData,
                        error = ""
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error al cargar datos"
                    )
                }
            )
        }
    }

    private fun extractUserIdFromToken(token: String): Int? {
        return try {
            if (token.isBlank()) return null

            val cleanToken = if (token.startsWith("Bearer ", ignoreCase = true)) {
                token.substring(7)
            } else {
                token
            }

            val parts = cleanToken.split(".")
            if (parts.size != 3) return null

            val payload = parts[1]
            val paddedPayload = when (payload.length % 4) {
                2 -> payload + "=="
                3 -> payload + "="
                else -> payload
            }

            val decodedBytes = Base64.decode(paddedPayload, Base64.URL_SAFE)
            val decodedString = String(decodedBytes)
            val jsonObject = JSONObject(decodedString)

            when {
                jsonObject.has("id") -> jsonObject.getInt("id")
                jsonObject.has("userId") -> jsonObject.getInt("userId")
                jsonObject.has("user_id") -> jsonObject.getInt("user_id")
                jsonObject.has("sub") -> jsonObject.getInt("sub")
                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }
}