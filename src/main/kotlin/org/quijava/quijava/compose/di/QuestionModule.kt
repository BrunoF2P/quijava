package org.quijava.quijava.compose.di

import org.koin.dsl.module
import org.quijava.quijava.compose.viewmodels.CreateQuestionViewModel
import org.quijava.quijava.services.ImageService
import org.quijava.quijava.services.QuestionService
import org.springframework.context.ConfigurableApplicationContext

fun questionModule(springContext: ConfigurableApplicationContext) = module {
    // Services
    single { springContext.getBean(QuestionService::class.java) }
    single { springContext.getBean(ImageService::class.java) }
    
    // ViewModels
    factory { CreateQuestionViewModel(get(), get()) }
}
