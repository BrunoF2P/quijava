package org.quijava.quijava.compose.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.quijava.quijava.models.*
import org.quijava.quijava.services.ImageService
import org.quijava.quijava.services.QuestionService
import java.time.Duration

data class CreateQuestionUiState(
    val isLoading: Boolean = true,
    val questions: List<QuestionModel> = emptyList(),
    val form: QuestionFormState = QuestionFormState(),
    val errorMessage: String? = null
)

data class QuestionFormState(
    val questionText: String = "",
    val durationSeconds: String = "30",
    val score: String = "10",
    val difficulty: QuestionDifficulty = QuestionDifficulty.FACIL,
    val selectedImageBytes: ByteArray? = null,
    val options: List<OptionState> = listOf(OptionState(), OptionState()),
    val editingQuestion: QuestionModel? = null,
    val validationError: String? = null
)

data class OptionState(
    val id: Long = System.nanoTime(), 
    val text: String = "", 
    val isCorrect: Boolean = false
)

sealed class CreateQuestionEvent {
    object QuestionSaved : CreateQuestionEvent()
    data class ShowError(val message: String) : CreateQuestionEvent()
}

class CreateQuestionViewModel(
    private val questionService: QuestionService,
    private val imageService: ImageService
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateQuestionUiState())
    val uiState: StateFlow<CreateQuestionUiState> = _uiState.asStateFlow()

    private val _events = Channel<CreateQuestionEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    // Optimized flows
    val isLoading: Flow<Boolean> = uiState.map { it.isLoading }.distinctUntilChanged()
    val errorMessage: Flow<String?> = uiState.map { it.errorMessage }.distinctUntilChanged()

    private var currentQuiz: QuizModel? = null

    fun loadQuestions(quiz: QuizModel) {
        if (currentQuiz?.id == quiz.id && !_uiState.value.isLoading) return
        
        currentQuiz = quiz
        _uiState.update { it.copy(isLoading = true) }
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val questions = questionService.findQuestionsByQuiz(quiz.id)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        questions = questions
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Erro ao carregar questões: ${e.message}"
                    )
                }
            }
        }
    }

    fun updateForm(update: QuestionFormState.() -> QuestionFormState) {
        _uiState.update { it.copy(form = it.form.update()) }
    }

    fun addOption() {
        updateForm { copy(options = options + OptionState()) }
    }

    fun removeOption(index: Int) {
        if (_uiState.value.form.options.size > 2) {
            updateForm { 
                val newOptions = options.toMutableList().apply { removeAt(index) }
                copy(options = newOptions)
            }
        }
    }

    fun updateOption(index: Int, option: OptionState) {
        updateForm {
            val newOptions = options.toMutableList().apply { this[index] = option }
            copy(options = newOptions)
        }
    }

    fun selectImage() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val bytes = imageService.selectImage()
                if (bytes != null) {
                    _uiState.update {
                        it.copy(form = it.form.copy(selectedImageBytes = bytes))
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "Erro ao selecionar imagem: ${e.message}") }
            }
        }
    }
    
    fun removeImage() {
        updateForm { copy(selectedImageBytes = null) }
    }

    fun loadQuestionForEdit(question: QuestionModel) {
        val options = question.optionsAnswers?.map { 
            OptionState(text = it.optionText, isCorrect = it.isCorrect) 
        } ?: listOf(OptionState(), OptionState())

        _uiState.update {
            it.copy(
                form = QuestionFormState(
                    questionText = question.questionText,
                    durationSeconds = question.limiteTime?.seconds?.toString() ?: "30",
                    difficulty = question.questionDifficulty,
                    selectedImageBytes = question.imageQuestion,
                    options = options,
                    editingQuestion = question,
                    score = "10"
                )
            )
        }
    }

    fun saveQuestion() {
        if (!validateForm()) return

        val form = _uiState.value.form
        val quiz = currentQuiz ?: return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val type = if (form.options.count { it.isCorrect } > 1) TypeQuestion.Multipla_escolha else TypeQuestion.Escolha_unica
                
                val questionToSave = if (form.editingQuestion != null) {
                    form.editingQuestion.apply {
                        questionText = form.questionText
                        typeQuestion = type
                        limiteTime = Duration.ofSeconds(form.durationSeconds.toLongOrNull() ?: 30)
                        questionDifficulty = form.difficulty
                        imageQuestion = form.selectedImageBytes
                    }
                } else {
                    questionService.createQuestion(
                        form.questionText,
                        quiz,
                        type,
                        form.durationSeconds,
                        emptyList(),
                        form.difficulty,
                        form.selectedImageBytes
                    )
                }

                val optionsModels = form.options.map { 
                    OptionsAnswerModel().apply {
                        this.optionText = it.text
                        this.isCorrect = it.isCorrect
                        this.score = form.score.toIntOrNull() ?: 10
                        this.question = questionToSave
                    }
                }

                questionService.saveQuestion(questionToSave)
                questionService.saveOptionsAnswers(optionsModels)

                val questions = questionService.findQuestionsByQuiz(quiz.id)
                
                _uiState.update {
                    it.copy(
                        questions = questions,
                        form = QuestionFormState(),
                        errorMessage = null
                    )
                }
                
                // Emitting event instead of just state update if needed, 
                // but here we just refresh the list. 
                // If we wanted to close a dialog or show a toast, we'd use an event.
                // Let's emit QuestionSaved to signal success (maybe show a toast)
                _events.send(CreateQuestionEvent.QuestionSaved)

            } catch (e: Exception) {
                _events.send(CreateQuestionEvent.ShowError("Erro ao salvar: ${e.message}"))
                e.printStackTrace()
            }
        }
    }

    fun deleteQuestion(question: QuestionModel) {
        val quiz = currentQuiz ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                questionService.deleteQuestion(question.id)
                val questions = questionService.findQuestionsByQuiz(quiz.id)
                _uiState.update { it.copy(questions = questions) }
            } catch (e: Exception) {
                _events.send(CreateQuestionEvent.ShowError("Erro ao deletar: ${e.message}"))
            }
        }
    }

    private fun validateForm(): Boolean {
        val form = _uiState.value.form
        var error: String? = null

        if (form.questionText.isBlank()) {
            error = "Texto da pergunta é obrigatório"
        } else if (form.options.none { it.isCorrect }) {
            error = "Selecione pelo menos uma resposta correta"
        } else if (form.options.any { it.text.isBlank() }) {
            error = "Preencha todas as opções"
        }

        if (error != null) {
            _uiState.update {
                it.copy(form = it.form.copy(validationError = error))
            }
            return false
        }
        
        _uiState.update {
            it.copy(form = it.form.copy(validationError = null))
        }
        return true
    }
    
    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}
