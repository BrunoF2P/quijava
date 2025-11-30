package org.quijava.quijava.compose.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import org.jetbrains.skia.Image
import org.quijava.quijava.compose.viewmodels.CreateQuestionEvent
import org.quijava.quijava.compose.viewmodels.CreateQuestionViewModel
import org.quijava.quijava.compose.viewmodels.QuestionFormState
import org.quijava.quijava.models.QuestionDifficulty
import org.quijava.quijava.models.QuestionModel
import org.quijava.quijava.models.QuizModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQuestionScreen(
    quiz: QuizModel,
    viewModel: CreateQuestionViewModel,
    onFinish: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    
    // Collect events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CreateQuestionEvent.QuestionSaved -> {
                    // Maybe show a snackbar or toast
                }
                is CreateQuestionEvent.ShowError -> {
                    // Error shown in UI via state or snackbar
                }
            }
        }
    }

    LaunchedEffect(quiz) {
        viewModel.loadQuestions(quiz)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Adicionar Questões: ${quiz.title}") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    Button(onClick = onFinish) {
                        Text("Concluir")
                    }
                }
            )
        }
    ) { paddingValues ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            QuestionForm(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()
                    .padding(16.dp),
                formState = state.form,
                errorMessage = state.errorMessage,
                onSave = { viewModel.saveQuestion() },
                onImageSelect = { viewModel.selectImage() },
                onImageRemove = { viewModel.removeImage() },
                onFormUpdate = { update -> viewModel.updateForm(update) },
                onAddOption = { viewModel.addOption() },
                onRemoveOption = { viewModel.removeOption(it) },
                onUpdateOption = { index, option -> viewModel.updateOption(index, option) }
            )

            VerticalDivider()

            // Right Side: List of Questions (40%)
            QuestionList(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
                    .padding(16.dp),
                questions = state.questions,
                isLoading = state.isLoading,
                onEdit = { viewModel.loadQuestionForEdit(it) },
                onDelete = { viewModel.deleteQuestion(it) }
            )
        }
    }
}

@Composable
fun QuestionForm(
    modifier: Modifier = Modifier,
    formState: QuestionFormState,
    errorMessage: String?,
    onSave: () -> Unit,
    onImageSelect: () -> Unit,
    onImageRemove: () -> Unit,
    onFormUpdate: (QuestionFormState.() -> QuestionFormState) -> Unit,
    onAddOption: () -> Unit,
    onRemoveOption: (Int) -> Unit,
    onUpdateOption: (Int, org.quijava.quijava.compose.viewmodels.OptionState) -> Unit
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Nova Pergunta", style = MaterialTheme.typography.titleLarge)

        OutlinedTextField(
            value = formState.questionText,
            onValueChange = { text -> onFormUpdate { copy(questionText = text) } },
            label = { Text("Texto da Pergunta") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )

        // Image Selection
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = onImageSelect) {
                Icon(Icons.Default.Image, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Selecionar Imagem")
            }
            
            if (formState.selectedImageBytes != null) {
                Text("Imagem selecionada", color = MaterialTheme.colorScheme.primary)
                IconButton(onClick = onImageRemove) {
                    Icon(Icons.Default.Close, contentDescription = "Remover imagem")
                }
            }
        }
        
        // Image Preview
        formState.selectedImageBytes?.let { bytes ->
            val imageBitmap = remember(bytes) {
                try {
                    Image.makeFromEncoded(bytes).toComposeImageBitmap()
                } catch (e: Exception) {
                    null
                }
            }

            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = "Preview",
                    modifier = Modifier
                        .height(150.dp)
                        .fillMaxWidth(),
                    alignment = Alignment.Center
                )
            } else {
                Text("Erro ao carregar preview da imagem", color = MaterialTheme.colorScheme.error)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = formState.durationSeconds,
                onValueChange = { text -> 
                    if (text.all { char -> char.isDigit() }) onFormUpdate { copy(durationSeconds = text) } 
                },
                label = { Text("Duração (s)") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = formState.score,
                onValueChange = { text -> 
                    if (text.all { char -> char.isDigit() }) onFormUpdate { copy(score = text) } 
                },
                label = { Text("Pontos") },
                modifier = Modifier.weight(1f)
            )
        }

        // Difficulty
        Column {
            Text("Dificuldade", style = MaterialTheme.typography.labelMedium)
            Row {
                QuestionDifficulty.entries.forEach { diff ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = formState.difficulty == diff,
                            onClick = { onFormUpdate { copy(difficulty = diff) } }
                        )
                        Text(diff.name)
                        Spacer(Modifier.width(8.dp))
                    }
                }
            }
        }

        HorizontalDivider()

        Text("Alternativas", style = MaterialTheme.typography.titleMedium)
        
        formState.options.forEachIndexed { index, option ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = option.isCorrect,
                    onCheckedChange = { isChecked ->
                        onUpdateOption(index, option.copy(isCorrect = isChecked))
                    }
                )
                OutlinedTextField(
                    value = option.text,
                    onValueChange = { text ->
                        onUpdateOption(index, option.copy(text = text))
                    },
                    label = { Text("Opção ${index + 1}") },
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { onRemoveOption(index) },
                    enabled = formState.options.size > 2
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Remover")
                }
            }
        }

        Button(
            onClick = onAddOption,
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Text("Adicionar Opção")
        }

        // Global Error Message
        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }
        
        // Validation Error
        formState.validationError?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(if (formState.editingQuestion != null) "Atualizar Pergunta" else "Adicionar Pergunta")
        }
    }
}

@Composable
fun QuestionList(
    modifier: Modifier = Modifier,
    questions: List<QuestionModel>,
    isLoading: Boolean,
    onEdit: (QuestionModel) -> Unit,
    onDelete: (QuestionModel) -> Unit
) {
    Column(modifier = modifier) {
        Text("Questões Adicionadas", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(questions) { question ->
                    Card(
                        onClick = { onEdit(question) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                question.questionText,
                                maxLines = 2,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    question.questionDifficulty.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                IconButton(
                                    onClick = { onDelete(question) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Deletar", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
