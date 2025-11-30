package org.quijava.quijava.compose.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.quijava.quijava.services.CategoryService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateCategoryScreen(
    categoryService: CategoryService,
    onCategoryCreated: () -> Unit,
    onBack: () -> Unit
) {
    var categoryName by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nova Categoria") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .width(400.dp)
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Criar Categoria",
                        style = MaterialTheme.typography.headlineSmall
                    )

                    OutlinedTextField(
                        value = categoryName,
                        onValueChange = { 
                            categoryName = it
                            errorMessage = null
                            successMessage = null
                        },
                        label = { Text("Nome da Categoria") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    if (successMessage != null) {
                        Text(
                            text = successMessage!!,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Button(
                        onClick = {
                            if (categoryName.isBlank()) {
                                errorMessage = "O nome da categoria é obrigatório"
                                return@Button
                            }

                            isLoading = true
                            scope.launch(Dispatchers.IO) {
                                try {
                                    categoryService.createCategory(categoryName)
                                    successMessage = "Categoria criada com sucesso!"
                                    categoryName = ""
                                } catch (e: Exception) {
                                    errorMessage = "Erro ao criar categoria: ${e.message}"
                                } finally {
                                    isLoading = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        } else {
                            Text("Criar")
                        }
                    }
                }
            }
        }
    }
}
