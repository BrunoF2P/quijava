package org.quijava.quijava.compose.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.quijava.quijava.services.LoginService

data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed class LoginEvent {
    object NavigateToMenu : LoginEvent()
    data class ShowError(val message: String) : LoginEvent()
}

class LoginViewModel(
    private val loginService: LoginService
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = Channel<LoginEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    fun updateUsername(username: String) {
        _uiState.update { it.copy(username = username) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun login() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val success = loginService.validateLogin(_uiState.value.username, _uiState.value.password)
                if (success) {
                    val user = loginService.getUserByUsername(_uiState.value.username).orElse(null)
                    if (user != null) {
                        loginService.deleteActiveSession(_uiState.value.username)
                        loginService.createSession(_uiState.value.username, user.role, user.id)
                        val sessionId = loginService.getLastSessionId(_uiState.value.username)
                        loginService.createPreferencesSession(_uiState.value.username, sessionId, user.role, user.id)
                        
                        _uiState.update { it.copy(isLoading = false) }
                        _events.send(LoginEvent.NavigateToMenu)
                    } else {
                        _uiState.update { it.copy(isLoading = false) }
                        _events.send(LoginEvent.ShowError("User not found"))
                    }
                } else {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(LoginEvent.ShowError("Invalid credentials"))
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                _events.send(LoginEvent.ShowError(e.message ?: "Login failed"))
            }
        }
    }
}
