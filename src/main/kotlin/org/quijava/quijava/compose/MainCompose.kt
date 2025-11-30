package org.quijava.quijava.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.koin.core.context.startKoin
import org.quijava.quijava.SpringRunApp
import org.quijava.quijava.compose.di.authModule
import org.quijava.quijava.compose.di.questionModule
import org.quijava.quijava.compose.di.quizModule
import org.quijava.quijava.compose.navigation.Navigation
import org.quijava.quijava.compose.theme.QuiJavaTheme
import org.springframework.boot.builder.SpringApplicationBuilder

fun main() = application {
    // Inicia Spring Boot
    val springContext = SpringApplicationBuilder(SpringRunApp::class.java)
        .headless(false)
        .run()
    
    // Inicia Koin com módulos separados por feature
    startKoin {
        modules(
            authModule(springContext),
            quizModule(springContext),
            questionModule(springContext)
        )
    }
    
    Window(
        onCloseRequest = {
            springContext.close()
            exitApplication()
        },
        title = "QuiJava",
        state = rememberWindowState(width = 1200.dp, height = 800.dp)
    ) {
        QuiJavaTheme {
            androidx.compose.material3.Surface(
                modifier = Modifier.fillMaxSize(),
                color = androidx.compose.material3.MaterialTheme.colorScheme.background
            ) {
                Navigation(springContext)
            }
        }
    }
}
