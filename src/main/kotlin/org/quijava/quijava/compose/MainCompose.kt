package org.quijava.quijava.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.quijava.quijava.SpringRunApp
import org.quijava.quijava.compose.di.authModule
import org.quijava.quijava.compose.di.questionModule
import org.quijava.quijava.compose.di.quizModule
import org.quijava.quijava.compose.navigation.Navigation
import org.quijava.quijava.compose.theme.QuiJavaTheme
import org.springframework.boot.WebApplicationType
import org.springframework.boot.builder.SpringApplicationBuilder

fun main() = application {
    val springContext = SpringApplicationBuilder(SpringRunApp::class.java)
        .headless(false)
        .web(WebApplicationType.NONE)
        .run()

    startKoin {
        modules(
            authModule(springContext),
            quizModule(springContext),
            questionModule(springContext)
        )
    }

    Window(
        onCloseRequest = {
            cleanup(springContext)
            exitApplication()
        },
        title = "QuiJava",
        icon = painterResource("images/logo.png"),
        state = rememberWindowState(width = 1200.dp, height = 800.dp)
    ) {
        QuiJavaTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Navigation(springContext)
            }
        }
    }
}

private fun cleanup(springContext: org.springframework.context.ConfigurableApplicationContext) {
    try {
        stopKoin()
        springContext.close()
    } catch (e: Exception) {
        println("Erro ao fechar contextos: ${e.message}")
    }
}
