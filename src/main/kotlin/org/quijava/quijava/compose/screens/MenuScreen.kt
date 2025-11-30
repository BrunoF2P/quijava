package org.quijava.quijava.compose.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.quijava.quijava.services.MenuService

@Composable
fun MenuScreen(
    menuService: MenuService,
    onNavigateToCreateQuiz: () -> Unit,
    onNavigateToCreateCategory: () -> Unit,
    onNavigateToAllQuizzes: () -> Unit,
    onNavigateToMyQuizzes: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onLogout: () -> Unit
) {
    val userRole = remember { menuService.role.orElse(1).toString() }
    val isStudent = userRole == "1"
    val isProfessor = !isStudent

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header with gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary,
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                        )
                    )
                )
        ) {
            // Logout button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = {
                        menuService.logout()
                        onLogout()
                    },
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.White
                    ),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = Brush.linearGradient(listOf(Color.White, Color.White))
                    )
                ) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Sair")
                }
            }

            // Hero content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource("images/logo.png"),
                    contentDescription = "QuiJava Logo",
                    modifier = Modifier.size(100.dp)
                )

                Spacer(Modifier.height(24.dp))

                Text(
                    "Bem-vindo ao QuiJava",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )

                Text(
                    if (isProfessor) "Painel do Professor" else "Painel do Estudante",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White.copy(alpha = 0.9f)
                    ),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        // Action cards
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp, vertical = 32.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                "O que você gostaria de fazer?",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )

            // Grid of action cards
            if (isProfessor) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    ActionCard(
                        title = "Criar Quiz",
                        description = "Crie um novo quiz para seus alunos",
                        icon = Icons.Default.Add,
                        onClick = onNavigateToCreateQuiz,
                        modifier = Modifier.weight(1f)
                    )

                    ActionCard(
                        title = "Nova Categoria",
                        description = "Adicione uma nova categoria de quiz",
                        icon = Icons.Default.Category,
                        onClick = onNavigateToCreateCategory,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    ActionCard(
                        title = "Meus Quizzes",
                        description = "Gerencie seus quizzes criados",
                        icon = Icons.Default.Person,
                        onClick = onNavigateToMyQuizzes,
                        modifier = Modifier.weight(1f)
                    )

                    ActionCard(
                        title = "Todos os Quizzes",
                        description = "Explore todos os quizzes disponíveis",
                        icon = Icons.AutoMirrored.Filled.List,
                        onClick = onNavigateToAllQuizzes,
                        modifier = Modifier.weight(1f),
                        isPrimary = true
                    )
                }
            } else {
                // Student view
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    ActionCard(
                        title = "Explorar Quizzes",
                        description = "Descubra e jogue quizzes incríveis",
                        icon = Icons.AutoMirrored.Filled.List,
                        onClick = onNavigateToAllQuizzes,
                        modifier = Modifier.weight(1f),
                        isPrimary = true
                    )

                    ActionCard(
                        title = "Histórico",
                        description = "Veja seu desempenho passado",
                        icon = Icons.Default.History,
                        onClick = onNavigateToHistory,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    val elevation by animateDpAsState(
        targetValue = if (isHovered) 12.dp else 4.dp,
        label = "elevation"
    )

    Card(
        modifier = modifier
            .height(160.dp)
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(16.dp),
                spotColor = if (isPrimary) 
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                else 
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPrimary)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = if (isPrimary) Color.White else MaterialTheme.colorScheme.primary
            )

            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = if (isPrimary) Color.White else MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isPrimary) 
                        Color.White.copy(alpha = 0.9f) 
                    else 
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}
