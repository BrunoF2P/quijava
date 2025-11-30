package org.quijava.quijava.compose.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.skia.Image
import org.quijava.quijava.compose.viewmodels.PlayQuizEvent
import org.quijava.quijava.compose.viewmodels.PlayQuizViewModel
import org.quijava.quijava.compose.viewmodels.currentQuestion
import org.quijava.quijava.models.QuizModel
import org.quijava.quijava.models.TypeQuestion

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayQuizScreen(
    quiz: QuizModel,
    viewModel: PlayQuizViewModel,
    onBack: () -> Unit,
    onQuizComplete: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    // Collect events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is PlayQuizEvent.QuizCompleted -> {
                    // Already handled by callback in ViewModel for now
                }

                is PlayQuizEvent.ShowError -> {
                    // Error shown in UI via state
                }
            }
        }
    }

    LaunchedEffect(quiz) {
        viewModel.loadQuiz(quiz)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(quiz.title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "${state.score}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.errorMessage != null) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Erro: ${state.errorMessage}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onBack) {
                        Text("Voltar")
                    }
                }
            } else if (state.currentQuestion == null) {
                Text("Nenhuma questão disponível", modifier = Modifier.align(Alignment.Center))
            } else {
                state.currentQuestion?.let { currentQuestion ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Progress
                        Text(
                            "Pergunta ${state.currentQuestionIndex + 1}/${state.questions.size}",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary
                        )

                        // Timer and Progress Bar
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Timer, contentDescription = null)
                                    Text(
                                        String.format("%02d:%02d", state.timeRemaining / 60, state.timeRemaining % 60),
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(Modifier.height(8.dp))
                                LinearProgressIndicator(
                                    progress = {
                                        val totalTime = currentQuestion.limiteTime?.seconds?.toFloat() ?: 30f
                                        if (totalTime > 0) state.timeRemaining.toFloat() / totalTime else 0f
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                )
                            }
                        }

                        // Question Type
                        Text(
                            currentQuestion.typeQuestion?.name?.replace("_", " ") ?: "",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        // Image Display
                        currentQuestion.imageQuestion?.let { bytes ->
                            val imageBitmap = remember(currentQuestion) {
                                try {
                                    Image.makeFromEncoded(bytes).toComposeImageBitmap()
                                } catch (e: Exception) {
                                    null
                                }
                            }

                            if (imageBitmap != null) {
                                Card(modifier = Modifier.fillMaxWidth()) {
                                    Image(
                                        bitmap = imageBitmap,
                                        contentDescription = "Imagem da Pergunta",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(max = 300.dp)
                                            .padding(16.dp),
                                        alignment = Alignment.Center
                                    )
                                }
                            }
                        }

                        // Question Text
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                currentQuestion.questionText,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        // Answers
                        Text("Respostas:", style = MaterialTheme.typography.titleSmall)

                        state.shuffledOptions.forEachIndexed { index, option ->
                            val isSelected = state.selectedOptions.contains(index)
                            val isSingleChoice = currentQuestion.typeQuestion == TypeQuestion.SINGLE_CHOICE

                            Card(
                                onClick = {
                                    viewModel.toggleOptionSelection(index, isSingleChoice)
                                },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected)
                                        MaterialTheme.colorScheme.primaryContainer
                                    else
                                        MaterialTheme.colorScheme.surface
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isSingleChoice) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = null
                                        )
                                    } else {
                                        Checkbox(
                                            checked = isSelected,
                                            onCheckedChange = null
                                        )
                                    }
                                    Spacer(Modifier.width(8.dp))
                                    Text(option.optionText ?: "")
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Next Button
                        Button(
                            onClick = { viewModel.submitAnswer() },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = state.selectedOptions.isNotEmpty()
                        ) {
                            Text("Próxima")
                        }
                    }
                }


                // Result Dialog
                state.resultDialog?.let { dialog ->
                    AlertDialog(
                        onDismissRequest = {
                            viewModel.dismissDialog(onQuizComplete)
                        },
                        title = { Text(dialog.title) },
                        text = { Text(dialog.message) },
                        confirmButton = {
                            TextButton(onClick = {
                                viewModel.dismissDialog(onQuizComplete)
                            }) {
                                Text("OK")
                            }
                        }
                    )
                }
            }
        }
    }
}

