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
    val rememberMe: Boolean = false,
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

    init {
        loadRememberedUsername()
    }
    fun updateUsername(username: String) {
        _uiState.update { it.copy(username = username) }
    }

    fun updatePassword(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun toggleRememberMe(checked: Boolean) {
        _uiState.update { it.copy(rememberMe = checked) }
    }

    fun login() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val (username, password, rememberMe) = _uiState.value.let {
                    Triple(it.username, it.password, it.rememberMe)
                }

                if (!loginService.validateLogin(username, password)) {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.send(LoginEvent.ShowError("Credenciais inválidas"))
                    return@launch
                }

                val user = loginService.getUserByUsername(username)
                    .orElseThrow { IllegalStateException("Usuário não encontrado") }

                loginService.createFullSession(username, user.role, user.id)

                if (rememberMe) {
                    loginService.saveRememberedUsername(username)
                } else {
                    loginService.clearRememberedUsername()
                }

                _uiState.update { it.copy(isLoading = false) }
                _events.send(LoginEvent.NavigateToMenu)

            } catch (e: IllegalArgumentException) {
                _uiState.update { it.copy(isLoading = false) }
                _events.send(LoginEvent.ShowError(e.message ?: "Erro de validação"))
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                _events.send(LoginEvent.ShowError("Erro ao fazer login"))
                e.printStackTrace()
            }
        }
    }

    fun loadRememberedUsername() {
        viewModelScope.launch(Dispatchers.IO) {
            loginService.getRememberedUsername().ifPresent { username ->
                _uiState.update {
                    it.copy(
                        username = username,
                        rememberMe = true
                    )
                }
            }
        }
    }
}
