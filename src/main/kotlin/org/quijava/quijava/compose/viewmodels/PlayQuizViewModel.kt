package org.quijava.quijava.compose.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import org.quijava.quijava.models.OptionsAnswerModel
import org.quijava.quijava.models.QuestionModel
import org.quijava.quijava.models.QuizModel
import org.quijava.quijava.services.QuizService
import org.quijava.quijava.services.RankService

data class PlayQuizUiState(
    val isLoading: Boolean = true,
    val questions: List<QuestionModel> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val shuffledOptions: List<OptionsAnswerModel> = emptyList(),
    val selectedOptions: Set<Int> = emptySet(),
    val timeRemaining: Long = 30,
    val score: Int = 0,
    val isQuizFinished: Boolean = false,
    val resultDialog: ResultDialogState? = null,
    val errorMessage: String? = null,
    val startTime: Long = System.currentTimeMillis()
)

data class ResultDialogState(
    val title: String,
    val message: String,
    val isCorrect: Boolean
)

val PlayQuizUiState.currentQuestion: QuestionModel?
    get() = questions.getOrNull(currentQuestionIndex)

sealed class PlayQuizEvent {
    object QuizCompleted : PlayQuizEvent()
    data class ShowError(val message: String) : PlayQuizEvent()
}

class PlayQuizViewModel(
    private val quizService: QuizService,
    private val rankService: RankService
) : ViewModel() {

    private val _uiState = MutableStateFlow(PlayQuizUiState())
    val uiState: StateFlow<PlayQuizUiState> = _uiState.asStateFlow()

    private val _events = Channel<PlayQuizEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    // Optimized flows
    val isLoading: Flow<Boolean> = uiState.map { it.isLoading }.distinctUntilChanged()
    val currentQuestionIndex: Flow<Int> = uiState.map { it.currentQuestionIndex }.distinctUntilChanged()
    val timeRemaining: Flow<Long> = uiState.map { it.timeRemaining }.distinctUntilChanged()

    private var timerJob: Job? = null
    private var currentQuiz: QuizModel? = null

    fun loadQuiz(quiz: QuizModel) {
        if (currentQuiz?.id == quiz.id && !_uiState.value.isLoading) return
        
        currentQuiz = quiz
        _uiState.update { it.copy(isLoading = true) }
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Fetch questions eagerly using service to avoid lazy loading exception
                val questions = quizService.getAllQuestionsByQuizId(quiz.id)
                
                // Shuffle options for first question
                val firstQuestionOptions = questions.firstOrNull()?.optionsAnswers?.shuffled() ?: emptyList()
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        questions = questions,
                        currentQuestionIndex = 0,
                        shuffledOptions = firstQuestionOptions,
                        score = 0,
                        isQuizFinished = false,
                        startTime = System.currentTimeMillis()
                    )
                }
                startTimer()
            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Erro ao carregar quiz: ${e.message}"
                    )
                }
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        val currentQuestion = _uiState.value.currentQuestion ?: return
        val duration = currentQuestion.limiteTime?.seconds ?: 30L

        _uiState.update { it.copy(timeRemaining = duration) }

        timerJob = viewModelScope.launch {
            while (_uiState.value.timeRemaining > 0) {
                delay(1000)
                _uiState.update { it.copy(timeRemaining = it.timeRemaining - 1) }
            }
            submitAnswer(timeOut = true)
        }
    }

    fun toggleOptionSelection(optionIndex: Int, isSingleChoice: Boolean) {
        _uiState.update { state ->
            val newSelection = if (isSingleChoice) {
                setOf(optionIndex)
            } else {
                if (state.selectedOptions.contains(optionIndex)) {
                    state.selectedOptions - optionIndex
                } else {
                    state.selectedOptions + optionIndex
                }
            }
            state.copy(selectedOptions = newSelection)
        }
    }

    fun submitAnswer(timeOut: Boolean = false) {
        timerJob?.cancel()
        
        val state = _uiState.value
        val question = state.currentQuestion ?: return
        val options = state.shuffledOptions
        
        val selectedIndices = state.selectedOptions
        val correctIndices = options.mapIndexedNotNull { index, option -> 
            if (option.isCorrect) index else null 
        }.toSet()

        val isCorrect = !timeOut && selectedIndices == correctIndices
        
        val points = if (isCorrect) {
            options.filterIndexed { index, _ -> selectedIndices.contains(index) }
                .sumOf { it.getScore() ?: 0 }
        } else 0

        val dialogState = ResultDialogState(
            title = if (timeOut) "Tempo Esgotado!" else if (isCorrect) "Correto!" else "Incorreto!",
            message = if (isCorrect) "Você ganhou $points pontos." else "A resposta correta era: ${options.filter { it.getIsCorrect() == true }.joinToString { it.getOptionText() ?: "" }}",
            isCorrect = isCorrect
        )

        _uiState.update {
            it.copy(
                score = it.score + points,
                resultDialog = dialogState
            )
        }
    }

    fun dismissDialog(onQuizComplete: () -> Unit) {
        _uiState.update { it.copy(resultDialog = null) }
        
        if (_uiState.value.currentQuestionIndex < _uiState.value.questions.size - 1) {
            val nextIndex = _uiState.value.currentQuestionIndex + 1
            val nextQuestion = _uiState.value.questions.getOrNull(nextIndex)
            val nextShuffledOptions = nextQuestion?.optionsAnswers?.shuffled() ?: emptyList()
            
            _uiState.update {
                it.copy(
                    currentQuestionIndex = nextIndex,
                    shuffledOptions = nextShuffledOptions,
                    selectedOptions = emptySet()
                )
            }
            startTimer()
        } else {
            finishQuiz(onQuizComplete)
        }
    }

    private fun finishQuiz(onQuizComplete: () -> Unit) {
        val quiz = currentQuiz ?: return
        val score = _uiState.value.score
        val startTime = _uiState.value.startTime
        val endTime = System.currentTimeMillis()
        val durationMillis = endTime - startTime
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Save rank
                rankService.saveRanking(quiz, score, java.time.Duration.ofMillis(durationMillis))
                
                // Increment quiz attempts
                quizService.countPlayQuiz(quiz)
                
                _uiState.update { it.copy(isQuizFinished = true) }
                
                // Emit event
                _events.send(PlayQuizEvent.QuizCompleted)
                
                // Call completion callback on Main thread
                withContext(Dispatchers.Main) {
                    onQuizComplete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _events.send(PlayQuizEvent.ShowError("Erro ao finalizar quiz: ${e.message}"))
            }
        }
    }
}
