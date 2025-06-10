package com.example.medicitas.src.Features.Register.presentation.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medicitas.src.Features.Register.domain.model.RegisterCredentials
import com.example.medicitas.src.Features.Register.domain.usecase.RegisterUseCase
import com.example.medicitas.src.Features.Register.presentation.view.RegisterUIState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUIState())
    val uiState: StateFlow<RegisterUIState> = _uiState.asStateFlow()

    private val _nombres = MutableStateFlow("")
    val nombres: StateFlow<String> = _nombres.asStateFlow()

    private val _apellidos = MutableStateFlow("")
    val apellidos: StateFlow<String> = _apellidos.asStateFlow()

    private val _correo = MutableStateFlow("")
    val correo: StateFlow<String> = _correo.asStateFlow()

    private val _contrasena = MutableStateFlow("")
    val contrasena: StateFlow<String> = _contrasena.asStateFlow()

    private val _telefono = MutableStateFlow("")
    val telefono: StateFlow<String> = _telefono.asStateFlow()

    private val _edad = MutableStateFlow("")
    val edad: StateFlow<String> = _edad.asStateFlow()

    private val _genero = MutableStateFlow("")
    val genero: StateFlow<String> = _genero.asStateFlow()

    private val _alergias = MutableStateFlow("")
    val alergias: StateFlow<String> = _alergias.asStateFlow()

    private val _tipoSangre = MutableStateFlow("")
    val tipoSangre: StateFlow<String> = _tipoSangre.asStateFlow()

    private val _passwordVisible = MutableStateFlow(false)
    val passwordVisible: StateFlow<Boolean> = _passwordVisible.asStateFlow()

    private val _expandedGenero = MutableStateFlow(false)
    val expandedGenero: StateFlow<Boolean> = _expandedGenero.asStateFlow()

    private val _expandedTipoSangre = MutableStateFlow(false)
    val expandedTipoSangre: StateFlow<Boolean> = _expandedTipoSangre.asStateFlow()

    fun onNombresChanged(newNombres: String) {
        _nombres.value = newNombres
        if (_uiState.value.error.isNotEmpty()) clearError()
    }

    fun onApellidosChanged(newApellidos: String) {
        _apellidos.value = newApellidos
        if (_uiState.value.error.isNotEmpty()) clearError()
    }

    fun onCorreoChanged(newCorreo: String) {
        _correo.value = newCorreo
        if (_uiState.value.error.isNotEmpty()) clearError()
    }

    fun onContrasenaChanged(newContrasena: String) {
        _contrasena.value = newContrasena
        if (_uiState.value.error.isNotEmpty()) clearError()
    }

    fun onTelefonoChanged(newTelefono: String) {
        _telefono.value = newTelefono
        if (_uiState.value.error.isNotEmpty()) clearError()
    }

    fun onEdadChanged(newEdad: String) {
        _edad.value = newEdad
        if (_uiState.value.error.isNotEmpty()) clearError()
    }

    fun onGeneroChanged(newGenero: String) {
        _genero.value = newGenero
        _expandedGenero.value = false
        if (_uiState.value.error.isNotEmpty()) clearError()
    }

    fun onAlergiasChanged(newAlergias: String) {
        _alergias.value = newAlergias
    }

    fun onTipoSangreChanged(newTipoSangre: String) {
        _tipoSangre.value = newTipoSangre
        _expandedTipoSangre.value = false
        if (_uiState.value.error.isNotEmpty()) clearError()
    }

    fun togglePasswordVisibility() {
        _passwordVisible.value = !_passwordVisible.value
    }

    fun toggleGeneroDropdown() {
        if (!_uiState.value.isLoading) {
            _expandedGenero.value = !_expandedGenero.value
        }
    }

    fun dismissGeneroDropdown() {
        _expandedGenero.value = false
    }

    fun toggleTipoSangreDropdown() {
        if (!_uiState.value.isLoading) {
            _expandedTipoSangre.value = !_expandedTipoSangre.value
        }
    }

    fun dismissTipoSangreDropdown() {
        _expandedTipoSangre.value = false
    }

    fun register() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = "",
                isSuccess = false
            )

            val credentials = RegisterCredentials(
                nombres = _nombres.value.trim(),
                apellidos = _apellidos.value.trim(),
                correo = _correo.value.trim(),
                contrasena = _contrasena.value,
                telefono = _telefono.value.trim(),
                edad = _edad.value.toIntOrNull() ?: 0,
                genero = _genero.value,
                alergias = _alergias.value.trim(),
                tipoSangre = _tipoSangre.value
            )

            val result = registerUseCase(credentials)

            result.fold(
                onSuccess = { registerResult ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        isSuccess = true,
                        successMessage = registerResult.message
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Error desconocido"
                    )
                }
            )
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = "")
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(isSuccess = false, successMessage = "")
    }

    fun resetState() {
        _uiState.value = RegisterUIState()
        _nombres.value = ""
        _apellidos.value = ""
        _correo.value = ""
        _contrasena.value = ""
        _telefono.value = ""
        _edad.value = ""
        _genero.value = ""
        _alergias.value = ""
        _tipoSangre.value = ""
        _passwordVisible.value = false
        _expandedGenero.value = false
        _expandedTipoSangre.value = false
    }
}