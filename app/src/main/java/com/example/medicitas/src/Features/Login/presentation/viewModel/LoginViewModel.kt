package com.example.medicitas.src.Features.Login.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicitas.src.Features.Login.domain.model.LoginCredentials
import com.example.medicitas.src.Features.Login.domain.usecase.LoginUseCase
import com.example.medicitas.src.Features.Login.presentation.view.LoginUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _passwordVisible = MutableStateFlow(false)
    val passwordVisible: StateFlow<Boolean> = _passwordVisible.asStateFlow()

    fun onEmailChanged(newEmail: String) {
        _email.value = newEmail
        if (_uiState.value.error != null) clearError()
    }

    fun onPasswordChanged(newPassword: String) {
        _password.value = newPassword
        if (_uiState.value.error != null) clearError()
    }

    fun togglePasswordVisibility() {
        _passwordVisible.value = !_passwordVisible.value
    }

    fun login() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                isSuccess = false
            )

            val credentials = LoginCredentials(_email.value, _password.value)

            loginUseCase(credentials)
                .onSuccess { result ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        token = result.token,
                        message = result.message
                    )
                }
                .onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error desconocido"
                    )
                }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false, message = null)
    }

    fun resetState() {
        _uiState.value = LoginUiState()
        _email.value = ""
        _password.value = ""
        _passwordVisible.value = false
    }
}