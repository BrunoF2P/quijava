package org.quijava.quijava.compose.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import org.quijava.quijava.compose.viewmodels.EditQuizEvent
import org.quijava.quijava.compose.viewmodels.EditQuizViewModel
import org.quijava.quijava.models.QuizModel
import org.jetbrains.skia.Image as SkiaImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditQuizScreen(
    quiz: QuizModel,
    viewModel: EditQuizViewModel,
    onQuizUpdated: (QuizModel) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    
    // Collect events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is EditQuizEvent.QuizUpdated -> onQuizUpdated(event.quiz)
                is EditQuizEvent.ShowError -> {
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
                title = { Text("Editar Quiz") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = state.title,
                        onValueChange = { viewModel.updateTitle(it) },
                        label = { Text("Título do Quiz") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = state.description,
                        onValueChange = { viewModel.updateDescription(it) },
                        label = { Text("Descrição") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3
                    )

                    // Categories Section
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Categorias", style = MaterialTheme.typography.titleMedium)
                                IconButton(onClick = { viewModel.toggleCategoryDialog(true) }) {
                                    Icon(Icons.Default.Add, contentDescription = "Adicionar Categoria")
                                }
                            }
                            
                            Spacer(Modifier.height(8.dp))
                            
                            if (state.selectedCategories.isEmpty()) {
                                Text(
                                    "Nenhuma categoria selecionada",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            } else {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    state.selectedCategories.forEach { category ->
                                        InputChip(
                                            selected = true,
                                            onClick = { viewModel.toggleCategorySelection(category, false) },
                                            label = { Text(category) },
                                            trailingIcon = {
                                                Icon(
                                                    Icons.Default.Close,
                                                    contentDescription = "Remover",
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { viewModel.selectImage() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors()
                    ) {
                        Icon(Icons.Default.Image, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Alterar Imagem (Opcional)")
                    }

                    // Image Preview
                    state.selectedImageBytes?.let { bytes ->
                        val imageBitmap = remember(bytes) {
                            try {
                                SkiaImage.makeFromEncoded(bytes).toComposeImageBitmap()
                            } catch (e: Exception) {
                                null
                            }
                        }

                        if (imageBitmap != null) {
                            Box(modifier = Modifier.fillMaxWidth()) {
                                Image(
                                    bitmap = imageBitmap,
                                    contentDescription = "Preview",
                                    modifier = Modifier
                                        .height(200.dp)
                                        .fillMaxWidth(),
                                    alignment = Alignment.Center
                                )
                                IconButton(
                                    onClick = { viewModel.removeImage() },
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Remover",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))

                    state.errorMessage?.let {
                        Text(
                            text = it,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Button(
                        onClick = { viewModel.updateQuiz(quiz) },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = true
                    ) {
                        Text("Atualizar Quiz")
                    }
                }
            }
            
            // Category Selection Dialog
            if (state.showCategoryDialog) {
                AlertDialog(
                    onDismissRequest = { viewModel.toggleCategoryDialog(false) },
                    title = { Text("Selecionar Categorias") },
                    text = {
                        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                            state.categories.forEach { category ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Checkbox(
                                        checked = state.selectedCategories.contains(category),
                                        onCheckedChange = { isChecked ->
                                            viewModel.toggleCategorySelection(category, isChecked)
                                        }
                                    )
                                    Text(
                                        text = category,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { viewModel.toggleCategoryDialog(false) }) {
                            Text("Concluir")
                        }
                    }
                )
            }
        }
    }
}
