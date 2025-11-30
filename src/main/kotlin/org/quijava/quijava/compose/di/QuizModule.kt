package org.quijava.quijava.compose.di

import org.koin.dsl.module
import org.quijava.quijava.compose.viewmodels.CreateQuizViewModel
import org.quijava.quijava.compose.viewmodels.EditQuizViewModel
import org.quijava.quijava.compose.viewmodels.PlayQuizViewModel
import org.quijava.quijava.services.CategoryService
import org.quijava.quijava.services.QuizService
import org.quijava.quijava.services.RankService
import org.springframework.context.ConfigurableApplicationContext

fun quizModule(springContext: ConfigurableApplicationContext) = module {
    // Services
    single { springContext.getBean(QuizService::class.java) }
    single { springContext.getBean(CategoryService::class.java) }
    single { springContext.getBean(RankService::class.java) }
    
    // ViewModels
    factory { CreateQuizViewModel(get(), get(), get()) }
    factory { EditQuizViewModel(get(), get(), get()) }
    factory { PlayQuizViewModel(get(), get()) }
}
