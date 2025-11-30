package org.quijava.quijava.compose.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.quijava.quijava.compose.components.QuizCard
import org.quijava.quijava.models.QuizModel
import org.quijava.quijava.services.QuizService
import org.quijava.quijava.services.SessionPreferencesService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyQuizzesScreen(
    sessionPreferencesService: SessionPreferencesService,
    quizService: QuizService,
    onEditQuiz: (QuizModel) -> Unit,
    onNavigateToCreateQuiz: () -> Unit,
    onBack: () -> Unit
) {
    var quizzes by remember { mutableStateOf<List<QuizModel>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showDeleteDialog by remember { mutableStateOf<QuizModel?>(null) }

    fun loadQuizzes() {
        isLoading = true
        // Using IO dispatcher for database operations
        kotlinx.coroutines.GlobalScope.launch(Dispatchers.IO) {
            try {
                val userId = sessionPreferencesService.userId.orElse(null)
                if (userId != null) {
                    quizzes = quizService.findAllQuizzesByUserId(userId)
                }
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        loadQuizzes()
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreateQuiz,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Criar Quiz")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.secondary,
                                MaterialTheme.colorScheme.secondary.copy(alpha = 0.8f)
                            )
                        )
                    )
            ) {
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

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Meus Quizzes",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        "Gerencie seus quizzes criados",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    )
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (quizzes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "Você ainda não criou nenhum quiz",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(onClick = onNavigateToCreateQuiz) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Criar meu primeiro quiz")
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 350.dp),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 80.dp // Space for FAB
                    ),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(quizzes) { quiz ->
                        Box {
                            QuizCard(
                                quiz = quiz,
                                onClick = { onEditQuiz(quiz) },
                                showPlayButton = false
                            )
                            
                            // Edit/Delete actions overlay
                            Row(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                    shadowElevation = 4.dp,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    IconButton(onClick = { onEditQuiz(quiz) }) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Editar",
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                
                                Surface(
                                    shape = CircleShape,
                                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                    shadowElevation = 4.dp,
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    IconButton(onClick = { showDeleteDialog = quiz }) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Excluir",
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

    // Delete Confirmation Dialog
    showDeleteDialog?.let { quiz ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Excluir Quiz") },
            text = { Text("Tem certeza que deseja excluir o quiz '${quiz.title}'? Esta ação não pode ser desfeita.") },
            confirmButton = {
                Button(
                    onClick = {
                        kotlinx.coroutines.GlobalScope.launch(Dispatchers.IO) {
                            try {
                                quizService.deleteQuiz(quiz.id)
                                showDeleteDialog = null
                                loadQuizzes()
                            } catch (e: Exception) {
                                e.printStackTrace()
                                showDeleteDialog = null
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Excluir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
