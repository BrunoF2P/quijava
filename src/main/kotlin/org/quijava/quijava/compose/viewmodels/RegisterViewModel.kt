package org.quijava.quijava.compose.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.quijava.quijava.services.RegisterService

data class RegisterUiState(
    val username: String = "",
    val password: String = "",
    val rePassword: String = "",
    val refCode: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class RegisterEvent {
    object NavigateToMenu : RegisterEvent()
    data class ShowError(val message: String) : RegisterEvent()
}

class RegisterViewModel(
    private val registerService: RegisterService
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    private val _events = Channel<RegisterEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    // Exposed optimized flows with distinctUntilChanged
    val isLoading: Flow<Boolean> = uiState.map { it.isLoading }.distinctUntilChanged()
    val errorMessage: Flow<String?> = uiState.map { it.errorMessage }.distinctUntilChanged()

    fun updateUsername(username: String) {
        _uiState.update { it.copy(username = username) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun updateRePassword(rePassword: String) {
        _uiState.update { it.copy(rePassword = rePassword) }
    }

    fun updateRefCode(refCode: String) {
        _uiState.update { it.copy(refCode = refCode) }
    }

    fun register() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                with(_uiState.value) {
                    registerService.validateFields(username, password, rePassword)
                    val user = registerService.registerUser(username, password)
                    registerService.manageUserSession(user)
                }

                _uiState.update { it.copy(isLoading = false) }
                _events.send(RegisterEvent.NavigateToMenu)

            } catch (e: IllegalArgumentException) {
                _uiState.update { it.copy(isLoading = false) }
                _events.send(RegisterEvent.ShowError(e.message ?: "Erro de validação"))
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update { it.copy(isLoading = false) }
                _events.send(RegisterEvent.ShowError("Erro durante o registro. Tente novamente."))
            }
        }
    }
}
