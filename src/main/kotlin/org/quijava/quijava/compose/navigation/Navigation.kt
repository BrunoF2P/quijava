package org.quijava.quijava.compose.navigation

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.compose.koinInject
import org.quijava.quijava.compose.screens.*
import org.quijava.quijava.models.CategoryModel
import org.quijava.quijava.models.QuizModel
import org.quijava.quijava.services.*
import org.springframework.context.ConfigurableApplicationContext

enum class Screen {
    Login, Register, Menu, CreateQuiz, Categories, ListQuizzes, PlayQuiz, MyQuizzes, DetailsQuiz, EditQuiz, CreateQuestion, CreateCategory, History
}

@Composable
fun Navigation(context: ConfigurableApplicationContext) {
    val navController = rememberNavController()
    var selectedCategory by remember { mutableStateOf<CategoryModel?>(null) }
    var selectedQuiz by remember { mutableStateOf<QuizModel?>(null) }
    
    NavHost(
        navController = navController,
        startDestination = Screen.Login.name
    ) {
        composable(Screen.Login.name) {
            LoginScreen(
                viewModel = koinInject(),
                onLoginSuccess = { navController.navigate(Screen.Menu.name) },
                onNavigateToRegister = { navController.navigate(Screen.Register.name) }
            )
        }
        
        composable(Screen.Register.name) {
            RegisterScreen(
                viewModel = koinInject(),
                onRegisterSuccess = { navController.navigate(Screen.Menu.name) },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }
        
        composable(Screen.Menu.name) {
            val menuService = context.getBean(MenuService::class.java)
            MenuScreen(
                menuService = menuService,
                onNavigateToCreateQuiz = { navController.navigate(Screen.CreateQuiz.name) },
                onNavigateToCreateCategory = { navController.navigate(Screen.CreateCategory.name) },
                onNavigateToAllQuizzes = { navController.navigate(Screen.Categories.name) },
                onNavigateToMyQuizzes = { navController.navigate(Screen.MyQuizzes.name) },
                onNavigateToHistory = { navController.navigate(Screen.History.name) },
                onLogout = { 
                    navController.navigate(Screen.Login.name) {
                        popUpTo(Screen.Menu.name) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Categories.name) {
            val categoryService = context.getBean(CategoryService::class.java)
            CategoriesScreen(
                categoryService = categoryService,
                onCategoryClick = { category ->
                    selectedCategory = category
                    navController.navigate(Screen.ListQuizzes.name)
                },
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.CreateQuiz.name) {
            CreateQuizScreen(
                viewModel = koinInject(),
                onQuizCreated = { newQuiz -> 
                    selectedQuiz = newQuiz
                    navController.navigate(Screen.CreateQuestion.name) 
                },
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.ListQuizzes.name) {
            selectedCategory?.let { category ->
                val quizService = context.getBean(QuizService::class.java)
                ListQuizzesScreen(
                    category = category,
                    quizService = quizService,
                    onQuizPlay = { quiz ->
                        selectedQuiz = quiz
                        navController.navigate(Screen.PlayQuiz.name) {
                            popUpTo(Screen.Menu.name) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onQuizDetails = { quiz ->
                        selectedQuiz = quiz
                        navController.navigate(Screen.DetailsQuiz.name)
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
        
        composable(Screen.MyQuizzes.name) {
            val quizService = context.getBean(QuizService::class.java)
            val sessionPreferencesService = context.getBean(SessionPreferencesService::class.java)
            MyQuizzesScreen(
                quizService = quizService,
                sessionPreferencesService = sessionPreferencesService,
                onEditQuiz = { quiz ->
                    selectedQuiz = quiz
                    navController.navigate(Screen.EditQuiz.name)
                },
                onNavigateToCreateQuiz = {
                    navController.navigate(Screen.CreateQuiz.name)
                },
                onBack = { navController.popBackStack() }
            )
        }
        
        composable(Screen.DetailsQuiz.name) {
            selectedQuiz?.let { quiz ->
                val rankService = context.getBean(RankService::class.java)
                DetailsQuizScreen(
                    quiz = quiz,
                    rankService = rankService,
                    onPlay = {
                        navController.navigate(Screen.PlayQuiz.name) {
                            popUpTo(Screen.Menu.name) { inclusive = false }
                            launchSingleTop = true
                        }
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
        
        composable(Screen.PlayQuiz.name) {
            selectedQuiz?.let { quiz ->
                PlayQuizScreen(
                    quiz = quiz,
                    viewModel = koinInject(),
                    onBack = {
                        navController.popBackStack()
                    },
                    onQuizComplete = {
                        navController.navigate(Screen.DetailsQuiz.name) {
                            popUpTo(Screen.PlayQuiz.name) { inclusive = true }
                        }
                    }
                )
            }
        }
        
        composable(Screen.CreateQuestion.name) {
            selectedQuiz?.let { quiz ->
                CreateQuestionScreen(
                    quiz = quiz,
                    viewModel = koinInject(),
                    onFinish = {
                        navController.navigate(Screen.MyQuizzes.name) {
                            popUpTo(Screen.Menu.name)
                        }
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
        
        composable(Screen.EditQuiz.name) {
            selectedQuiz?.let { quiz ->
                EditQuizScreen(
                    quiz = quiz,
                    viewModel = koinInject(),
                    onQuizUpdated = { updatedQuiz ->
                        selectedQuiz = updatedQuiz
                        navController.navigate(Screen.CreateQuestion.name)
                    },
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }
        }
        
        composable(Screen.CreateCategory.name) {
            val categoryService = context.getBean(CategoryService::class.java)
            CreateCategoryScreen(
                categoryService = categoryService,
                onCategoryCreated = {
                    navController.popBackStack()
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.History.name) {
            HistoryScreen(
                viewModel = koinInject(),
                onBack = { navController.popBackStack() }
            )
        }
    }
}
