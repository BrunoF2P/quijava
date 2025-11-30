package org.quijava.quijava.compose.di

import org.koin.dsl.module
import org.quijava.quijava.compose.viewmodels.*
import org.quijava.quijava.services.*
import org.springframework.context.ConfigurableApplicationContext

fun appModule(springContext: ConfigurableApplicationContext) = module {
    // Spring Services
    single { springContext.getBean(QuizService::class.java) }
    single { springContext.getBean(QuestionService::class.java) }
    single { springContext.getBean(LoginService::class.java) }
    single { springContext.getBean(RegisterService::class.java) }
    single { springContext.getBean(CategoryService::class.java) }
    single { springContext.getBean(ImageService::class.java) }
    single { springContext.getBean(RankService::class.java) }
    
    // ViewModels - using factory for new instance each time
    factory { PlayQuizViewModel(get(), get()) }
    factory { CreateQuestionViewModel(get(), get()) }
    factory { CreateQuizViewModel(get(), get(), get()) }
    factory { LoginViewModel(get()) }
    factory { RegisterViewModel(get()) }
    factory { EditQuizViewModel(get(), get(), get()) }
}
