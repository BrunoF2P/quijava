package org.quijava.quijava.compose.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.quijava.quijava.services.MenuService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    menuService: MenuService,
    onNavigateToCreateQuiz: () -> Unit,
    onNavigateToCreateCategory: () -> Unit,
    onNavigateToAllQuizzes: () -> Unit,
    onNavigateToMyQuizzes: () -> Unit,
    onLogout: () -> Unit
) {
    val userRole = remember { menuService.role.toString() }
    val isStudent = userRole == "1"
    val isProfessor = !isStudent

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("QuiJava") },
                actions = {
                    Button(
                        onClick = {
                            menuService.logout()
                            onLogout()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Sair")
                    }
                    Spacer(Modifier.width(16.dp))
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.Quiz,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(16.dp))

            Text(
                "Menu Principal",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.height(48.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Botões de Ação para Professores
                if (isProfessor) {
                    Button(
                        onClick = onNavigateToCreateQuiz,
                        modifier = Modifier
                            .height(60.dp)
                            .width(200.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Cadastrar quiz")
                    }

                    Button(
                        onClick = onNavigateToCreateCategory,
                        modifier = Modifier
                            .height(60.dp)
                            .width(200.dp)
                    ) {
                        Icon(Icons.Default.Category, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Nova Categoria")
                    }

                    Button(
                        onClick = onNavigateToMyQuizzes,
                        modifier = Modifier
                            .height(60.dp)
                            .width(200.dp)
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Meus quizzes")
                    }
                }

                // Botão "Todos os quizzes" - para todos
                Button(
                    onClick = onNavigateToAllQuizzes,
                    modifier = Modifier
                        .height(60.dp)
                        .width(200.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.List, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Todos os quizzes")
                }
            }
        }
    }
}
