package org.quijava.quijava.compose.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import org.quijava.quijava.compose.components.PrimaryButton
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

            // Back Button
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .padding(16.dp)
                    .align(Alignment.TopStart)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White
                )
            }

            // Main Content Card
            Card(
                modifier = Modifier
                    .width(600.dp)
                    .align(Alignment.Center)
                    .padding(vertical = 32.dp)
                    .fillMaxHeight(0.9f),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                if (state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // Header Icon
                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = MaterialTheme.colorScheme.primaryContainer,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Quiz,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Text(
                            "Criar Novo Quiz",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        // Form Fields
                        OutlinedTextField(
                            value = state.title,
                            onValueChange = { viewModel.updateTitle(it) },
                            label = { Text("Título do Quiz") },
                            leadingIcon = { Icon(Icons.Default.Title, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp)
                        )

                        OutlinedTextField(
                            value = state.description,
                            onValueChange = { viewModel.updateDescription(it) },
                            label = { Text("Descrição") },
                            leadingIcon = { Icon(Icons.Default.Description, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            maxLines = 5,
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Category Selection
                        OutlinedCard(
                            onClick = { viewModel.toggleCategoryDialog(true) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.Category,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            "Categorias",
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        if (state.selectedCategories.isNotEmpty()) {
                                            Text(
                                                "${state.selectedCategories.size} selecionada(s)",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                                Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Image Selection
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.selectImage() },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.Image, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Selecionar Imagem de Capa")
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
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        Image(
                                            bitmap = imageBitmap,
                                            contentDescription = "Preview",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                        IconButton(
                                            onClick = { viewModel.removeImage() },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(8.dp)
                                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                        ) {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Remover",
                                                tint = Color.White
                                            )
                                        }
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

                        PrimaryButton(
                            text = "Criar Quiz",
                            onClick = { viewModel.createQuiz() },
                            modifier = Modifier.fillMaxWidth(),
                            icon = { Icon(Icons.Default.Check, contentDescription = null, tint = Color.White) }
                        )
                    }
                }
            }
        }
    }

    // Category Selection Dialog
    if (state.showCategoryDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.toggleCategoryDialog(false) },
            icon = { Icon(Icons.Default.Category, contentDescription = null) },
            title = { Text("Selecionar Categorias") },
            text = {
                Column(
                    modifier = Modifier
                        .heightIn(max = 400.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    state.categories.forEach { category ->
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
                            Text(
                                text = category,
                                style = MaterialTheme.typography.bodyLarge,
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
