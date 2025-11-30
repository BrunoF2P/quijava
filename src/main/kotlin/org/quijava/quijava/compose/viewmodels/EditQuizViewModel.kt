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

data class EditQuizUiState(
    val title: String = "",
    val description: String = "",
    val categories: List<String> = emptyList(),
    val selectedCategories: Set<String> = emptySet(),
    val selectedImageBytes: ByteArray? = null,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val showCategoryDialog: Boolean = false
)

sealed class EditQuizEvent {
    data class QuizUpdated(val quiz: QuizModel) : EditQuizEvent()
    data class ShowError(val message: String) : EditQuizEvent()
}

class EditQuizViewModel(
    private val quizService: QuizService,
    private val categoryService: CategoryService,
    private val imageService: ImageService
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditQuizUiState())
    val uiState: StateFlow<EditQuizUiState> = _uiState.asStateFlow()

    private val _events = Channel<EditQuizEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()
    
    // Optimized flows
    val isLoading: Flow<Boolean> = uiState.map { it.isLoading }.distinctUntilChanged()
    val errorMessage: Flow<String?> = uiState.map { it.errorMessage }.distinctUntilChanged()

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

    fun loadQuiz(quiz: QuizModel) {
        _uiState.update {
            it.copy(
                title = quiz.title,
                description = quiz.description,
                selectedCategories = quiz.categories.map { cat -> cat.description }.toSet(),
                selectedImageBytes = quiz.imageQuiz
            )
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

    fun updateQuiz(originalQuiz: QuizModel) {
        if (!validateForm()) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                quizService.updateQuiz(
                    originalQuiz.id,
                    _uiState.value.title,
                    _uiState.value.description,
                    _uiState.value.selectedCategories,
                    _uiState.value.selectedImageBytes
                )
                
                // Update local object to reflect changes immediately in UI if needed
                originalQuiz.title = _uiState.value.title
                originalQuiz.description = _uiState.value.description
                originalQuiz.imageQuiz = _uiState.value.selectedImageBytes
                // Categories update is complex locally without fetching, but service handles DB
                
                _events.send(EditQuizEvent.QuizUpdated(originalQuiz))
            } catch (e: Exception) {
                _events.send(EditQuizEvent.ShowError("Erro ao atualizar quiz: ${e.message}"))
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
