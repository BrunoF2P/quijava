package org.quijava.quijava.compose.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.quijava.quijava.models.QuizModel
import org.quijava.quijava.services.CategoryService
import org.quijava.quijava.services.ImageService
import org.quijava.quijava.services.QuizService

data class CreateQuizUiState(
    val title: String = "",
    val description: String = "",
    val categories: List<String> = emptyList(),
    val selectedCategories: Set<String> = emptySet(),
    val selectedImageBytes: ByteArray? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val showCategoryDialog: Boolean = false
)

sealed class CreateQuizEvent {
    data class QuizCreated(val quiz: QuizModel) : CreateQuizEvent()
    data class ShowError(val message: String) : CreateQuizEvent()
}

class CreateQuizViewModel(
    private val quizService: QuizService,
    private val categoryService: CategoryService,
    private val imageService: ImageService
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateQuizUiState())
    val uiState: StateFlow<CreateQuizUiState> = _uiState.asStateFlow()

    private val _events = Channel<CreateQuizEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    // Optimized flows
    val isLoading: Flow<Boolean> = uiState.map { it.isLoading }.distinctUntilChanged()
    val errorMessage: Flow<String?> = uiState.map { it.errorMessage }.distinctUntilChanged()
    val showCategoryDialog: Flow<Boolean> = uiState.map { it.showCategoryDialog }.distinctUntilChanged()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val categories = categoryService.allCategoriesDescriptions
                _uiState.update {
                    it.copy(
                        categories = categories,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Erro ao carregar categorias: ${e.message}"
                    )
                }
            }
        }
    }

    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun toggleCategoryDialog(show: Boolean) {
        _uiState.update { it.copy(showCategoryDialog = show) }
    }

    fun toggleCategorySelection(category: String, isSelected: Boolean) {
        val currentSelected = _uiState.value.selectedCategories
        val newSelected = if (isSelected) {
            currentSelected + category
        } else {
            currentSelected - category
        }
        _uiState.update { it.copy(selectedCategories = newSelected) }
    }

    fun selectImage() {
        viewModelScope.launch(Dispatchers.IO) {
            imageService.selectImage().ifPresent { bytes ->
                _uiState.update { it.copy(selectedImageBytes = bytes) }
            }
        }
    }

    fun removeImage() {
        _uiState.update { it.copy(selectedImageBytes = null) }
    }

    fun createQuiz() {
        if (!validateForm()) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val newQuiz = quizService.createQuiz(
                    _uiState.value.title,
                    _uiState.value.description,
                    _uiState.value.selectedCategories,
                    _uiState.value.selectedImageBytes
                )
                _events.send(CreateQuizEvent.QuizCreated(newQuiz))
            } catch (e: Exception) {
                _events.send(CreateQuizEvent.ShowError("Erro ao criar quiz: ${e.message}"))
            }
        }
    }

    private fun validateForm(): Boolean {
        if (_uiState.value.title.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Título é obrigatório") }
            return false
        }
        if (_uiState.value.description.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Descrição é obrigatória") }
            return false
        }
        if (_uiState.value.selectedCategories.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Selecione pelo menos uma categoria") }
            return false
        }
        _uiState.update { it.copy(errorMessage = null) }
        return true
    }
    
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
