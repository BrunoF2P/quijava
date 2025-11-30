package org.quijava.quijava.compose.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import org.quijava.quijava.compose.viewmodels.CreateQuizEvent
import org.quijava.quijava.compose.viewmodels.CreateQuizViewModel
import org.quijava.quijava.models.QuizModel
import org.jetbrains.skia.Image as SkiaImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateQuizScreen(
    viewModel: CreateQuizViewModel,
    onQuizCreated: (QuizModel) -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    
    // Collect events
    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CreateQuizEvent.QuizCreated -> onQuizCreated(event.quiz)
                is CreateQuizEvent.ShowError -> {
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Criar Quiz") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(32.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    Icons.Default.Quiz,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )

                Text(
                    "Novo Quiz",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = state.title,
                    onValueChange = { viewModel.updateTitle(it) },
                    label = { Text("Título") },
                    leadingIcon = { Icon(Icons.Default.Title, contentDescription = null) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(0.7f)
                )

                OutlinedTextField(
                    value = state.description,
                    onValueChange = { viewModel.updateDescription(it) },
                    label = { Text("Descrição") },
                    leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                    minLines = 4,
                    maxLines = 6,
                    modifier = Modifier.fillMaxWidth(0.7f)
                )

                // Seleção de categorias
                Card(
                    modifier = Modifier.fillMaxWidth(0.7f),
                    onClick = { viewModel.toggleCategoryDialog(true) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "Categorias",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                if (state.selectedCategories.isEmpty()) "Nenhuma selecionada"
                                else "${state.selectedCategories.size} selecionada(s)",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }

                // Image Selection
                Button(
                    onClick = { viewModel.selectImage() },
                    modifier = Modifier.fillMaxWidth(0.7f),
                    colors = ButtonDefaults.outlinedButtonColors()
                ) {
                    Icon(Icons.Default.Image, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Selecionar Imagem (Opcional)")
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
                        Box(modifier = Modifier.fillMaxWidth(0.7f)) {
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
                                Icon(Icons.Default.Close, contentDescription = "Remover", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }

                state.errorMessage?.let {
                    Text(
                        it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(Modifier.weight(1f))

                Button(
                    onClick = { viewModel.createQuiz() },
                    modifier = Modifier.fillMaxWidth(0.7f).height(50.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Criar Quiz")
                }
            }
        }
    }

    // Dialog de seleção de categorias
    if (state.showCategoryDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.toggleCategoryDialog(false) },
            title = { Text("Selecionar Categorias") },
            text = {
                LazyColumn {
                    items(state.categories) { category ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = state.selectedCategories.contains(category),
                                onCheckedChange = { checked ->
                                    viewModel.toggleCategorySelection(category, checked)
                                }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(category)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.toggleCategoryDialog(false) }) {
                    Text("OK")
                }
            }
        )
    }
}
