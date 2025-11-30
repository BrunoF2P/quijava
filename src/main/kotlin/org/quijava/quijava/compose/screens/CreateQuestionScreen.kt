package org.quijava.quijava.compose.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.jetbrains.skia.Image
import org.quijava.quijava.compose.components.PrimaryButton
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

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            // Background Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                            )
                        )
                    )
            )

            // Top Bar Area
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = Color.White
                    )
                }
                
                PrimaryButton(
                    text = "Concluir Quiz",
                    onClick = onFinish,
                    icon = { Icon(Icons.Default.Check, contentDescription = null, tint = Color.White) }
                )
            }

            // Main Content - Split Layout
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 80.dp, start = 24.dp, end = 24.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Left Side: Question Form
                Card(
                    modifier = Modifier
                        .weight(0.6f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    QuestionForm(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
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
                }

                // Right Side: Question List
                Card(
                    modifier = Modifier
                        .weight(0.4f)
                        .fillMaxHeight(),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    QuestionList(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        questions = state.questions,
                        isLoading = state.isLoading,
                        onEdit = { viewModel.loadQuestionForEdit(it) },
                        onDelete = { viewModel.deleteQuestion(it) }
                    )
                }
            }
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.QuestionMark,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            Text(
                if (formState.editingQuestion != null) "Editar Pergunta" else "Nova Pergunta",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
        }

        OutlinedTextField(
            value = formState.questionText,
            onValueChange = { text -> onFormUpdate { copy(questionText = text) } },
            label = { Text("Texto da Pergunta") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            shape = RoundedCornerShape(12.dp)
        )

        // Image Selection
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = onImageSelect,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.outlinedButtonColors(),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Default.Image, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Adicionar Imagem (Opcional)")
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        androidx.compose.foundation.Image(
                            bitmap = imageBitmap,
                            contentDescription = "Preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = onImageRemove,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Remover", tint = Color.White)
                        }
                    }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            OutlinedTextField(
                value = formState.durationSeconds,
                onValueChange = { text -> 
                    if (text.all { char -> char.isDigit() }) onFormUpdate { copy(durationSeconds = text) } 
                },
                label = { Text("Duração (s)") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Timer, contentDescription = null) }
            )
            OutlinedTextField(
                value = formState.score,
                onValueChange = { text -> 
                    if (text.all { char -> char.isDigit() }) onFormUpdate { copy(score = text) } 
                },
                label = { Text("Pontos") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = { Icon(Icons.Default.Star, contentDescription = null) }
            )
        }

        // Difficulty
        Column {
            Text("Dificuldade", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                QuestionDifficulty.entries.forEach { diff ->
                    FilterChip(
                        selected = formState.difficulty == diff,
                        onClick = { onFormUpdate { copy(difficulty = diff) } },
                        label = { Text(diff.name) },
                        leadingIcon = if (formState.difficulty == diff) {
                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        } else null
                    )
                }
            }
        }

        HorizontalDivider()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Alternativas", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            TextButton(onClick = onAddOption) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text("Adicionar")
            }
        }
        
        formState.options.forEachIndexed { index, option ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (option.isCorrect) 
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    else MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp, 
                    if (option.isCorrect) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    RadioButton(
                        selected = option.isCorrect,
                        onClick = { onUpdateOption(index, option.copy(isCorrect = !option.isCorrect)) }
                    )
                    
                    OutlinedTextField(
                        value = option.text,
                        onValueChange = { text ->
                            onUpdateOption(index, option.copy(text = text))
                        },
                        placeholder = { Text("Opção ${index + 1}") },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                    
                    IconButton(
                        onClick = { onRemoveOption(index) },
                        enabled = formState.options.size > 2
                    ) {
                        Icon(
                            Icons.Default.Delete, 
                            contentDescription = "Remover",
                            tint = if (formState.options.size > 2) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        }

        // Global Error Message
        errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }
        
        // Validation Error
        formState.validationError?.let {
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(16.dp))

        // Show cancel button when editing
        if (formState.editingQuestion != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { onFormUpdate { QuestionFormState() } },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Cancelar")
                }
                
                PrimaryButton(
                    text = "Atualizar Pergunta",
                    onClick = onSave,
                    modifier = Modifier.weight(1f),
                    icon = { Icon(Icons.Default.Save, contentDescription = null, tint = Color.White) }
                )
            }
        } else {
            PrimaryButton(
                text = "Adicionar Pergunta",
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                icon = { Icon(Icons.Default.Save, contentDescription = null, tint = Color.White) }
            )
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(Icons.Default.List, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text(
                "Questões (${questions.size})",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
        
        Spacer(Modifier.height(16.dp))
        
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (questions.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.PostAdd,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Nenhuma questão adicionada",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(questions) { question ->
                    Card(
                        onClick = { onEdit(question) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                question.questionText,
                                maxLines = 2,
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = when(question.questionDifficulty) {
                                        QuestionDifficulty.EASY -> Color(0xFF4CAF50)
                                        QuestionDifficulty.MEDIUM -> Color(0xFFFF9800)
                                        QuestionDifficulty.HARD -> Color(0xFFF44336)
                                    }.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        question.questionDifficulty.name,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = when(question.questionDifficulty) {
                                            QuestionDifficulty.EASY -> Color(0xFF2E7D32)
                                            QuestionDifficulty.MEDIUM -> Color(0xFFEF6C00)
                                            QuestionDifficulty.HARD -> Color(0xFFC62828)
                                        },
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                
                                IconButton(
                                    onClick = { onDelete(question) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Delete,
                                        contentDescription = "Deletar",
                                        tint = MaterialTheme.colorScheme.error,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
