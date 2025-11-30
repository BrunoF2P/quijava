package org.quijava.quijava.compose.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.quijava.quijava.models.RankingModel
import org.quijava.quijava.services.LoginService
import org.quijava.quijava.services.RankService
import org.quijava.quijava.services.SessionPreferencesService
import org.springframework.stereotype.Component

data class HistoryUiState(
    val history: List<RankingModel> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@Component
class HistoryViewModel(
    private val rankService: RankService,
    private val sessionPreferencesService: SessionPreferencesService,
    private val loginService: LoginService
) : ViewModel() {

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    fun loadHistory() {
        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val userId = sessionPreferencesService.userId.orElse(null)
                if (userId != null) {
                    val user = loginService.findById(userId).orElse(null)
                    if (user != null) {
                        val history = rankService.getUserHistory(user)
                        _uiState.update { 
                            it.copy(
                                history = history,
                                isLoading = false
                            ) 
                        }
                    } else {
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = "Usuário não encontrado"
                            ) 
                        }
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = "Sessão inválida"
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Erro ao carregar histórico: ${e.message}"
                    ) 
                }
            }
        }
    }
}
